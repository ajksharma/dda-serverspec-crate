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
    [org.domaindrivenarchitecture.pallet.serverstate.apt :as apt-tests]
    [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]))

(def facility :dda-serverstate)

(def ServerstateCrate 
  (dda-crate/make-dda-crate
    :facility facility
    :version [0 1 0]))

(defmethod dda-crate/dda-test facility [dda-crate config]
  (apt-tests/define-resources-apt))

(def with-serverstate (dda-crate/create-server-spec ServerstateCrate))