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

(ns dda.pallet.domain.dda-servertest-crate
  (:require
   [pallet.api :as api]
   [schema.core :as s]
   [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
   [dda.pallet.core.dda-crate :as dda-crate]
   [dda.pallet.crate.config :as config-crate]
   [dda.pallet.crate.dda-servertest-crate :as crate]))

(def ServerTestDomainConfig
 {(s/optional-key :package) {s/Keyword {(s/optional-key :installed?) s/Bool}}
  (s/optional-key :netstat) {s/Keyword {:port s/Num}}
  (s/optional-key :file) {s/Keyword {:path s/Str
                                     (s/optional-key :exist?) s/Bool}}})

; domain: {:netstat {:sshd {:port 22}}}) ->
;
; crate:
;   {:netstat-fact nil
;    :netstat-test {sshd: {:port 22}}

(def ServertestCrateStackConfig
  {:group-specific-config
   {:dda-servertest-group
    {:dda-servertest crate/ServerTestConfig}}})

(s/defn ^:always-validate crate-stack-configuration :- ServertestCrateStackConfig
  [domain-config :- ServerTestDomainConfig]
  (let [{:keys [os-user]} domain-config]
    {:group-specific-config
      {:dda-servertest-group
        {:dda-servertest
         (merge
          (if (contains? domain-config :package)
            {:package-fact nil
             :package-test {:firefox {:installed? false}}}
            {})
          (if (contains? domain-config :netstat)
            {:netstat-fact nil
             :netstat-test {:sshd {:port 22}}}
            {})
          (if (contains? domain-config :file)
            {:file-fact ["/root" "/etc" "/absent"]
             :file-test {:-root-sth {:exist? true}
                         :-etc {:exist? true}
                         :-absent {:exist? false}}}))}}}))

(s/defn ^:always-validate dda-servertest-group
  [stack-config :- ServertestCrateStackConfig]
  (let []
    (api/group-spec
      "dda-servertest-group"
      :extends [(config-crate/with-config stack-config)
                crate/with-servertest])))
