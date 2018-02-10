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

(ns dda.pallet.dda-serverspec-crate.infra.fact.http-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.fact.http :as sut]))

; ------------------------  test data  ------------------------
(def output1
  "_someurl
notAfter=Apr 17 13:36:00 2022 GMT
")

(def fact1 {:_someurl {:expiration-days 1527}})

(def output2
  (str
    "_someurl\nnotAfter=Apr 17 13:36:00 2022 GMT\n"
    sut/output-separator
    "_some_other_url\nnotAfter=Apr 17 13:36:00 2017 GMT\n"
    sut/output-separator))

(def fact2 {:_someurl {:expiration-days 1527}
            :_some_other_url {:expiration-days -299}})

(def output3
  "_someinvalidurl\nunable to load certificate
139913245157016:error:0906D06C:PEM routines:PEM_read_bio:no start line:pem_lib.c:701:Expecting: TRUSTED CERTIFICATE")

(def fact3 {:_someinvalidurl {:expiration-days -1}})

(def test-date
  (java.time.LocalDate/parse "01.01.2018"
    (java.time.format.DateTimeFormatter/ofPattern "dd.MM.yyyy")))

; ------------------------  tests  ------------------------------
(deftest test-parse
  (testing
    "test parsing http output"
    (is (= fact1
           (sut/parse-http-script-responses output1 test-date)))
    (is (= fact2
           (sut/parse-http-script-responses output2 test-date)))
    (is (= fact3
           (sut/parse-http-script-responses output3 test-date)))))
