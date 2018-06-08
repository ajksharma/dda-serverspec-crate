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


(ns dda.pallet.dda-serverspec-crate.infra.fact.file
  (:require
    [schema.core :as s]
    [dda.pallet.dda-serverspec-crate.infra.core.fact :refer :all]))

(def fact-id-file ::file)

(def FileFactConfig {s/Keyword {:path s/Str}})

(def FileFactResult {:path s/Str
                     :fact-exist? s/Bool
                     :fact-size-in-bytes s/Num
                     :fact-user s/Str
                     :fact-group s/Str
                     :fact-mod s/Str
                     :fact-type s/Str ;use keywords instead here?
                     :fact-created s/Str
                     :fact-modified s/Str
                     :fact-accessed s/Str})

(def FileFactResults {s/Keyword FileFactResult})

(s/defn path-to-keyword :- s/Keyword
  [path :- s/Str] (keyword (clojure.string/replace path #"/" "_")))

(s/defn clean-up-negative-find :- s/Str
  "extracts path from error message"
  [negative-find :- s/Str]
  (let [split-result (clojure.string/split negative-find #" ")
        unclean-path (nth split-result 1)]
    (subs unclean-path 1 (- (count unclean-path) 2))))

(s/defn parse-find-line :- FileFactResult
  [script-result-line :- s/Str]
  (let [match (not= (.indexOf script-result-line "find:") -1)
        split-string (clojure.string/split script-result-line #"'")]
    (if match
      {:path (clean-up-negative-find (nth split-string 0))
       :fact-exist? false}
      (let [result-map (zipmap [:path :fact-size-in-bytes :fact-user :fact-group :fact-mod
                                :fact-type :fact-created :fact-modified :fact-accessed]
                               split-string)
            cleaned-path (clean-up-sudo-string (:path result-map))]
        (merge
         (assoc result-map :path cleaned-path)
         {:fact-exist? true})))))

(s/defn create-line-parse-result [script-result-line]
  (let [file-fact-result (parse-find-line script-result-line)
        result-key (path-to-keyword (:path file-fact-result))]
    {result-key file-fact-result}))

(defn parse-find
  [script-result]
  (apply merge
    (map create-line-parse-result (clojure.string/split script-result #"\n"))))

(s/defn build-find-line
  "Builds the string for executing the find commands."
  [fact-config]
  (str "find " (:path (val fact-config)) " -prune -printf \"%p'%s'%u'%g'%m'%y'%c'%t'%a\\n\" 2>&1"))

(s/defn collect-file-fact
  "Collects the file facts."
  [fact-configs :- FileFactConfig]
  (collect-fact
    fact-id-file
    (str
      (clojure.string/join
       "; " (map #(build-find-line %) fact-configs))
      "; exit 0")
    :transform-fn parse-find))
