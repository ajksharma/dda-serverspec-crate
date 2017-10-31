# dda-serverspec-crate

[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-serverspec-crate.svg)](https://clojars.org/dda/dda-serverspec-crate)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-serverspec-crate.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-serverspec-crate)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](http://clojurians.net) | [Meet us on Meetup](https://www.meetup.com/de-DE/preview/dda-pallet-DevOps-Hacking-with-Clojure) | [Website & Blog](https://domaindrivenarchitecture.org)

## compatability
dda-pallet is compatible to the following versions
 * pallet 0.8
 * clojure 1.7
 * (x)ubunutu 16.04

## Usage documentation
This crate provides integration tests for servers.

TODO: remote-whitebox / local-whitebox

TODO: Configuration

TODO: test with jar-file


## Reference
### Schema for Targets

```clojure
(def ExistingNode {:node-name s/Str
                   :node-ip s/Str})

(def ProvisioningUser {:login s/Str
                       (s/optional-key :password) s/Str})

(def Targets {:existing [existing/ExistingNode]
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
