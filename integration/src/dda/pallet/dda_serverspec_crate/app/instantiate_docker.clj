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
(ns dda.pallet.dda-serverspec-crate.app.instantiate-docker
  (:require [clojure.java.shell :as shell]))

(defn build-docker-image
  [docker-file image-name directory]
  (shell/sh "docker" "build" "-f" docker-file "-t" image-name directory "uberjar=target/dda-serverspec-crate-1.1.1-standalone.jar"))

(defn run-docker-image
  [image]
  (shell/sh "docker" "run" "-t" image "uberjar=target/dda-serverspec-crate-1.1.1-standalone.jar"))

(defn serverspec-docker-test []
  (build-docker-image "serverspec-docker" "demo/serverspec" ".")
  (run-docker-image "demo/serverspec:latest"))

;docker build -f serverspec-docker -t demo/serverspec .
; docker run -i -t demo/serverspec:latest