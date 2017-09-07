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


(ns dda.pallet.dda-serverspec-crate.infra.fact.netcat
  (:require
   [schema.core :as s]
   [dda.pallet.dda-serverspec-crate.infra.core.fact :refer :all]))

(def fact-id-netcat ::netcat)

(def NetcatFactConfig
  {s/Keyword
   {:host s/Str
    :port s/Num
    :timeout s/Num}})

(def NetcatFactResult {:reachable? s/Bool})

(def NetcatFactResults {s/Keyword NetcatFactResult})

(s/defn replace-comma :- s/Str [input :- s/Str]
  (clojure.string/replace input #"," "-"))

(s/defn ^:always-validate config-to-string :- s/Str
  [host :- s/Str port :- s/Num timeout :- s/Num]
  (str (replace-comma host)
       "_"
       port
       "_"
       timeout))

;TODO: Make DNS always to FQDN
(s/defn build-netcat-script
  "builds the script from the given config"
  [netcat-config :- NetcatFactConfig]
  (let [config-val (val netcat-config)]
    (str
     "echo -n '"
     (config-to-string
      (:host config-val)
      (:port config-val)
      (:timeout config-val))
     ",' && nc "
     (:host config-val)
     " "
     (:port config-val)
     " -w "
     (:timeout config-val)
     " && echo $?")))

(s/defn parse-result [out-raw-line :- s/Str]
  (let [split-raw (clojure.string/split out-raw-line  #",")
        has-error (> (count (nth split-raw 1)) 1)
        netcat-key (keyword (clean-up-sudo-string (nth split-raw 0)))]
    (if has-error
      {netcat-key {:reachable? false}}
      {netcat-key {:reachable? (= 0 (read-string (nth split-raw 1)))}})))

(defn parse-netcat
  [script-result]
  (apply merge
         (map parse-result (clojure.string/split script-result #"\n"))))

(s/defn collect-netcat-fact
  "Collects the netcat facts."
  [netcat-configs :- NetcatFactConfig]
  (collect-fact
   fact-id-netcat
   (str
    (clojure.string/join
     "; " (map #(build-netcat-script %) netcat-configs))
    "; exit 0")
   :transform-fn parse-netcat))
