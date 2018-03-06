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

(ns dda.pallet.dda-serverspec-crate.app
  (:require
   [schema.core :as s]
   [dda.cm.group :as group]
   [dda.pallet.core.app :as core-app]
   [dda.pallet.core.dda-crate :as dda-crate]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-serverspec-crate.infra :as infra]
   [dda.pallet.dda-serverspec-crate.domain :as domain]
   [dda.pallet.dda-serverspec-crate.app.summary :as summary]))

(def with-serverspec infra/with-serverspec)

(def InfraResult domain/InfraResult)

(def ServerspecDomainConfig domain/ServerTestDomainConfig)

(def ServerspecAppConfig
  {:group-specific-config
   {s/Keyword InfraResult}})

(s/defn ^:always-validate
  app-configuration :- ServerspecAppConfig
  [domain-config :- ServerspecDomainConfig
   & options]
  (let [{:keys [group-key]
         :or  {group-key infra/facility}} options]
    {:group-specific-config
       {group-key (domain/infra-configuration domain-config)}}))

(s/defmethod ^:always-validate
  core-app/group-spec infra/facility
  [crate-app
   domain-config :- ServerspecDomainConfig]
  (let [app-config (app-configuration domain-config)]
    (group/group-spec
      app-config [(config-crate/with-config app-config)
                  with-serverspec])))

(def crate-app (core-app/make-dda-crate-app
                  :facility infra/facility
                  :domain-schema ServerspecDomainConfig
                  :domain-schema-resolved ServerspecDomainConfig
                  :default-domain-file "serverspec.edn"))


; TODO: validate as soon as pallet-commons issue is fixed
;[session :- session/SessionSpec
(s/defn ^:always-validate
  summarize-test-session
  [session
   & options]
  (apply summary/summarize-test-session (cons session options)))

; TODO: validate as soon as pallet-commons issue is fixed
;[session :- session/SessionSpec]
(s/defn ^:always-validate
  session-passed? :- s/Bool
  [session]
  (apply summary/session-passed? '(session)))
