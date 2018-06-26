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

(ns dda.pallet.dda-serverspec-crate.infra.test.netstat-test
  (:require
    [clojure.test :refer :all]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.test.netstat :as sut]))

(def test-config-1 {:not-running-service_11 {:port "11" :exp-proto "tcp" :running? true}
                    :sshd_0.0.0.0_11 {:port "11" :ip "0.0.0.0" :exp-proto "tcp" :running? false}
                    :sshd_22 {:port "22" :running? true}
                    (keyword "apache2_80") {:port "80" :ip "::" :exp-proto "tcp6" :running? true}})
(def test-config-2 {:not-running-service_0.0.0.1_11 {:port "11" :ip "0.0.0.1" :exp-proto "tcp" :running? true}
                    (keyword "apache2_81") {:port "81" :ip "::" :exp-proto "tcp6" :running? true}
                    :sshd_0.0.0.0_22 {:port "22" :ip "0.0.0.0" :exp-proto "tcp" :running? true}})
(def test-config-3 {:not-running-service_0.0.0.1_11 {:port "11" :ip "0.0.0.1" :exp-proto "tcp" :running? false}
                    (keyword "apache2_80") {:port "80" :ip "::" :exp-proto "tcp7" :running? false}})
(def test-config-4 {(keyword "apache2_80") {:port "80" :ip "::" :running? true}
                    :sshd____22 {:port "22" :ip "::" :exp-proto "tcp6" :running? true}})

(def input
  '({:fact-foreign-adress ":::*",
     :fact-ip "::",
     :fact-port "80",
     :fact-recv-q "0",
     :fact-inode "44161",
     :fact-state "LISTEN",
     :fact-process-name "apache2",
     :fact-proto "tcp6",
     :fact-pid "4135",
     :fact-send-q "0",
     :fact-user "0"}
    {:fact-foreign-adress "0.0.0.0:*",
     :fact-ip "0.0.0.0",
     :fact-port "22",
     :fact-recv-q "0",
     :fact-inode "10289",
     :fact-state "LISTEN",
     :fact-process-name "sshd",
     :fact-proto "tcp",
     :fact-pid "974",
     :fact-send-q "0",
     :fact-user "0"}
    {:fact-foreign-adress ":::*",
     :fact-ip "::",
     :fact-port "22",
     :fact-recv-q "0",
     :fact-inode "10289",
     :fact-state "LISTEN",
     :fact-process-name "sshd",
     :fact-proto "tcp6",
     :fact-pid "974",
     :fact-send-q "0",
     :fact-user "0"}))

(deftest test-netstat-internal
 (testing
   "test test-netstat-internal"
   (is (= 0
          (:no-failed (sut/test-netstat-internal {} input))))
   (is (= 1
          (:no-failed (sut/test-netstat-internal test-config-1 input))))
   (is (= 2
          (:no-failed (sut/test-netstat-internal test-config-2 input))))
   (is (= 0
          (:no-failed (sut/test-netstat-internal test-config-3 input))))
   (is (= 0
          (:no-failed (sut/test-netstat-internal test-config-4 input))))))

(deftest retest-issues
 (testing
   "test issues"
   (is (= 1
          (:no-failed (sut/test-netstat-internal
                       {:sshd_22 {:port 22}}
                       '({:fact-foreign-adress "0.0.0.0:*", :fact-ip "0.0.0.0" :fact-port "22",
                          :fact-recv-q "0", :fact-inode "15261", :fact-state "LISTEN",
                          :fact-proto "tcp", :fact-pid "1200", :fact-process-name "sshd",
                          :fact-send-q "0", :fact-user "0"}
                         {:fact-foreign-adress ":::*", :fact-ip "::" :fact-port "22", :fact-recv-q "0",
                          :fact-inode "15263", :fact-state "LISTEN", :fact-proto "tcp6", :fact-pid "1200",
                          :fact-process-name "sshd", :fact-send-q "0", :fact-user "0"}
                         {:fact-proto "udp", :fact-recv-q "0", :fact-send-q "0",
                          :fact-ip "0.0.0.0" :fact-port "68", :fact-foreign-adress "0.0.0.0:*",
                          :fact-state "0", :fact-user "13122", :fact-inode "919", :fact-pid "dhclient"})))))))
