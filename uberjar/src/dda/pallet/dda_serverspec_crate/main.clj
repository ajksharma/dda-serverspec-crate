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
    [keypin.core :refer [defkey letval] :as k]
    [schema.core :as s]
    [pallet.repl :as pr]
    [dda.pallet.commons.operation :as operation]
    [dda.pallet.commons.existing :as existing]
    [dda.pallet.dda-serverspec-crate.app :as app]))

(defn dispatch-file-type
  "Dispatches a string to a keyword which represents the file type."
  [file-name]
  (keyword (last (str/split file-name #"\."))))

(defmulti parse-config dispatch-file-type)

(defmethod parse-config :edn
  [file-path]
  (keypin.util/clojurize-data (k/read-config [file-path])))

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
     provider
     provisioning-spec
     :summarize-session summarize-session)))

(defn execute-target
  [domain-config targets]
  (let [{:keys [serverspec provisioning-user]} targets]
    (server-test (existing/provider {:dda-servertest-group serverspec})
                 (provisioning-spec
                             domain-config
                             provisioning-user)
                 :summarize-session true)))

(defn -main [& args]
  (case (count args)
    0 "error"
    1 "error"
    2 (execute-target (parse-config (first args)) (parse-config (second args)))
    3 "do something with credentials"
    "error"))
