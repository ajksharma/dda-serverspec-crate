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
    [dda.pallet.dda-serverspec-crate.app :as app]
    [dda.pallet.dda-serverspec-crate.app.summary :as summary]))

(defn execute-serverspec
  [domain-config target-config verbosity]
  (let [session (operation/do-test
                  (app/existing-provider target-config)
                  (app/existing-provisioning-spec domain-config target-config)
                  :summarize-session false)]
    (app/summarize-test-session session :verbose verbosity)
    (app/session-passed? session)))

(defn execute-install
  [domain-config target-config]
  (operation/do-apply-install
    (app/existing-provider target-config)
    (app/existing-provisioning-spec domain-config target-config)
    :summarize-session true))

(def cli-options
  [["-h" "--help"]
   ["-i" "--install-dependencies"]
   ["-t" "--targets [localhost-target.edn]" "edn file containing the targets to test."
    :default "localhost-target.edn"]
   ["-v" "--verbose"]])

(defn usage [options-summary]
  (str/join
   \newline
   ["dda-serverspec-crate is testing a configuration on a server"
    ""
    "Usage: java -jar dda-serverspec-crate-[version]-standalone.jar [options] test-spec-file"
    ""
    "Options:"
    options-summary
    ""
    "test-spec-file"
    "  - follows the edn format."
    "  - has to be a valid ServerTestDomainConfig (see: https://github.com/DomainDrivenArchitecture/dda-serverspec-crate)"
    ""]))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary help]} (cli/parse-opts args cli-options)
        verbose (if (contains? options :verbose) 1 0)]
    (cond
      help (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (not= (count arguments) 1) (exit 1 (usage summary))
      (:install-dependencies options) (execute-install
                                       (app/load-domain (first arguments))
                                       (app/load-targets (:targets options)))
      :default (if (execute-serverspec
                     (app/load-domain (first arguments))
                     (app/load-targets (:targets options))
                     verbose)
                   (exit 0 (summary/styled "ALL TESTS PASSED" :green))
                   (exit 2 (summary/styled "SOME TESTS FAILED" :red))))))
