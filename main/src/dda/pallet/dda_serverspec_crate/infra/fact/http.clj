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

(ns dda.pallet.dda-serverspec-crate.infra.fact.http
  (:require
   [schema.core :as s]
   [clojure.string :as string]
   [dda.pallet.dda-serverspec-crate.infra.core.fact :refer :all]))

; -----------------------  fields & schemas  ------------------------
(def fact-id-http ::http)

(def HttpFactConfig
  {s/Keyword {:url s/Str}})         ;e.g. http://google.com

(def HttpFactResult {:expiration-days s/Num})    ;value -1 in case of error

(def HttpFactResults {s/Keyword HttpFactResult})

(def output-separator "----- http output separator -----\n")

; -----------------------  functions  -------------------------------
(s/defn url-to-keyword :- s/Keyword
  "creates a keyword from the url"
  [url :- s/Str] (keyword (clojure.string/replace url #"[:/]" "_")))

(s/defn build-http-script
  "builds the script to retrieve the http response of the url"
  [http-config :- HttpFactConfig]
  (let [config-val (val http-config)
        config-key (key http-config)
        {:keys [file]} config-val]
    (str
      "echo '" (name config-key) "';"
      "echo $(( ( $(date --date=\"$(openssl x509 -in "
      file
      " -noout -enddate | cut -d= -f 2)\" \"+%s\") - $(date \"+%s\") ) / 86400));"
      "echo -n '" output-separator "'")))

(s/defn parse-http-response :- HttpFactResult
  "returns a HttpFactResult from the result text of one http check"
  [single-script-result]
  (let [result-lines (string/split single-script-result #"\n")
        result-key (first result-lines)
        result-text (nth result-lines 1)
        ;convert to number or nil:
        result-number (re-find #"^\d+$" result-text)]
    (if result-number
      {(keyword result-key) {:expiration-days (Integer. result-number)}}
      {(keyword result-key) {:expiration-days -1}})))

(defn parse-http-script-responses
  "returns a HttpFactResult from the result text of one http check"
  [raw-script-results]
  (apply merge
    (map parse-http-response (clojure.string/split raw-script-results (re-pattern output-separator)))))

(s/defn collect-http-fact
  "Collects the facts for all http checks by executing the script and parsing the results"
  [fact-config :- HttpFactConfig]
  (collect-fact
    fact-id-http
    (str
      (clojure.string/join
       "; " (map #(build-http-script %) fact-config))
      "; exit 0")
    :transform-fn parse-http-script-responses))
