; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns org.domaindrivenarchitecture.pallet.serverstate.resources  
  (:require
    [pallet.core.session :as session]
    [pallet.crate :as crate]
    [pallet.actions :as actions]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :refer :all]))

(defn- create-resource-timestamp
  "Creates a timestamp to create state directories."
  []
  (.format (java.text.SimpleDateFormat. "yyyyMMdd-HHmmss.SSS") (new java.util.Date)))

(def resource-folder-path-base
  "/home/pallet/state/resources-")

(defn- resource-folder-path
   "Creates the resource folder and sets its path to the settings if no
   path is set yet."
  []
  (let [settings-resource-path (-> (crate/get-settings :dda-pallet-commons) :resource-path)]
    (if settings-resource-path
      settings-resource-path
      (do
        (let [create-resource-path (str resource-folder-path-base (create-resource-timestamp))]
        (crate/assoc-settings :dda-pallet-commons {:resource-path create-resource-path})
        (actions/directory 
          create-resource-path
          :owner "pallet" :group "pallet" :mode "700")
        (actions/file
          (str resource-folder-path-base "current")
          :action :delete)  
        (actions/symbolic-link
          create-resource-path
          (str resource-folder-path-base "current")
          :owner "pallet" :group "pallet" :mode "700")         
        create-resource-path))
      )))

(defn- resource-file-path
  "Path to the resource file of a given res-id."
  [res-id]
  (str (resource-folder-path) "/" res-id ".rc"))

(defn- resource-script-path
  "Path to the script for creation of the resource file of a given res-id."
  [res-id]
  (str (resource-folder-path) "/" res-id ".sh"))

(script/defscript script-run-resource 
  "This script (explained in stevedore) runs the resource script and copys
   the output to stdout (for the result in the session) and to the resource
   file on the remote machine.

   It fails if the script fails or the resource file cannot be created."
  [res-id])
(script/defimpl script-run-resource :default [res-id]
  ("set -o pipefail")
  (if
    (pipe 
      (resource-script-path ~res-id) 
      ("tee" (resource-file-path ~res-id)))
    (do
      (println "[Resource created successful]")
      (exit 0))
    (do
      (println "[Resource creation failed]")
      (exit 1))))

;;; Defining Resources

(defn define-resource-from-script
  "Defines a resource as output from an arbitry script. This fails if the
   script fails (exitcode <> 0) or the resource file cannot be created."
  [res-id script]
  (actions/remote-file 
    (resource-script-path res-id)
    :content script :owner "pallet" :group "pallet" :mode "700")    
  (actions/file 
    (resource-file-path res-id)
    :owner "pallet" :group "pallet" :mode "600")    
  (actions/exec-script
    (script-run-resource ~res-id)))

(defn define-resource-from-file
  "Defines a remote file as a resource. This fails if the file does not exist
   or the resource file cannot be created."
  [res-id file]
  (define-resource-from-script res-id 
    (str "cat " file)))


;;; Test for checking defined resources

(defn test-script
  "Runs a script that receives the resource in stdin. The provided script 
   should:
    * Exit with code 0 iff the test is passed and exit with any other
      code on failure.
    * Provide a human-readable test result (like reasons for failure)
      on stdout."
  [res-id script]
  (actions/exec-script
    (println "Testing resource" ~res-id)
    (defn testscript [] ~script)
    (pipe
      ("cat" (resource-file-path ~res-id))
      ("testscript"))))
  
(script/defscript script-test-not-empty
  "Tests if input from stdin has positive byte count. If used with :strip true
   option whitespaces, \n and \r are ignored in the count."
  [& {:keys [strip] :or {strip false}}])
(script/defimpl script-test-not-empty :default [& {:keys [strip] :or {strip false}}]
  (if (= 0 @(~(if strip 
                "tr -d \" \\r\\n\" | wc -c | cut -d' ' -f1" 
                "wc -c | cut -d' ' -f1")))
    (do 
      (println "FAIL:" ~(if strip "stripped" "raw") "file is empty")
      (exit 1))
    (do 
      (println "PASS:" ~(if strip "stripped" "raw") "file has content")
      (exit 0))))
(defn test-not-empty
  "Tests if a created resource is not empty (=success) or is empty (=failure)"
  [res-id & {:keys [strip] :or {strip false}}]
  (test-script res-id (script-test-not-empty :strip strip)))

(script/defscript script-test-match-regexp
  "Prints all matching lines and has successful exit code if at least one line
   matches."
  [regexp])
