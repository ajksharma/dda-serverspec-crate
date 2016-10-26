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

(ns org.domaindrivenarchitecture.pallet.serverstate.processes
  (:require
    [org.domaindrivenarchitecture.pallet.serverstate.resources :refer :all]
    [org.domaindrivenarchitecture.pallet.serverstate.tests :refer :all]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :refer :all]))

;;; Predefined resource: running processes
(def res-id-running-processes ::running-processes)
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
  (testnode-resource res-id-running-processes (script-test-process-running process-name)))
