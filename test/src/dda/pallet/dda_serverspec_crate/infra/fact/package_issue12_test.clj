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


(ns dda.pallet.dda-serverspec-crate.infra.fact.package-test
  (:require
    [clojure.test :refer :all]
    [dda.pallet.dda-serverspec-crate.infra.fact.package :as sut]))


(def package-resource "Desired=Unknown/Install/Remove/Purge/Hold
| Status=Not/Inst/Conf-files/Unpacked/halF-conf/Half-inst/trig-aWait/Trig-pend
|/ Err?=(none)/Reinst-required (Status,Err: uppercase=bad)
||/ Name                                  Version                                  Architecture Description
+++-=====================================-========================================-============-===============================================================================
ii  openjdk-8-jdk: 8u171-b11-0u amd64        OpenJDK Development Kit (JDK)
ii  openjdk-8-jdk- 8u171-b11-0u amd64        OpenJDK Development Kit (JDK) (he
ii  openjdk-8-jre: 8u171-b11-0u amd64        OpenJDK Java runtime, using Hotsp
ii  openjdk-8-jre- 8u171-b11-0u amd64        OpenJDK Java runtime, using Hotsp
")

(deftest test-parse
  (testing
    "test parsing packages-output"
      (is (= "openjdk-8-jdk"
             (:package
               (first (sut/parse-package package-resource)))))))
