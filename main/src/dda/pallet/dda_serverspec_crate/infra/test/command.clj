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

(ns dda.pallet.dda-serverspec-crate.infra.test.command
  (:require
    [schema.core :as s]
    [dda.pallet.dda-serverspec-crate.infra.fact.command :as command-fact]
    [dda.pallet.dda-serverspec-crate.infra.core.test :as server-test]))

(def CommandTestConfig {s/Keyword {:exit-code s/Num}})

(s/defn fact-check :- server-test/TestResult
  "Compare facts & expectation in order to return test-results."
  [result :- server-test/TestResult
   spec :- CommandTestConfig
   fact-map]
  (if (<= (count spec) 0)
    result
    (let [elem (first spec)
          expected-exit-code (:exit-code (val elem))
          fact-elem (get-in fact-map [(key elem)])
          fact-exit-code (:exit-code fact-elem)
          passed? (= expected-exit-code fact-exit-code)]
      (recur
        {:test-passed (and (:test-passed result) passed?)
         :test-message (str (:test-message result) "test command: " (name (key elem))
                            ", expected:: exit-code: " expected-exit-code
                            " - found facts:: exit-code: " fact-exit-code
                            " - passed?: " passed? "\n")
         :no-passed (if passed? (inc (:no-passed result)) (:no-passed result))
         :no-failed (if (not passed?) (inc (:no-failed result)) (:no-failed result))}
        (rest spec)
        fact-map))))

(s/defn test-command-internal :- server-test/TestResultHuman
  [test-config :- CommandTestConfig
   input :- {s/Keyword command-fact/CommandFactResults}]
  (let [fact-result (fact-check server-test/fact-check-seed test-config input)]
    (server-test/fact-result-to-test-result input fact-result)))

(s/defn test-command :- server-test/TestActionResult
  [test-config :- CommandTestConfig]
  (server-test/test-it
    command-fact/fact-id-command
    #(test-command-internal test-config %)))
