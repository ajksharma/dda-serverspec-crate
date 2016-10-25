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
(ns org.domaindrivenarchitecture.pallet.crate.instantiate-existing-init
  (:require
      [org.domaindrivenarchitecture.pallet.serverstate :as serverstate])
  (:gen-class :main true))
 
(require '(pallet.api))
(require '(pallet.compute))
(require '(pallet.compute.node-list))
(require '[org.domaindrivenarchitecture.pallet.commons.session-tools :as st])

(def mygroup
  (pallet.api/group-spec
    "mygroup" :extends [serverstate/with-serverstate]))
(def localhost-node
  (pallet.compute.node-list/make-node 
    "localhost-node" "mygroup" "127.1.1.1" :ubuntu :id :localhost-node))
(def node-list
  (pallet.compute/instantiate-provider
    "node-list" :node-list [localhost-node]))

(defn -main [outpath]
  (st/emit-xml-to-file 
    outpath
    (st/explain-session-xml                   
      (pallet.api/lift
        mygroup
        :user (pallet.api/make-user "pallet")
        :compute node-list
        :phase '(:settings :test)))))