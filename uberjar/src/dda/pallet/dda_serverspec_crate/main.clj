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
   [keypin.core :refer [defkey letval] :as k]
   [clojure.tools.cli :as cli]
   [clojure.string :as str]
   [dda.cm.operation :as operation]
   [pallet.repl :as pr]
   [dda.cm.existing :as existing]
   [dda.pallet.dda-serverspec-crate.app :as app]
   [schema.core :as schema]))

(defn dispatch-file-type
  "Dispatches a string to a keyword which represents the file type."
  [file-name]
  (keyword (last (str/split file-name #"\."))))

(defmulti parse-config dispatch-file-type)

(defmethod parse-config :edn
  [file-path]
  (keypin.util/clojurize-data (k/read-config [file-path])))

(defn node-spec
  "Creates a provider from the provisioning ip and a node-id"
  [provisioning-ip node-id]
  (existing/provider provisioning-ip node-id "dda-servertest-group"))

(defn provisioning-spec
  "Creates an integrated group spec from a domain config and a provisioning user."
  [domain-config provisioning-user]
  (merge
   (app/servertest-group-spec (app/app-configuration domain-config))
   (existing/node-spec provisioning-user)))

(defn server-test
  [provider provisioning-spec & options]
  (let [{:keys [summarize-session]
         :or {summarize-session true}} options]
    (operation/do-server-test
     (cloud-target/provider)
     (provisioning-spec count)
     :summarize-session summarize-session)))

(defn execute-target
  [domain-config target-configs]
  (doseq [i (:existing target-configs)]
    (let [provider (create-provider (:provisioning-ip i) (:node-id i))
          provisioning-spec (provisioning-spec
                             domain-config
                             {:login (:login i)
                              :password (:password i)})]
      (prn i)
      (server-test provider provisioning-spec))))

(defn domain-and-target-config
  "docstring"
  [domain-config target-config]
  (doseq [[k v] target-config]
    (execute-target domain-config {k v})))

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
      2 (domain-and-target-config (parse-config (first args)) (parse-config (second args)))
      "error")))
