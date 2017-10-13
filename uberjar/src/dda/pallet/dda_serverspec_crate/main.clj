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

(defn dispatch-target-type
  "Dispatches the first keyword of the target-config."
  [domain-config target-config]
  (first (first target-config)))

(defn create-provider
  "Creates a provider from the provisioning ip and a node-id"
  [provisioning-ip node-id]
  (existing/provider provisioning-ip node-id "dda-servertest-group"))

(defn create-integrated-group-spec
  "Creates an integrated group spec from a domain config and a provisioning user."
  [domain-config provisioning-user]
  (merge
   (app/servertest-group-spec (app/app-configuration domain-config))
   (existing/node-spec provisioning-user)))

(defn execute-phase
  [phase-keyword provider integrated-group-spec]
  (cond
    (= :install phase-keyword) (pr/session-summary
                                (pr/session-summary
                                 (operation/do-apply-install provider integrated-group-spec)))
    (= :configure phase-keyword) (pr/session-summary
                                  (pr/session-summary
                                   (operation/do-apply-configure provider integrated-group-spec)))
    (= :test phase-keyword) (pr/session-summary
                             (pr/session-summary
                              (operation/do-server-test provider integrated-group-spec)))))

(defmulti execute-target dispatch-target-type)
(defmethod execute-target :aws
  [domain-config target-configs]
  (println "target aws config was given"))
(defmethod execute-target :existing
  [domain-config target-configs]
  (doseq [i (:existing target-configs)]
    (let [provider (create-provider (:provisioning-ip i) (:node-id i))
          integrated-group-spec (create-integrated-group-spec domain-config {:login (:login i) :password (:password i)})]
      (prn i)
      (if-let [phases (:phases i)]
        (doseq [i phases]
          (execute-phase i provider integrated-group-spec))
        (do
          (execute-phase :install provider integrated-group-spec)
          (execute-phase :configure provider integrated-group-spec)
          (execute-phase :test provider integrated-group-spec))))))

(defn domain-and-target-config
  "docstring"
  [domain-config target-config]
  (doseq [[k v] target-config]
    (execute-target domain-config {k v})))

(defn -main [& args]
  (case (count args)
    0 "error"
    1 "error"
    2 (domain-and-target-config (parse-config (first args)) (parse-config (second args)))
    3 "do something with credentials"
    "error"))