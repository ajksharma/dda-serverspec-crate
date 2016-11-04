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

(ns org.domaindrivenarchitecture.pallet.servertest.test.netstat
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.resources :refer :all]
    [org.domaindrivenarchitecture.pallet.servertest.scripts.core :refer :all]))

(defn parse-netstat
  [netstat-resource]
  (map #(zipmap [:proto :recv-q :send-q :local-adress :foreign-adress :state :user :inode :pid :program-name]
              (clojure.string/split % #"\s+|/"))
     (rest netstat-resource)))

(defn filter-listening-prog
  "filter for program ist listening."
  [netstat-line prog]
  (and (= (:state netstat-line) "LISTEN")
       (= (:program-name netstat-line) prog)))

(defn prog-listen?
  [netstat-resource prog]
  (some? (filter 
           #(filter-listening-prog % prog)
           (parse-netstat netstat-resource))))