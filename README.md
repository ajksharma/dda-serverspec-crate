# dda-serverspec-crate

[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-serverspec-crate.svg)](https://clojars.org/dda/dda-serverspec-crate)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-serverspec-crate.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-serverspec-crate)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](https://clojurians.slack.com/messages/#dda-pallet/) | [<img src="https://domaindrivenarchitecture.org/img/meetup.svg" width=50 alt="DevOps Hacking with Clojure Meetup"> DevOps Hacking with Clojure](https://www.meetup.com/de-DE/preview/dda-pallet-DevOps-Hacking-with-Clojure) | [Website & Blog](https://domaindrivenarchitecture.org)

## Compatibility
dda-pallet is compatible to the following versions
 * pallet 0.8
 * clojure 1.7
 * (x)ubunutu 16.04

## Usage documentation
This crate provides integration tests for servers.
There is two main scenarios in which our crate could be
used for great benefit.
### Remote-whitebox
The first is the remote-whitebox test.

![alt text](./ServerSpecRemoteWhitebox.png "ServerSpecRemoteWhitebox")

Here the serverspec crate can be used from a source machine to test remote target machines.
The crate can be used here either through the source code or the jar.

### Local-whitebox
The second scenario is the local-whitebox test.

<p align="center">
  <img src="./ServerSpecLocalWhitebox.png" alt="ServerSpecLocalWhitebox">
</p>

Here the serverspec crate can be used directly on a target machine to test itself.
A possible use case would be to deploy the jar on the target and execute it there via the terminal.

### Configuration
To determine what kind of integration tests are going to be executed, two external config files have to be created.

#### Targets config
```clojure
{:existing [{:node-name "test-vm1"
             :node-ip "35.157.19.218"}
            {:node-name "test-vm2"
             :node-ip "18.194.113.138"}]
 :provisioning-user {:login "ubuntu"}}
```

#### Test config
```clojure
{:netstat [{:process-name "sshd" :port "11" :running? false}
           {:process-name "sshd" :port "22"}
           {:process-name "sshd" :port "22" :exp-proto "tcp6" :ip "::"}]
 :file [{:path "/root"}
        {:path "/etc"}
        {:path "/absent" :exist? false}]
 :netcat [{:host "www.google.com" :port 80}
          {:host "www.google.c" :port 80 :reachable? false}]
 :package [{:name "test" :installed? false}
           {:name "nano"}]}
```         

-> minimal example for config and where the example-files are

### Test with jar-file
-> how to input config into jar
-> github realeses link to jar file

## Reference
-> Schema explain?
-> explain what infra/domain means
### Schema for Targets

```clojure
(def ExistingNode {:node-name s/Str
                   :node-ip s/Str})

(def ProvisioningUser {:login s/Str
                       (s/optional-key :password) s/Str})

(def Targets {:existing [ExistingNode]
              :provisioning-user ProvisioningUser})
```

### Domain-Schema for Tests

```clojure
(def ServerTestDomainConfig {(s/optional-key :package) [{:name s/Str
                                                         (s/optional-key :installed?) s/Bool}]
                             (s/optional-key :netstat) [{:process-name s/Str
                                                         :port s/Str
                                                         (s/optional-key :running?) s/Bool
                                                         (s/optional-key :ip) s/Str
                                                         (s/optional-key :exp-proto) s/Str}]
                             (s/optional-key :file) [{:path s/Str
                                                      (s/optional-key :exist?) s/Bool}]
                             (s/optional-key :netcat) [{:host s/Str
                                                        :port s/Num
                                                        (s/optional-key :reachable?) s/Bool}]})

```

### Infra-Schema for Facts & Tests
```clojure
(def ServerTestConfig {(s/optional-key :package-fact) s/Any
                       (s/optional-key :netstat-fact) s/Any
                       (s/optional-key :file-fact) file-fact/FileFactConfig
                       (s/optional-key :netcat-fact) netcat-fact/NetcatFactConfig
                       (s/optional-key :package-test) package-test/PackageTestConfig
                       (s/optional-key :netstat-test) netstat-test/NetstatTestConfig
                       (s/optional-key :file-test) file-test/FileTestConfig
                       (s/optional-key :netcat-test) netcat-test/NetcatTestConfig})
```

## License
Published under [apache2.0 license](LICENSE.md)
