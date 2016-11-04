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

(ns org.domaindrivenarchitecture.pallet.servertest.apt
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.resources :refer :all]
    [org.domaindrivenarchitecture.pallet.servertest.tests :refer :all]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :as lib]))

(def res-key-apt-packages ::apt-packages)
(def res-key-apt-repositories ::apt-repositories)

(defn define-resources-apt
  "Defines the ospackages resource. 
   This is automatically done serverstate crate is used."
  []
  (define-resource-from-script res-key-apt-packages 
    "apt list --installed")
  (define-resource-from-script res-key-apt-repositories 
    "egrep -v '^#|^ *$' /etc/apt/sources.list /etc/apt/sources.list.d/*"))

(script/defscript script-test-package-installed
  "Tests if a package is installed. Prints the version if installed and fails
   if package is not installed."
  [package])
(script/defimpl script-test-package-installed :default [package]
  (if ("grep" ~(str "^" package "/*"))
    (lib/exit 0)
    (do
      (println "Package not installed, test FAILED.")
      (lib/exit 1))))
(defn test-package-installed
  "Tests if a file matches a regular expression."
  [package]
  (testnode-resource res-key-apt-packages (script-test-package-installed package)))

(script/defscript script-test-package-not-installed
  "Tests if a package is NOT installed. Prints the version if installed and 
   fails if package is installed."
  [package])
(script/defimpl script-test-package-not-installed :default [package]
  (if (= 0 ("grep -c" ~(str "^" package "/*")))
    (do
      (println "Package not installed, test PASSED.")
      (lib/exit 0))
    (do
      ("grep" ~(str "^" package "/*"))
      (println "Package installed, test FAILED.")
      (lib/exit 1))))
(defn test-package-not-installed
  "Tests if a file matches a regular expression."
  [package]
  (testnode-resource res-key-apt-packages (script-test-package-not-installed package)))

