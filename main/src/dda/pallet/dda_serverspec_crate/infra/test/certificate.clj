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

(ns dda.pallet.dda-serverspec-crate.infra.test.certificate
  (:require
    [schema.core :as s]
    [dda.pallet.dda-serverspec-crate.infra.fact.certificate :as certificate-fact]
    [dda.pallet.dda-serverspec-crate.infra.core.test :as server-test]))

(def CertificateTestConfig {s/Keyword {:valid? s/Bool}})

(s/defn fact-check :- server-test/FactCheckResult
  "Compare facts & expectation in order to return test-results."
  [result :- server-test/FactCheckResult
   spec :- CertificateTestConfig
   fact-map]
  (if (<= (count spec) 0)
    result
    (let [elem (first spec)
          expected-valid? (:valid? (val elem))
          fact-elem  (get-in fact-map [(key elem)])
          fact-valid? (:valid? fact-elem)
          passed? (= expected-valid? fact-valid?)]
        (recur
          {:test-passed (and (:test-passed result) passed?)
           :test-message (str (:test-message result) "test certificate: " (name (key elem))
                              ", expected valid?: " expected-valid? ", was valid?: "
                              fact-valid? ", passed?: " passed? "\n")
           :no-passed (if passed? (inc (:no-passed result)) (:no-passed result))
           :no-failed (if (not passed?) (inc (:no-failed result)) (:no-failed result))}
          (rest spec)
          fact-map))))

(s/defn test-certificate-internal :- server-test/TestResult
  "Exposing fact input to signature for tests."
  [test-config :- CertificateTestConfig
   input :- {s/Keyword certificate-fact/CertificateResults}]
  (let [fact-result (fact-check server-test/fact-check-seed test-config input)]
    (server-test/fact-result-to-test-result input fact-result)))

(s/defn test-certificate :- server-test/TestActionResult
  "The delayed action to be called in test phase.
  Getting upfront filled facts from session."
  [test-config :- CertificateTestConfig]
  (server-test/test-it
    certificate-fact/fact-id-certificate
    #(test-certificate-internal test-config %)))
