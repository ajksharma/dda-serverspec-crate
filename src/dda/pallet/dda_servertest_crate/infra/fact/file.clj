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


(ns dda.pallet.dda-servertest-crate.infra.fact.file
  (:require
    [schema.core :as s]
    [dda.pallet.dda-servertest-crate.infra.core.fact :refer :all]))

; todo: create crate boundary & schema for configuration & result

(def fact-id-file ::file)

(def FileFactConfig {:file-paths [s/Str]})

(def FileFactResult {:path s/Str
                     :exist? s/Bool
                     (s/optional-key :size-in-bytes) s/Num
                     (s/optional-key :user) s/Str
                     (s/optional-key :group) s/Str
                     (s/optional-key :mod) s/Str
                     (s/optional-key :type) s/Str ;use keywords instead here?
                     (s/optional-key :created) s/Str
                     (s/optional-key :modified) s/Str
                     (s/optional-key :accessed) s/Str})

(def FileFactResults {s/Keyword FileFactResult})

(s/defn path-to-keyword :- s/Keyword
  [path :- s/Str] (keyword (clojure.string/replace path #"/" "-")))

(s/defn parse-find-line :- FileFactResult
  [script-result-line :- s/Str]
  (let [ne-match (re-find (re-matcher #"(find: `)(.*)(': No such file or directory)" script-result-line))]
    (if ne-match
      {:path (nth ne-match 2)
       :exist? false}
      (merge
        (zipmap [:path :size-in-bytes :user :group :mod :type :created :modified :accessed]
          (clojure.string/split script-result-line #"'"))
        {:exist? true}))))

(s/defn create-line-parse-result [script-result-line]
  (let [file-fact-result (parse-find-line script-result-line)
        result-key (path-to-keyword (:path file-fact-result))]
    {result-key file-fact-result}))

(defn parse-find
  [script-result]
  (apply merge
    (map create-line-parse-result (clojure.string/split script-result #"\n"))))

(defn build-find-line
  "Builds the string for executing the find commands."
  [path]
  (str "find " path " -prune -printf \"%p'%s'%u'%g'%m'%y'%c'%t'%a\\n\""))

(defn collect-file-fact
  "Collects the file facts."
  [paths]
  (collect-fact
    fact-id-file
    (str
      (clojure.string/join
       "; " (map #(build-find-line %) paths))
      "; exit 0")
    :transform-fn parse-find))
