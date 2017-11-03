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

### Usage Summary
1. Download jar from the releases page of this repo
2. Deploy jar on source machine
3. Create test.edn (Domain-Schema for Tests) and target.edn (Schema for Targets) according to the reference and our example configs
4. Run jar with the following order and inspect the output.
```bash
java -jar dda-serverspec-crate-standalone.jar test.edn targets.edn
```

### Remote-whitebox
The first is the remote-whitebox test.

![alt text](./ServerSpecRemoteWhitebox.png "ServerSpecRemoteWhitebox")

Here the serverspec crate can be used from a source machine to test remote target machines.
The crate can be used here either through the source code or the jar.

### Local-whitebox
The second scenario is the local-whitebox test.

![alt text](./ServerSpecLocalWhitebox.png "ServerSpecLocalWhitebox")

Here the serverspec crate can be used directly on a target machine to test itself.
A possible use case would be to deploy the jar on the target and execute it there via the terminal.

### Configuration
The configuration determines what integration tests are going to be executed. Two external config files have to be created.
To illustrate this, we provided example files in the source folder called "targets.edn" and "test.edn".

#### Targets config
```clojure
{:existing [{:node-name "test-vm1"
             :node-ip "35.157.19.218"}
            {:node-name "test-vm2"
             :node-ip "18.194.113.138"}]
 :provisioning-user {:login "ubuntu"}}
```
The keyword :existing has to be assigned a vector that contains maps of the information about the nodes.
The nodes are the target machines that will be tested. The node-name has to be set to be able to identify the target machine and the node-ip has to be set so that the source machine can reach it.
--> local? http://127.0.0.1/??
--> user aws? multiple nodes?
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
The test config determines the test that are executed. At the moment we have four different types of
tests that can be configured. The exact details can be found down in the reference.


### Test with jar-file
For ease of usage we created a jar file of the serverspec-crate. It can be found at [here](https://github.com/DomainDrivenArchitecture/dda-serverspec-crate/releases) or by navigating to the releases page of this repo.

```bash
java -jar dda-serverspec-crate-standalone.jar test.edn targets.edn
```

## Reference

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
The "targets.edn" has the schema of the Targets
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
