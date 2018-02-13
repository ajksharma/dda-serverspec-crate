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

(ns dda.pallet.dda-serverspec-crate.infra.test.http-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.test.http :as sut]))

; -----------------------  test data  --------------------------
(def test-config-1 {:some_url {:expiration-days 10}
                    :another_url {:expiration-days 20}})

(def test-config-2 {:some_url {:expiration-days 20}
                    :another_url {:expiration-days 21}})

(def test-config-3 {:some_url {:expiration-days 30}
                    :another_url {:expiration-days 30}})

(def fact-result
  {:some_url {:expiration-days 20}
   :another_url {:expiration-days 20}})

; -----------------------  tests  --------------------------
(deftest test-http-internal
 (testing
   "test test-http-internal"
    (is (= 0
          (:no-failed (sut/test-http-internal {} fact-result))))
    (is (= 0
          (:no-failed (sut/test-http-internal test-config-1 fact-result))))
    (is (= 1
          (:no-failed (sut/test-http-internal test-config-2 fact-result))))
    (is (= 2
          (:no-failed (sut/test-http-internal test-config-3 fact-result))))))
