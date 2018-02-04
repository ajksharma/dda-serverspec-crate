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

(ns dda.pallet.dda-serverspec-crate.infra.fact.certificate-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.fact.certificate :as sut]))

; ------------------------  test data  ------------------------
(def non-expiring-result
  "Certificate will not expire
0
")

(def expiring-result
  "Certificate will expire
1
")

(def certificate-not-found-result
  "Error opening Certificate ccert.pem
139973823276696:error:02001002:system library:fopen:No such file or directory:bss_file.c:398:fopen('ccert.pem','r')
139973823276696:error:20074002:BIO routines:FILE_CTRL:system lib:bss_file.c:400:
unable to load certificate
1
")

; ------------------------  tests  ------------------------------
(deftest test-parse
  (testing
    "test parsing certificate check output"
      (is (= true
             (:still-valid?
               (sut/parse-certificate non-expiring-result))))
      (is (= false
             (:still-valid?
               (sut/parse-certificate expiring-result))))
      (is (= false
             (:still-valid?
               (sut/parse-certificate certificate-not-found-result))))))
