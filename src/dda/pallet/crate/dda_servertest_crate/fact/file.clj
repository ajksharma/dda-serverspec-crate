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


(ns dda.pallet.crate.dda-servertest-crate.fact.file
  (:require
   [dda.pallet.crate.dda-servertest-crate.core.fact :refer :all]
   [schema.core :as s]))

; todo: create crate boundary & schema for configuration & result

(def fact-id-file ::file)

(def FileFactConfig {:file-paths [s/Str]})

(def FileFactResult {s/Keyword {:exists s/Bool
                                (s/optional-key :path) s/Str
                                (s/optional-key :size-in-bytes) s/Num
                                (s/optional-key :user) s/Str
                                (s/optional-key :group) s/Str
                                (s/optional-key :mod) s/Str
                                (s/optional-key :type) s/Str ;use keywords instead here?
                                (s/optional-key :created) s/Str
                                (s/optional-key :modified) s/Str
                                (s/optional-key :accessed) s/Str}})

(defn parse-find
  [paths file-resource]
  (let [maps (#(zipmap (map (fn [m] (keyword (:path m))) %) %)
               (map #(assoc % :exists true)
                    (map #(update-in % [:size-in-bytes] read-string)
                         (map #(zipmap [:path :size-in-bytes :user :group :mod :type :created :modified :accessed] %)
                              (map #(clojure.string/split % #"'") (clojure.string/split file-resource #"\n"))))))
        existing-path-keys (keys maps)
        path-keys (map #(keyword %) paths)
        non-existing-path-keys (seq (clojure.set/difference (set path-keys) (set existing-path-keys)))]
    (merge
      maps
      (#(zipmap (map (fn [n] n) %) (repeat (count %) {:exists false}))
        non-existing-path-keys))))

(defn build-find-vec
  "Builds the string for executing the find commands."
  [path]
  (let [p (clojure.string/join "/" (drop-last (clojure.string/split path #"/")))
        n (last (clojure.string/split path #"/"))]
     ["find"
      p
      "-name"
      n
      "-prune"
      "-printf \"%p'%s'%u'%g'%m'%y'%c'%t'%a\\n\""]))

(defn collect-file-fact
  "Collects the file facts."
  [files-to-inspect]
  (let [paths (:file-paths files-to-inspect)]
    (collect-fact
     fact-id-file
     (flatten
      (interpose
       "&&"
       (map #(build-find-vec %) paths)))
     :transform-fn (partial parse-find paths))))
