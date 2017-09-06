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
(ns dda.pallet.dda-serverspec-crate.infra
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.api :as api]
    [pallet.actions :as actions]
    [pallet.crate :as crate]
    [dda.pallet.core.dda-crate :as dda-crate]
    [dda.pallet.dda-serverspec-crate.infra.fact.package :as package-fact]
    [dda.pallet.dda-serverspec-crate.infra.fact.netstat :as netstat-fact]
    [dda.pallet.dda-serverspec-crate.infra.fact.file :as file-fact]
    [dda.pallet.dda-serverspec-crate.infra.test.package :as package-test]
    [dda.pallet.dda-serverspec-crate.infra.test.netstat :as netstat-test]
    [dda.pallet.dda-serverspec-crate.infra.test.file :as file-test]))

(def facility :dda-servertest)
(def version  [0 1 0])

(def ServerTestConfig
  {(s/optional-key :package-fact) s/Any
   (s/optional-key :netstat-fact) s/Any
   (s/optional-key :file-fact) file-fact/FileFactConfig
   (s/optional-key :package-test) package-test/PackageTestConfig
   (s/optional-key :netstat-test) netstat-test/NetstatTestConfig
   (s/optional-key :file-test) file-test/FileTestConfig})

(s/defn ^:always-validate path-to-keyword :- s/Keyword
  [path :- s/Str]
  (file-fact/path-to-keyword path))

(s/defmethod dda-crate/dda-settings facility
  [dda-crate config]
  "dda-servertest: setting"
  (let [{:keys [file-fact]} config]
    (when (contains? config :package-fact)
      (package-fact/collect-package-fact))
    (when (contains? config :netstat-fact)
      (netstat-fact/collect-netstat-fact))
    (when (contains? config :file-fact)
      (file-fact/collect-file-fact file-fact))))

(s/defmethod dda-crate/dda-test facility
  [dda-crate config]
  (let [{:keys [file-facts]} config]
    (when (contains? config :package-test)
      (package-test/test-package (:package-test config)))
    (when (contains? config :netstat-test)
        (netstat-test/test-netstat (:netstat-test config)))
    (when (contains? config :file-test)
        (file-test/test-file (:file-test config)))))

(def dda-serverspec-crate
  (dda-crate/make-dda-crate
   :facility facility
   :version version))

(def with-serverspec
  (dda-crate/create-server-spec dda-serverspec-crate))
