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

(ns dda.pallet.dda-serverspec-crate.infra.test.file-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.test.file :as sut]))


(def test-config-1 {:_etc_hosts {:exist? true}
                    :_root_.ssh_authorized_keys {:exist? false}})

(def test-config-2 {:_etc_hosts {:exist? false}
                    :_root_.ssh_authorized_keys {:exist? false}})

(def test-config-3 {:_etc_hosts {:exist? false}
                    :_root_.ssh_authorized_keys {:exist? true}})


(def input
  {:_etc_hosts {:path "/etc/hosts" :exist? true}
   :_root_.ssh_authorized_keys {:path "/root/.ssh/authorized_keys" :exist? false}})

(deftest test-file-internal
 (testing
   "test test-file-internal"
    (is (= 0
          (:no-failed (sut/test-file-internal {} input))))
    (is (= 0
          (:no-failed (sut/test-file-internal test-config-1 input))))
    (is (= 1
          (:no-failed (sut/test-file-internal test-config-2 input))))
    (is (= 2
          (:no-failed (sut/test-file-internal test-config-3 input))))))
