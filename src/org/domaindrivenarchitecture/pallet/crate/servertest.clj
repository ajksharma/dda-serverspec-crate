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

(ns org.domaindrivenarchitecture.pallet.servertest
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.apt :as apt-tests]
    [org.domaindrivenarchitecture.pallet.servertest.ports :as ports-test]
    [org.domaindrivenarchitecture.pallet.servertest.processes :as processes-test]
    [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]))

(def facility :dda-servertest)

(def ServertestCrate 
  (dda-crate/make-dda-crate
    :facility facility
    :version [0 1 0]))

(defmethod dda-crate/dda-test facility [dda-crate config]
  (apt-tests/define-resources-apt)
  (ports-test/define-resources-netstat)
  (processes-test/define-resources-ps))

(def with-servertest (dda-crate/create-server-spec ServertestCrate))