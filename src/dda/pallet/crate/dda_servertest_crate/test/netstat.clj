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

(ns dda.pallet.crate.dda-servertest-crate.test.netstat
  (:require
    [schema.core :as s]
    [dda.pallet.crate.dda-servertest-crate.fact.netstat :as netstat-fact]
    [dda.pallet.crate.dda-servertest-crate.core.test :as server-test]))

(def NetstatTestConfig {s/Keyword {:port s/Num}})

(s/defn fact-check :- server-test/FactCheckResult
  [result :- server-test/FactCheckResult
   spec :- NetstatTestConfig
   considered-map]
  (if (<= (count spec) 0)
    result
    (let [elem (first spec)
          expected-on-port (:port (val elem))
          present-elem  (get-in considered-map [(key elem)])
          passed? (and (some? present-elem)
                       (= (:state present-elem)) "LISTEN"
                       (some? (re-matches
                                (re-pattern (str ".+:" expected-on-port))
                                (:local-address present-elem))))]
        (recur
          {:test-passed (and (:test-passed result) passed?)
           :test-message (str (:test-message result) "test netstat: " (name (key elem))
                              ", expected port: " expected-on-port ", was running?: "
                              (some? present-elem) ", was listening on: "
                              (:local-address present-elem) ", passed?: " passed? "\\n")
           :no-passed (if passed? (inc (:no-passed result)) (:no-passed result))
           :no-failed (if (not passed?) (inc (:no-failed result)) (:no-failed result))}
          (rest spec)
          considered-map))))

(s/defn result-to-map
  [input :- (seq netstat-fact/NetstatResult)]
  (apply merge (map (fn [e] {(keyword (:process-name e)) e}) input)))

(s/defn test-netstat-internal :- server-test/TestResult
  [test-config :- NetstatTestConfig
   input :- (seq netstat-fact/NetstatResult)]
  (let [considered-map (result-to-map input)
        fact-result (fact-check server-test/fact-check-seed test-config considered-map)]
    (server-test/fact-result-to-test-result input fact-result)))

(s/defn test-netstat :- server-test/TestActionResult
  [test-config :- NetstatTestConfig]
  (server-test/test-it
    netstat-fact/fact-id-netstat
    #(test-netstat-internal test-config %)))
