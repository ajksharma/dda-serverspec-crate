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

(ns dda.pallet.dda-serverspec-crate.infra.fact.certificate
  (:require
   [schema.core :as s]
   [clojure.string :as string]
   [dda.pallet.dda-serverspec-crate.infra.core.fact :refer :all]))

; -----------------------  fields & schemas  ------------------------
(def fact-id-certificate ::certificate)

(def CertificateConfig
  {s/Keyword
   {:file-name s/Str         ;with full path
    :expiration-days s/Num}})   ;TODO not above 1000000 (a million)

(def CertificateResult {:valid? s/Bool
                        :message s/Str})

(def CertificateResults {s/Keyword CertificateResult})

; -----------------------  functions  -------------------------------
(s/defn build-certificate-script
  "builds the script to check the certificate from the given config"
  [certificate-config :- CertificateConfig]
  (let [config-val (val certificate-config)
        {:keys [file-name expiration-days]} config-val]
    (str
     "openssl x509 -checkend "
     (* 86400 (- expiration-days 1))
     " -in "
     file-name
     "; echo $?")))

(s/defn parse-certificate :- CertificateResult
  "returns a CertificateResult from the result text of one certificate check"
  [script-result]
  (let [result-vec (string/split script-result #"\n")]
    {:valid? (= "0" (peek result-vec))
     :message (clojure.string/join(pop result-vec))}))

(defn collect-certificate-fact
  "Defines the certificate-check resource."
  []
  (collect-fact fact-id-certificate '("openssl" "x509") :transform-fn parse-certificate))
