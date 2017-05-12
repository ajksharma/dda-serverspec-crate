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


(ns dda.pallet.crate.servertest.fact.file
  (:require
   [dda.pallet.servertest.core.fact :refer :all]
   [schema.core :as s]))

; todo: create crate boundary & schema for configuration & result

(def fact-id-netstat ::file)

(def FileFactConfig {:file-paths [s/Str]})

(def FileFactResult {s/Keyword {:exist s/Bool
                                :size-in-bytes s/Num
                                :user s/Str
                                :group s/Str
                                :mod s/Str
                                :type s/Str ;use keywords instead here?
                                :created s/Str
                                :modified s/Str
                                :accessed s/Str}})

(defn build-find-string
  "Builds the string for executing the find commands."
  [path]
  (let [p (clojure.string/join "/" (drop-last (clojure.string/split path #"/"))) 
        file (last (clojure.string/split path #"/"))]
     ["find"
      p
      "-name"
      file
      "-prune"
      "-printf \"%f'%s'%u'%g'%m'%y'%c'%t'%a\n\""]))

(defn collect-file-fact
  "Collects the file facts."
  [files-to-inspect]
  (let [paths (:file-paths files-to-inspect)]
    (collect-fact
     fact-id-netstat 
     (flatten
      (interpose
       "&&"
       (map #(build-find-string %) paths)))
     :transform-fn "parse-find")))
