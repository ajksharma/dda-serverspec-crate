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

(ns dda.pallet.servertest.fact.file
  (:require
    [dda.pallet.servertest.core.fact :refer :all]))

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

(defn parse-find
  [file-resource]
  ;todo create parse function
  (map #(zipmap
          [:name :size-in-bytes :user :group :mod :type :created :modified :accessed]
          (clojure.string/split (clojure.string/trim %) #"\s+|/"))
     (drop-while #(not (re-matches #"\s*(tcp|udp).*" %))
       (clojure.string/split netstat-resource #"\n"))))

(s/defn collect-file-fact
  "Defines the netstat resource.
   This is automatically done serverstate crate is used.
   See also: https://unix.stackexchange.com/questions/128985/why-not-parse-ls, http://man7.org/linux/man-pages/man1/find.1.html"
  [files-to-inspect :- FileFactConfig]
  ;find linked-entry -prune -printf "'%f' '%s' '%u' '%g' '%m' '%y' '%c' '%t' '%a'\n"
  ;todo: iterate over files-to-inspect & create one script
  (collect-fact fact-id-netstat '("find" ~path "-prune" "-printf \"'%f' '%s' '%u' '%g' '%m' '%y' '%c' '%t' '%a'\n\"") :transform-fn parse-find))
