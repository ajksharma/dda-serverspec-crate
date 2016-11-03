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
(ns org.domaindrivenarchitecture.pallet.crate.run-on-pallet-node
  (:require
    [org.domaindrivenarchitecture.pallet.serverstate :as serverstate]
    [org.domaindrivenarchitecture.pallet.serverstate.resources :as res]
    [org.domaindrivenarchitecture.pallet.serverstate.tests :as tests]
    [org.domaindrivenarchitecture.pallet.serverstate.apt :as apt-tests]
    [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]
    [org.domaindrivenarchitecture.pallet.serverstate.ports :as ports-test]
    [pallet.stevedore :refer :all]
    [pallet.script.lib :as lib])
  (:gen-class :main true))

(def facility :dda-serverstate-test)

(def ServerstateTestCrate 
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

(defn test-pallet-listening-on-port-80
  "Tests if ports are listened."
  [netstat-output-from-resource]
  (= 1 (count (filter #(= (first %) "80") user-list))))

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
  
  (apt-tests/test-package-installed "cowsay")
  (ports-test/test-port-open-on-process 53 "dnsmasq")
  (ports-test/test-port-open 42))

(def with-serverstate-test (dda-crate/create-server-spec ServerstateTestCrate))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '(pallet.api))
(require '(pallet.compute))
(require '(pallet.compute.node-list))
(require '[clojure.inspector :as inspector])
(require '[org.domaindrivenarchitecture.pallet.commons.session-tools :as st])

(def mygroup
  (pallet.api/group-spec
    "mygroup" :extends [serverstate/with-serverstate with-serverstate-test]))
(def localhost-node
  (pallet.compute.node-list/make-node 
    "localhost-node" "mygroup" "127.1.1.1" :ubuntu :id :localhost-node))
(def node-list
  (pallet.compute/instantiate-provider
    "node-list" :node-list [localhost-node]))

(defn -main []
  (let [session (pallet.api/lift
                  mygroup
                  :user (pallet.api/make-user "pallet")
                  :compute node-list
                  :phase '(:settings :test))]
  (inspector/inspect-tree 
    {:session session
     :test-result (tests/test-result session)})))