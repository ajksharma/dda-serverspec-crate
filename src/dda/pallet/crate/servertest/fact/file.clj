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

(def fact-id-netstat ::file)

(def FileFactConfig {:file-paths [s/Str]})

(def FileFactResult {s/Keyword {:exist s/Bool
                                :directory s/Bool
                                :link s/Bool
                                :owner s/Str
                                :group s/Str
                                :mod s/Str
                                :size s/Num
                                :}})



(defn parse-find
  [file-resource]
  (map #(zipmap
          [:name :size :user :group :mod :type :created :modified :accessed]
          (clojure.string/split (clojure.string/trim %) #"\s+|/"))
     (drop-while #(not (re-matches #"\s*(tcp|udp).*" %))
       (clojure.string/split netstat-resource #"\n"))))

(s/defn collect-netstat-fact
  "Defines the netstat resource.
   This is automatically done serverstate crate is used."
  [files-to-inspect :- FileFactConfig]
  ;find linked-entry -prune -printf "'%f' '%s' '%u' '%g' '%m' '%y' '%c' '%t' '%a'\n"
  (collect-fact fact-id-netstat '("find" ~path "-prune" "-printf \"'%f' '%s' '%u' '%g' '%m' '%y' '%c' '%t' '%a'\n\"") :transform-fn parse-find))
