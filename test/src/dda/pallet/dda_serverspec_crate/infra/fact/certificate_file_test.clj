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

(ns dda.pallet.dda-serverspec-crate.infra.fact.certificate-file-test
  (:require
    [clojure.test :refer :all]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.fact.certificate-file :as sut]))

; ------------------------  test data  ------------------------
(def output1
  "_somedir_cert.pem
123
")

(def fact1 {:_somedir_cert.pem {:expiration-days 123}})

(def output2
  (str
    "_somedir_cert.pem\n123\n"
    sut/output-separator
    "_someotherdir_cert2.pem\n789\n"
    sut/output-separator))

(def fact2 {:_somedir_cert.pem {:expiration-days 123}
            :_someotherdir_cert2.pem {:expiration-days 789}})

(def output3
  "_somedir_cert3.pem\nError opening Certificate cert3.pem
139973823276696:error:02001002:system library:fopen:No such file or directory:bss_file.c:398:fopen('ccert.pem','r')
139973823276696:error:20074002:BIO routines:FILE_CTRL:system lib:bss_file.c:400:
unable to load certificate")

(def fact3 {:_somedir_cert3.pem {:expiration-days -1}})

; ------------------------  tests  ------------------------------
(deftest test-parse
  (testing
    "test parsing certificate check output"
    (is (= fact1
           (sut/parse-certificate-script-responses output1)))
    (is (= fact2
           (sut/parse-certificate-script-responses output2)))
    (is (= fact3
           (sut/parse-certificate-script-responses output3)))))
