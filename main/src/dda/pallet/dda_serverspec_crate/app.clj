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
   [dda.pallet.commons.existing :as existing]
   [dda.pallet.core.dda-crate :as dda-crate]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-serverspec-crate.infra :as infra]
   [dda.pallet.dda-serverspec-crate.domain :as domain]
   [dda.pallet.dda-serverspec-crate.app.external-config :as ext-config]))

(def with-serverspec infra/with-serverspec)

(def ServerSpecDomainConfig domain/ServerTestDomainConfig)

(def ProvisioningUser {:login s/Str
                       (s/optional-key :password) s/Str})

(def Targets {:existing [existing/ExistingNode]
              :provisioning-user ProvisioningUser})

(def InfraResult domain/InfraResult)

(def ServertestAppConfig
  {:group-specific-config
   {s/Keyword InfraResult}})

(s/defn ^:always-validate load-targets :- Targets
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate load-tests :- ServerSpecDomainConfig
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate create-app-configuration :- ServertestAppConfig
  [config :- infra/ServerTestConfig
   group-key :- s/Keyword]
  {:group-specific-config
     {group-key config}})

(s/defn ^:always-validate app-configuration :- ServertestAppConfig
  [domain-config :- ServerSpecDomainConfig
   & options]
  (let [{:keys [group-key]
         :or  {group-key :dda-servertest-group}} options]
    {:group-specific-config
       {group-key (domain/infra-configuration domain-config)}}))

(s/defn ^:always-validate servertest-group-spec
  [app-config :- ServertestAppConfig]
  (group/group-spec
    app-config [(config-crate/with-config app-config)
                with-serverspec]))

(s/defn ^:always-validate existing-provisioning-spec
  "Creates an integrated group spec from a domain config and a provisioning user."
  [domain-config :- ServerSpecDomainConfig
   provisioning-user :- ProvisioningUser]
  (merge
   (servertest-group-spec (app-configuration domain-config))
   (existing/node-spec provisioning-user)))
