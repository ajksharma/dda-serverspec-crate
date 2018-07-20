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

(ns dda.pallet.dda-serverspec-crate.infra.fact.command-test
  (:require
    [clojure.test :refer :all]
    [pallet.actions :as actions]
    [dda.pallet.dda-serverspec-crate.infra.fact.command :as sut]))

(def remote
  {
   :input
   "find--absent
find: ‘/absent‘: Datei oder Verzeichnis nicht gefunden
2
----- command output separator -----
"
   :expected
   {:find--absent
    {:exit-code 2,
     :stout
     "find: ‘/absent‘: Datei oder Verzeichnis nicht gefunden"}}})

(def localhost
  {:input
   "find--absent
find: \"/absent\": Datei oder Verzeichnis nicht gefunden
1
----- command output separator -----
echo--Hallo-Welt-
Hallo Welt
0
----- command output separator -----
echo--Hallo-Welt---echo--second-line-
Hallo Welt
second line
0
----- command output separator -----
"
   :expected
   {:find--absent
    {:exit-code 1,
     :stout
     "find: \"/absent\": Datei oder Verzeichnis nicht gefunden"},
    :echo--Hallo-Welt- {:exit-code 0, :stout "Hallo Welt"},
    :echo--Hallo-Welt---echo--second-line-
    {:exit-code 0, :stout "Hallo Welt\nsecond line"}}})

(deftest test-parse-command-outputs
  (testing
    (is (= (:expected remote)
           (sut/parse-command-outputs (:input remote))))
    (is (= (:expected localhost)
           (sut/parse-command-outputs (:input localhost))))))

(deftest test-parse-single-command-output
  (testing
    (is (= {:find--absent
            {:exit-code 1,
             :stout "find: \"/absent\": Datei oder Verzeichnis nicht gefunden"}}
           (sut/parse-single-command-output "find--absent\nfind: \"/absent\": Datei oder Verzeichnis nicht gefunden\n1\n")))
    (is (= {:echo--Hallo-Welt {:exit-code 0, :stout "Hallo Welt"}}
           (sut/parse-single-command-output "echo--Hallo-Welt\nHallo Welt\n0\n")))
    (is (= {:echo--Hallo-Welt---echo-second-line
            {:exit-code 0, :stout "Hallo Welt\nsecond-line"}}
           (sut/parse-single-command-output "echo--Hallo-Welt---echo-second-line\nHallo Welt\nsecond line\n0\n")))))

(deftest test-command-to-keyword
  (testing
    (is (= :find--absent
           (sut/command-to-keyword "find /absent")))
    (is (= :echo--Hallo-Welt-
           (sut/command-to-keyword "echo 'Hallo Welt'")))
    (is (= :echo--Hallo-Welt---echo--second-line-
           (sut/command-to-keyword "echo 'Hallo Welt'; echo 'second line'")))))
