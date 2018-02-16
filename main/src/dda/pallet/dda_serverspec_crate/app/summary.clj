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

(ns dda.pallet.dda-serverspec-crate.app.summary
  (:require
   [schema.core :as s]))

(def StyleCodes
  {;style
   :reset              "\033[0m"
   :bright             "\033[1m"
   :blink-slow         "\033[5m"
   :underline          "\033[4m"
   :underline-off      "\033[24m"
   :inverse            "\033[7m"
   :inverse-off        "\033[27m"
   :strikethrough      "\033[9m"
   :strikethrough-off  "\033[29m"
   ;text
   :default "\033[39m"
   :white   "\033[37m"
   :black   "\033[30m"
   :red     "\033[31m"
   :green   "\033[32m"
   :blue    "\033[34m"
   :yellow  "\033[33m"
   :magenta "\033[35m"
   :cyan    "\033[36m"
   ;background
   :bg-default "\033[49m"
   :bg-white   "\033[47m"
   :bg-black   "\033[40m"
   :bg-red     "\033[41m"
   :bg-green   "\033[42m"
   :bg-blue    "\033[44m"
   :bg-yellow  "\033[43m"
   :bg-magenta "\033[45m"
   :bg-cyan    "\033[46m"})

(defn styled [string style]
  (str (get StyleCodes style) string (:reset StyleCodes)))

(defn run-results [session]
  (:runs (pallet.core.data-api/session-data session)))

(defn node-results [session]
  (filter some? (:action-results (second (run-results session)))))

(defn summarize-tests [test-results indent verbose]
  (doseq [test-result (clojure.string/split-lines test-results)]
    (println (str "    " test-result))))

(defn summarize-node-tests [action-results indent verbose]
  (doseq [action-result action-results]
    (let [{:keys [context summary-text out result]} action-result]
      (println (str "  " context " - ")
        (if (:test-passed result)
          (styled summary-text :green)
          (styled summary-text :red)))
      (when (> verbose 0)
        (summarize-tests out (+ 2 indent) verbose)))))

(defn tests-passed? [action-result]
  (get-in action-result [:result :test-passed]))

(defn run-passed?
  [run]
  (let [action-results (filter some? (:action-results run))]
    (every? tests-passed? action-results)))

(defn summarize-test-session
  [session & {:keys [verbose] :or {verbose 0}}]
  (let [runs (run-results session)]
    (doseq [run runs]
      (let [action-results (filter some? (:action-results run))]
        (println
          (str
            (get-in run [:node :primary-ip]) " - "
            (if (run-passed? run)
              (styled "PASSED" :green)
              (styled "FAILED" :red))))
        (summarize-node-tests action-results 2 verbose)))))

(defn session-passed?
  [session]
  (let [runs (run-results session)]
    (every? run-passed? runs)))
