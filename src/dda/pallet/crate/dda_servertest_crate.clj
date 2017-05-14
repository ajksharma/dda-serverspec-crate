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
(ns dda.pallet.crate.dda-servertest-crate
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.api :as api]
    [pallet.actions :as actions]
    [pallet.crate :as crate]
    [dda.pallet.core.dda-crate :as dda-crate]
    [dda.pallet.crate.dda-servertest-crate.fact.packages :as package-fact]
    [dda.pallet.crate.dda-servertest-crate.fact.netstat :as netstat-fact]
    [dda.pallet.crate.dda-servertest-crate.fact.file :as file-fact]
    [dda.pallet.crate.dda-servertest-crate.test.packages :as package-test]))

(def facility :dda-servertest)
(def version  [0 1 0])

(def ServerTestConfig
  {(s/optional-key :package-fact) s/Any
   (s/optional-key :netstat-fact) s/Any
   (s/optional-key :file-fact) [s/Str]
   (s/optional-key :package-test) {s/Keyword {:exist? s/Bool}}
   (s/optional-key :netstat-test) {s/Keyword {:exist? s/Bool
                                              :port s/Num}}
   (s/optional-key :file-test) {s/Keyword {:exist? s/Bool}}})

(s/defmethod dda-crate/dda-settings facility
  [dda-crate config]
  "dda-servertest: setting"
  (let [{:keys [file-facts]} config]
    (when (contains? config :package-fact)
      (package-fact/collect-packages-fact))
    (when (contains? config :netstat-fact)
      (netstat-fact/collect-netstat-fact))
    (when (contains? config :file-fact))))
      ;(file-fact/collect-file-fact file-facts))))

(s/defmethod dda-crate/dda-test facility
  [dda-crate config]
  (let [{:keys [file-facts]} config]
    (when (contains? :package-test config)
      ; todo: iterate over all packages to test
      (package-test/test-installed? "atom"))))

(def dda-servertest-crate
  (dda-crate/make-dda-crate
   :facility facility
   :version version))

(def with-servertest
  (dda-crate/create-server-spec dda-servertest-crate))