(script/defimpl script-test-match-regexp :default [regexp]
  (println "All matches for regexp ,," ~regexp "`` using grep:")
  (if ("grep" ~regexp)
    (exit 0)
    (do
      (println "No matches found, test FAILED.")
      (exit 1))))
(defn test-match-regex
  "Tests if a file matches a regular expression."
  [res-id regex]
  (test-script res-id (script-test-match-regexp regex)))

;;; Predefined resource: apt package manager

(def res-id-apt-packages "dda-serverstate_apt-packages")
(def res-id-apt-repositories "dda-serverstate_apt-repositories")

(defn define-resources-apt
  "Defines the ospackages resource. 
   This is automatically done serverstate crate is used."
  []
  (define-resource-from-script res-id-apt-packages 
    "apt list --installed")
  (define-resource-from-script res-id-apt-repositories 
    "egrep -v '^#|^ *$' /etc/apt/sources.list /etc/apt/sources.list.d/*"))

(script/defscript script-test-package-installed
  "Tests if a package is installed. Prints the version if installed and fails
   if package is not installed."
  [package])
(script/defimpl script-test-package-installed :default [package]
  (if ("grep" ~(str "^" package "/*"))
    (exit 0)
    (do
      (println "Package not installed, test FAILED.")
      (exit 1))))
(defn test-package-installed
  "Tests if a file matches a regular expression."
  [package]
  (test-script res-id-apt-packages (script-test-package-installed package)))
test
(script/defscript script-test-package-not-installed
  "Tests if a package is NOT installed. Prints the version if installed and 
   fails if package is installed."
  [package])
(script/defimpl script-test-package-not-installed :default [package]
  (if (= 0 ("grep -c" ~(str "^" package "/*")))
    (do
      (println "Package not installed, test PASSED.")
      (exit 0))
    (do
      ("grep" ~(str "^" package "/*"))
      (println "Package installed, test FAILED.")
      (exit 1))))
(defn test-package-not-installed
  "Tests if a file matches a regular expression."
  [package]
  (test-script res-id-apt-packages (script-test-package-not-installed package)))


;;; Predefined resource: open ports
(def res-id-open-ports "dda-serverstate_open-ports")
(defn define-resources-netstat
  "Defines the netstat resource. 
   This is automatically done serverstate crate is used."
  []
  (define-resource-from-script res-id-open-ports "netstat -tulpen"))

(script/defscript script-test-port-open
  "Checks if a port is open."
  [port-number])
(script/defimpl script-test-port-open :default 
  [port-number]
  (if ("grep " (str ":" ~port-number))
    (do 
      (println (str "The port " ~port-number " is open"))
      (exit 0))
    (do 
      (println (str "The port  " ~port-number " is not open."))
      (exit 1))))
(defn test-port-open
  "Tests if a created resource is not empty (=success) or is empty (=failure)"
  [port-number]
  (test-script  res-id-open-ports (script-test-port-open port-number)))

(script/defscript script-test-port-open
  "Checks if a port is open on a specified process-name."
  [port-number process-name])
(script/defimpl script-test-port-open :default 
  [port-number process-name]
  (if (pipe ("grep " (str ":" ~port-number)) ("grep " ~process-name))
    (do 
      (println (str "The port " ~port-number " is open on process " ~process-name))
      (exit 0))
    (do 
      (println (str "The port  " ~port-number " is not open on process " ~process-name))
      (exit 1))))
(defn test-port-open
  "Tests if a created resource is not empty (=success) or is empty (=failure)"
  [port-number process-name]
  (test-script  res-id-open-ports (script-test-port-open port-number process-name)))



;;; Predefined resource: running processes
(def res-id-running-processes "dda-serverstate_running-processes")
;test if a process is running
(defn define-resources-ps
  "Defines the ospackages resource. 
   This is automatically done serverstate crate is used."
  []
  (define-resource-from-script res-id-running-processes "ps -ef"))

(script/defscript script-test-process-running
  "Checks if a process is running."
  [process-name])
(script/defimpl script-test-process-running :default 
  [process-name]
  (if ("pgrep " ~process-name)
    (do 
      (println (str "The process " ~process-name " is running"))
      (exit 0))
    (do 
      (println (str "The process " ~process-name " is currently not running"))
      (exit 1))))

(defn test-process-running
  "Tests if a created resource is not empty (=success) or is empty (=failure)"
  [process-name]
  (test-script  res-id-running-processes (script-test-process-running process-name)))