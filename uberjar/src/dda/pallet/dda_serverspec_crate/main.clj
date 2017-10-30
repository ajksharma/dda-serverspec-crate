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
;[dda.pallet.commons.cli-helper :as cli-helper]

(ns dda.pallet.dda-serverspec-crate.main
  (:gen-class)
  (:require
    [clojure.string :as str]
    [clojure.tools.cli :as cli]
    [dda.pallet.commons.existing :as existing]
    [dda.pallet.commons.operation :as operation]
    [dda.pallet.dda-serverspec-crate.app :as app]))

(defn execute-target
  [domain-config targets]
  (let [{:keys [existing provisioning-user]} targets]
    (operation/do-server-test
     (existing/provider {:dda-servertest-group existing})
     (app/existing-provisioning-spec
       domain-config
       provisioning-user)
     :summarize-session true)))

(def cli-options
  [["-h" "--help"]])

(defn usage [options-summary]
  (str/join
   \newline
   ["dda-serverspec-crate is testing a configuration on a server"
    ""
    "Usage: java -jar dda-serverspec-crate-0.2.2-standalone.jar [test.edn] [targets.edn]"
    ""
    "Options:"
    options-summary]))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 2) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (case (count args)
      2 (execute-target (app/load-tests (first args)) (app/load-targets (second args)))
      "error")))
