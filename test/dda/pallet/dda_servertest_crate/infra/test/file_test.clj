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

(ns dda.pallet.dda-servertest-crate.infra.test.file-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-servertest-crate.infra.test.file :as sut]))


(def test-config-1 {:k1 {:path "/etc/hosts"
                         :exist? true}
                    :k2 {:path "/root/.ssh/authorized_keys"
                         :exist? false}})

(def input
  '({:path "/etc/hosts"
     :exist? true}
    {:path "/root/.ssh/authorized_keys"
     :exist? false}))
;
;(deftest test-netstat-internal
; (testing
;   "test test-netstat-internal"
;   (is (= 0
;          (:no-failed (sut/test-netstat-internal {} input))))
;   (is (= 1
;          (:no-failed (sut/test-netstat-internal test-config-1 input))))
;   (is (= 2
;          (:no-failed (sut/test-netstat-internal test-config-2 input))))))
;
;(deftest retest-issues
; (testing
;   "test issues"
;   (is (= 1
;          (:no-failed (sut/test-netstat-internal
;                       {:sshd {:port 22}}
;                       ({:foreign-adress "0.0.0.0:*", :local-adress "0.0.0.0:22",
;                         :recv-q "0", :inode "15261", :state "LISTEN",
;                         :proto "tcp", :pid "1200", :process-name "sshd",
;                         :send-q "0", :user "0"}
;                        {:foreign-adress ":::*", :local-adress ":::22", :recv-q "0",
;                         :inode "15263", :state "LISTEN", :proto "tcp6", :pid "1200",
;                         :process-name "sshd", :send-q "0", :user "0"}
;                        {:proto "udp", :recv-q "0", :send-q "0",
;                         :local-adress "0.0.0.0:68", :foreign-adress "0.0.0.0:*",
;                         :state "0", :user "13122", :inode "919", :pid "dhclient"})))))))
;
