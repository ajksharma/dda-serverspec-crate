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

(ns dda.pallet.dda-serverspec-crate.infra.fact.netcat-test
  (:require
   [clojure.test :refer :all]
   [pallet.build-actions :as build-actions]
   [pallet.actions :as actions]
   [dda.pallet.dda-serverspec-crate.infra.fact.netcat :as sut]))

(def netcat-google "www.google.com_80_1,0")

(def netcat-yahoo "www.yahoo.org_80_1,1")

(def netcat-bing "www.bing.org_80_1,2")

(def some-result
  "www.google.com_80_1,0
  www.yahoo.org_80_1,1
  www.bing.org_80_1,2
")

(def empty-result
  "
")

(deftest test-parse-line
  (testing
   (is (:reachable? (val (first (sut/parse-result netcat-google)))))
    (is (not (:reachable? (val (first (sut/parse-result netcat-yahoo ))))))
    (is (= "www.bing.org_80_1" (name (key (first (sut/parse-result netcat-bing))))))))

(deftest test-parse
  (testing
   "test parsing ls output"
    (is (= 3
           (count (keys (sut/parse-netcat some-result)))))
    (is (= 0
           (count (keys (sut/parse-netcat empty-result)))))))
