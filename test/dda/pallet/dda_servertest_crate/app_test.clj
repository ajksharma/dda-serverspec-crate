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


(ns dda.pallet.dda-servertest-crate.app-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-servertest-crate.app :as sut]))

(def domain-config
  {:netstat {:sshd {:port "22"}}
   :package {:firefox {:installed? false}}
   :file {:root-sth {:path "/root"
                     :exist? true}
          :etc {:path "/etc"
                :exist? true}
          :absent {:path "/absent"
                   :exist? false}}})

(deftest test-app-configuration
  (testing
    "test creation of stack configuration"
      (is (=
            {:group-specific-config
              {:dda-servertest-group
                {:dda-servertest
                  {:netstat-fact nil
                   :package-fact nil
                   :file-fact ["/root" "/etc" "/absent"]
                   :netstat-test {:sshd {:port "22"}}
                   :package-test {:firefox {:installed? false}}
                   :file-test {:absent {:path "/absent", :exist? false},
                               :etc {:path "/etc", :exist? true},
                               :root-sth {:path "/root", :exist? true}}}}}}
            (sut/app-configuration domain-config)))))
