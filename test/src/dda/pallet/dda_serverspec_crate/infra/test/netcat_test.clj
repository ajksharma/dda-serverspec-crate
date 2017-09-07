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

(ns dda.pallet.dda-serverspec-crate.infra.test.netcat-test
  (:require
   [clojure.test :refer :all]
   [pallet.build-actions :as build-actions]
   [pallet.actions :as actions]
   [dda.pallet.dda-serverspec-crate.infra.test.netcat :as sut]))

(def netcat-yahgo-1
  {:www.google.com_80_1 {:reachable? true}
   :www.yahoo.org_80_1 {:reachable? true}})

(def netcat-yahgo-2
  {:www.google.com_80_1 {:reachable? true}
   :www.yahoo.org_80_1 {:reachable? false}})

(def netcat-yahgo-3
  {:www.google.com_80_1 {:reachable? false}
   :www.yahoo.org_80_1 {:reachable? false}})

(def netcat-yahgo-4
  {:www.google.com_80_1 {:reachable? false}
   :www.yahoo.org_80_1 {:reachable? true}})

(def input
  {:www.google.com_80_1 {:reachable? true}
   :www.yahoo.org_80_1 {:reachable? true}})

(deftest test-netcat-internal
  (testing
   "test test-netcat-internal"
    (is (= 0
           (:no-failed (sut/test-netcat-internal {} input))))
    (is (= 0
           (:no-failed (sut/test-netcat-internal netcat-yahgo-1 input))))
    (is (= 1
           (:no-failed (sut/test-netcat-internal netcat-yahgo-2 input))))
    (is (= 2
           (:no-failed (sut/test-netcat-internal netcat-yahgo-3 input))))
    (is (= 1
           (:no-failed (sut/test-netcat-internal netcat-yahgo-4 input))))))
