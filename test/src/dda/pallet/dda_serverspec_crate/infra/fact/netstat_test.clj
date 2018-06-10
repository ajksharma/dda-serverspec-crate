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


(ns dda.pallet.dda-serverspec-crate.infra.fact.netstat-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.fact.netstat :as sut]))


(def netstat-resource1
  "Proto Recv-Q Send-Q Local Address           Foreign Address         State       User       Inode       PID/Program name
   tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      0          9807        1001/sshd
   tcp6       0      0 :::80                   :::*                    LISTEN      0          44161       4135/apache2
   tcp6       0      0 :::4369                 :::*                    LISTEN      108        33687       27416/epmd")

(def netstat-resource2
  "Another line
   Proto Recv-Q Send-Q Local Address           Foreign Address         State       User       Inode       PID/Program name
   tcp6       0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      0          9807        1001/sshd
   tcp6       0      0 :::80                   :::*                    LISTEN      0          44161       4135/apache2
   tcp6       0      0 :::4369                 :::*                    LISTEN      108        33687       27416/epmd")

(def netstat-resource3
  "Proto Recv-Q Send-Q Local Address           Foreign Address         State       User       Inode       PID/Program name
   udp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      0          9807        1001/sshd
   tcp6       0      0 :::80                   :::*                    LISTEN      0          44161       4135/apache2
   tcp6       0      0 :::4369                 :::*                    LISTEN      108        33687       27416/epmd")

(def netstat-resource4
 " Proto Recv-Q Send-Q Local Address           Foreign Address         State       User       Inode       PID/Program name
   tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      0          15304       1198/sshd
   tcp6       0      0 :::22                   :::*                    LISTEN      0          15306       1198/sshd
   udp        0      0 0.0.0.0:68              0.0.0.0:*                           0          13182       916/dhclient")


(deftest test-split-line
 (testing
   "test splitting fixed size line"
     (is (= 11
            (count (sut/split-netstat-line (str "tcp        0      0 0.0.0.0:22              0.0.0.0:*"
                                                "               LISTEN      0          9807        1001/sshd")))))
     (is (= 11
            (count (sut/split-netstat-line (str "udp        0      0 0.0.0.0:68              0.0.0.0:*"
                                                "                           0          13182       916/dhclient")))))
     (is (= 11
            (count (sut/split-netstat-line (str "tcp6       0      0 :::22                   :::* "
                                                "                   LISTEN      0          15306       1198/sshd")))))
     (is (= 11
            (count (sut/split-netstat-line (str "111111222222233333334444444444444444444:4444555555555555555555555555"
                                                "666666666666777777777778888888888889999/00000000000")))))))



(deftest test-parse
  (testing
    "test parsing netstat-output"
      (is (= "sshd"
             (:fact-process-name
               (first (sut/parse-netstat netstat-resource1)))))
      (is (= "sshd"
             (:fact-process-name
               (first (sut/parse-netstat netstat-resource2)))))
      (is (= "sshd"
             (:fact-process-name
               (first (sut/parse-netstat netstat-resource3)))))
      (is (= "dhclient"
             (:fact-process-name
               (nth (sut/parse-netstat netstat-resource4) 2))))))
