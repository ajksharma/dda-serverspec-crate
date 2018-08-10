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

(ns dda.pallet.dda-serverspec-crate.infra.test.command-test
  (:require
    [clojure.test :refer :all]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.test.command :as sut]))

(def config1 {:echo--Hallo-World- {:exit-code 0
                                   :stdout "Hallo World"}})

(def config2 {:echo--Hallo-World- {:exit-code 0}})

(def config3 {:echo--Hallo-World- {:exit-code 0
                                   :stdout "HalloWorld"}})

(def output1 {:test-passed true,
              :test-message "test command: echo--Hallo-World-, expected:: exit-code: 0, stdout: Hallo World, found facts:: exit-code: 0, stdout: Hallo World - passed?: true\n",
              :no-passed 1, :no-failed 0,
              :input {:echo--Hallo-World- {:exit-code 0, :stdout "Hallo World"}},
              :summary "PASSED, tests failed: 0, tests passed: 1"})

(def output2 {:test-passed true,
              :test-message "test command: echo--Hallo-World-, expected:: exit-code: 0, found facts:: exit-code: 0 - passed?: true\n",
              :no-passed 1, :no-failed 0,
              :input {:echo--Hallo-World- {:exit-code 0}},
              :summary "PASSED, tests failed: 0, tests passed: 1"})

(def output3 {:test-passed false,
              :test-message "test command: echo--Hallo-World-, expected:: exit-code: 0, stdout: HalloWorld, found facts:: exit-code: 0, stdout: Hallo World - passed?: false\n",
              :no-passed 0, :no-failed 1,
              :input {:echo--Hallo-World- {:exit-code 0, :stdout "Hallo World"}},
              :summary "FAILED, tests failed: 1, tests passed: 0"})


(deftest test-command-internal
  (testing
    (is (= (sut/test-command-internal config1 config1) output1))
    (is (= (sut/test-command-internal config2 config2) output2))
    (is (= (sut/test-command-internal config3 config1) output3))))
