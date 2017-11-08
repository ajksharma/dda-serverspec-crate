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

(ns dda.pallet.dda-serverspec-crate.infra.test.package-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.test.package :as sut]))


(def test-config {:missing {:installed? true}
                  :atom {:installed? true}
                  :firefox {:installed? false}})

(def input
  '({:state "rc"
     :package "firefox"
     :version "0.6.40-2ubuntu11.3"
     :arch "amd64"
     :desc "xxx"}
    {:state "ii"
     :package "atom"
     :version "0.6.40-2ubuntu11.3"
     :arch "amd64"
     :desc "xxx"}
    {:state "ii"
     :package "accountsservice"
     :version "0.6.40-2ubuntu11.3"
     :arch "amd64"
     :desc "query and manipulate user account information"}))

(deftest test-package-internal
  (testing
    "test test-package-internal"
    (is (= 1
           (:no-failed (sut/test-package-internal test-config input))))))
