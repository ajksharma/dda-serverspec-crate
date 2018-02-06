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

(def CertificateFactConfig
  {s/Keyword {:file s/Str}})         ;with full path   TODO repl with file type??

(def CertificateFactResult {:expiration-days s/Num})

(def CertificateFactResults {s/Keyword CertificateFactResult})

; -----------------------  functions  -------------------------------
(s/defn build-certificate-script
  "builds the script to check the certificate from the given config"
  [certificate-config :- CertificateFactConfig]
  (let [config-val (val certificate-config)
        {:keys [file expiration-days]} config-val]
    (str
     "openssl x509 -checkend "
     (* 86400 (- expiration-days 1))
     " -in "
     file
     "; echo $?")))

(s/defn certificate-file-to-keyword :- s/Keyword
  [file :- s/Str] (keyword (clojure.string/replace file #"/" "_")))

(s/defn parse-certificate :- CertificateFactResult
  "returns a CertificateFactResult from the result text of one certificate check"
  [script-result]
  (let [result-vec (string/split script-result #"\n")]
    {:valid? (= "0" (peek result-vec))
     :message (clojure.string/join(pop result-vec))}))

(s/defn collect-certificate-fact
  "Defines the certificate-check resource."
  [fact-config :- CertificateFactConfig]
  (collect-fact fact-id-certificate [(build-certificate-script fact-config)]
                :transform-fn parse-certificate))
