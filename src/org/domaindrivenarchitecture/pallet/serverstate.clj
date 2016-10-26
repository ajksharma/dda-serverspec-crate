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

(ns org.domaindrivenarchitecture.pallet.serverstate
  (:require
    [org.domaindrivenarchitecture.pallet.serverstate.resources :as res]
    [org.domaindrivenarchitecture.pallet.serverstate.tests :as tests]
    [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]
    [pallet.stevedore :refer :all]
    [pallet.script.lib :as lib]))

(def facility :dda-serverstate)

(def ServerstateCrate 
  (dda-crate/make-dda-crate
    :facility facility
    :version [0 1 0]))


(defn transform-user-list
  [output] 
  (filter #(= 2 (count %)) 
    (map #(clojure.string/split % #":") (clojure.string/split-lines output))))

(defn test-palletuser-existing
  "Tests if there is a pallet user in the user-list. Here user-list must be
   a vector of entries where every entry is a vector containing username and
   homefolder as string.
   
   e.g: [ [\"root\" \"/root\"] ... ] "
  [user-list]
  (println "Testing if user pallet exists.")
  (= 1 (count (filter #(= (first %) "pallet") user-list))))



(defmethod dda-crate/dda-test facility [dda-crate config]
  (res/define-resource-from-script 
    ::user-list 
    "cut -d: -f1,6 /etc/passwd" 
    :transform-fn transform-user-list)
  (tests/testclj-resource 
    ::user-list
    test-palletuser-existing)
(tests/testnode-resource
  ::user-list
  (script
   (set! exitcode 0)
   (while 
     ("read" line)
     (set! user @((pipe (println @line) ("cut -f1 -d:")))) 
     (set! homedir @((pipe (println @line) ("cut -f2 -d:"))))
     (if (not (directory? @homedir))
       (do
         (println "Home" @homedir "of user" @user "does not exist!")
         (set! exitcode 1))))
   ("exit" @exitcode)))
  
  )

(def with-serverstate (dda-crate/create-server-spec ServerstateCrate))