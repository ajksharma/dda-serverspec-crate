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
(ns org.domaindrivenarchitecture.pallet.crate.servertest.instantiate-aws-servertest
  (:require
      [pallet.api :as api]      
      [pallet.compute :as compute]
      [pallet.compute.node-list :as node-list]
      [org.domaindrivenarchitecture.pallet.commons.encrypted-credentials :as crypto]
      [org.domaindrivenarchitecture.pallet.crate.user.ssh-key :as ssh-key]
      [org.domaindrivenarchitecture.pallet.crate.user.os-user :as os-user]
      [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]
      [org.domaindrivenarchitecture.pallet.crate.config :as config]
      [org.domaindrivenarchitecture.pallet.crate.init :as init]
      [org.domaindrivenarchitecture.pallet.crate.servertest :as servertest]
      [org.domaindrivenarchitecture.pallet.servertest.resource.netstat :as netstat-res]
      [org.domaindrivenarchitecture.pallet.servertest.test.netstat :as netstat-test])
  (:gen-class :main true))

(def MyServertestTestCrate 
  (dda-crate/make-dda-crate
    :facility :dda-servertest-my
    :version [0 1 0]))

(defmethod dda-crate/dda-test 
  (:facility MyServertestTestCrate) 
  [dda-crate partial-effective-config]
  (let [config (dda-crate/merge-config dda-crate partial-effective-config)]
    (netstat-res/define-resources-netstat)
    (netstat-test/test-process-listen? "sshd" 22)
    ))

(def with-my-servertest (dda-crate/create-server-spec MyServertestTestCrate))
 
(def server-config
  {:host-name "test" 
   :domain-name "meissa-gmbh.de"})

(def ssh-config 
   {:mje-netschnell 
    (ssh-key/new-ssh-key
      "ssh-rsa"
      "AAAAB3NzaC1yc2EAAAADAQABAAABAQDd0NIMownb4CSsifH2OBoO3+Hv7I04EjblR5S1VdEOZ2a59nVjWJMIwVj+JkFoon7YaYhgRoqzmDuR7nX8yfHXTljJ2VRwecvbcPV3exaNTcWSMUZMwBKIAEKdTwaZ5wHogJRYeGtPTBYf6k433sGS3TH2zy6YOCwftGKFKc4LkhB7ZnjHTQ4AWefmazt6FV8xi4ohZv/sgy3Tnm9ylxI7vHdVwvwZM4MzOoCIQTHNJWvOMgxuFmSj9vZlwj/IpwmHimxEjBszMf1gzoA7lb/3MShfCB8u3WFpTUiHOlNu1xsbrzC3f0sK9PO1qpQ2QunModw7r3Avx7lE5mK0xPW/"
      "mje@netschnell")
    :mje-jergerProject 
    (ssh-key/new-ssh-key
      "ssh-rsa"
      "AAAAB3NzaC1yc2EAAAADAQABAAABAQCeO+eiYDonq3OfxyaUx259y/1OqbhLciD4UlCkguD5PgOuXw+kCXS1Wbdor9cvU8HnsL2j70sPSwCWkcDrrGQ0kpC0GuNO47pKawAOSv07ELpSIIp/nPK5AX2+qI1H3MADBWBE5N1L7sdgatON2A/cC3u5pzcWDaEH7/IJdOkRm8H+qqG+uva6ceFUoYFiJKDixmsmaUXhhDcfYhfpAPBUCSes+HTeT/hk6pdLTX9xXd4H5wyAc+j1e6kPq9ZcxvzZNr9qEMIFjnNL/S9w1ozxQa3sKJQHj8SyVZDlwjvepGS7fKrdlRps938A7I3Y4BaXGX//M1y2HNbUWbMOllLL"
      "mje@jergerProject")
    :hel-inital-VirtualBox
    (ssh-key/new-ssh-key
      "ssh-rsa"
      "AAAAB3NzaC1yc2EAAAADAQABAAABAQCsFF72hAlbafTCp9AWh5uoJfk5XwzMIv33COn1Ixlk8C6RPS3AaLycwulLOz0fH+3jKctlyRNSam8GZSXfIkcKBZuVBsvdoLOVzWA1fzVTRjGjPl9KNc9+OcwSMvSbw7OuE9DATQ6aAqKDcbtYrWJFsgmzmFgax7t93CQtOrfeBJs6hnQYo13QGbngFGD0J9DBowWewS4zPKzozFMtscCS3uSviT/PnIpCit5RzklgdCVywjNLYuzmTuuoxjFh5uXU7Tbo8IDSAiARR2soh/A0MuMX+ZlGqhfCAL5dXM1O1IfljhAHfu/dH0cN2XcIRSXQSa2vuykZ6LGYX0BND8MR"
      "hel@inital-VirtualBox")})

(def user-config
  {:root
   (os-user/new-os-user "root" [:mje-netschnell :mje-jergerProject :hel-inital-VirtualBox])
   :pallet
   (os-user/new-os-user "pallet" "xxxxxxx" [:mje-netschnell :mje-jergerProject :hel-inital-VirtualBox])})

(def config
  {:ssh-keys ssh-config
   :os-user user-config
   :node-specific-config
   {:default-instance server-config}})

(defn aws-node-spec []
  (api/node-spec
    :location {:location-id "eu-central-1a"
               ;:location-id "eu-west-1b"
               ;:location-id "us-east-1a"
               }
    :image {:os-family :ubuntu 
            ;eu-central-1 
            :image-id "ami-87564feb"
            ;us-east-1 :image-id "ami-2d39803a"
            ;eu-west1 :image-id "ami-f95ef58a"
            :os-version "14.04"
            :login-user "ubuntu"}
    :hardware {:hardware-id "t2.micro"}
    :provider {:pallet-ec2 {:key-name "jem"               
                            :network-interfaces [{:device-index 0
                                                  :groups ["sg-0606b16e"]
                                                  :subnet-id "subnet-f929df91"
                                                  :associate-public-ip-address true
                                                  :delete-on-termination true}]}}))

(defn aws-provider [key-id key-passphrase]
  (let 
    [aws-encrypted-credentials (get-in (pallet.configure/pallet-config) [:services :aws])
     aws-decrypted-credentials (crypto/decrypt
                                 (crypto/get-secret-key
                                   {:user-home "/home/mje/"
                                    :key-id key-id})
                                 aws-encrypted-credentials
                                 key-passphrase)]
    (compute/instantiate-provider
     :pallet-ec2
     :identity (get-in aws-decrypted-credentials [:account])
     :credential (get-in aws-decrypted-credentials [:secret])
     :endpoint "eu-central-1"
     :subnet-ids ["subnet-f929df91"])))

(defn init-group []
  (api/group-spec
    "init-group"
    :extends 
    [(config/with-config config)
     init/with-init
     with-my-servertest]
    :node-spec (aws-node-spec)
    :count 1))
 
(defn do-sth [key-id key-passphrase] 
      (api/converge
        (init-group)
        :compute (aws-provider key-id key-passphrase)
        :phase '(:settings :init :test)
        :user (api/make-user "ubuntu")))
