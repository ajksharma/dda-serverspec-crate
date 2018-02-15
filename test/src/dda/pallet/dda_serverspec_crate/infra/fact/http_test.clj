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

(ns dda.pallet.dda-serverspec-crate.infra.fact.http-test
  (:require
    [clojure.test :refer :all]
    [clojure.tools.logging :as logging]
    [dda.pallet.dda-serverspec-crate.infra.fact.http :as sut]))

; ------------------------  test data  ------------------------
(def reference-date
  (java.time.LocalDate/parse "10.02.2018"
    (java.time.format.DateTimeFormatter/ofPattern "dd.MM.yyyy")))

(def date-offset
  (try
    (.between (java.time.temporal.ChronoUnit/DAYS)
              reference-date
              (java.time.LocalDate/now))
    (catch java.time.DateTimeException ex
      (logging/warn "Exception encountered : " ex))))

(def script-output1
  "_some_url
* Rebuilt URL to: https://google.com/
*   Trying 216.58.207.46...
* Connected to google.com (216.58.207.46) port 443 (#0)
* found 148 certificates in /etc/ssl/certs/ca-certificates.crt
* found 597 certificates in /etc/ssl/certs
* ALPN, offering http/1.1
* SSL connection using TLS1.2 / ECDHE_ECDSA_AES_128_GCM_SHA256
* 	 server certificate verification OK
* 	 server certificate status verification SKIPPED
* 	 common name: *.google.com (matched)
* 	 server certificate expiration date OK
* 	 server certificate activation date OK
* 	 certificate public key: EC
* 	 certificate version: #3
* 	 subject: C=US,ST=California,L=Mountain View,O=Google Inc,CN=*.google.com
* 	 start date: Tue, 23 Jan 2018 13:36:00 GMT
* 	 expire date: Sat, 7 Mar 2020 23:59:59 GMT
* 	 issuer: C=US,O=Google Inc,CN=Google Internet Authority G2
* 	 compression: NULL
* ALPN, server accepted to use http/1.1
> GET / HTTP/1.1
> Host: google.com
> User-Agent: curl/7.47.0
> Accept: */*
>
< HTTP/1.1 302 Found
<
<HTML><HEAD>...</HEAD><BODY>
</BODY></HTML>
* Connection #0 to host google.com left intact
")

(def fact1 {:_some_url {:expiration-days (- 756 date-offset)}})

(def script-output2
  (str
    "https___google.com
  * Rebuilt URL to: https://google.com/
  *   Trying 216.58.207.46...
  * Connected to google.com (216.58.207.46) port 443 (#0)
  * found 148 certificates in /etc/ssl/certs/ca-certificates.crt
  * found 597 certificates in /etc/ssl/certs
  * ALPN, offering http/1.1
  * SSL connection using TLS1.2 / ECDHE_ECDSA_AES_128_GCM_SHA256
  * 	 server certificate verification OK
  * 	 server certificate status verification SKIPPED
  * 	 common name: *.google.com (matched)
  * 	 server certificate expiration date OK
  * 	 server certificate activation date OK
  * 	 certificate public key: EC
  * 	 certificate version: #3
  * 	 subject: C=US,ST=California,L=Mountain View,O=Google Inc,CN=*.google.com
  * 	 start date: Tue, 23 Jan 2018 13:36:00 GMT
  * 	 expire date: Tue, 17 Apr 2018 13:36:00 GMT
  * 	 issuer: C=US,O=Google Inc,CN=Google Internet Authority G2
"
    sut/output-separator
    "https___bahn.de
* Rebuilt URL to: https://bahn.de/
*   Trying 46.18.63.152...
* Connected to bahn.de (46.18.63.152) port 443 (#0)
* found 148 certificates in /etc/ssl/certs/ca-certificates.crt
* found 597 certificates in /etc/ssl/certs
* ALPN, offering http/1.1
* SSL connection using TLS1.2 / ECDHE_RSA_AES_256_GCM_SHA384
* 	 server certificate verification SKIPPED
* 	 server certificate status verification SKIPPED
* 	 common name: bahn.de (matched)
* 	 server certificate expiration date OK
* 	 server certificate activation date OK
* 	 certificate public key: RSA
* 	 certificate version: #3
* 	 subject: C=DE,ST=Hessen,L=Frankfurt am Main,O=DB Systel GmbH,CN=bahn.de
* 	 start date: Fri, 10 Mar 2017 00:00:00 GMT
* 	 expire date: Sat, 7 Mar 2020 23:59:59 GMT
* 	 issuer: C=US,O=Symantec Corporation,OU=Symantec Trust Network,CN=Symantec Class 3 Secure Server SHA256 SSL CA
"
    sut/output-separator))

(def fact2 {:https___google.com {:expiration-days (- 66 date-offset)}
            :https___bahn.de {:expiration-days (- 756 date-offset)}})

(def script-output3
  "_someinvalidurl\n* Rebuilt URL to: https://google.c/
* Could not resolve host: google.c
* Closing connection 0
curl: (6) Could not resolve host: google.c
")

(def fact3 {:_someinvalidurl {:expiration-days -1}})

; ------------------------  tests  ------------------------------
(deftest test-parse
  (testing
    "test parsing http output"
    (is (= fact1
           (sut/parse-http-response script-output1)))
    (is (= fact2
           (sut/parse-http-script-responses script-output2)))
    (is (= fact3
           (sut/parse-http-script-responses script-output3)))))
