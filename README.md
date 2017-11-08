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

### Usage Summary
1. Download the jar from the releases page of this repository
2. Deploy jar on the source machine
3. Create test.edn (Domain-Schema for Tests) and target.edn (Schema for Targets) according to the reference and our example configs
4. Run the jar with the following options and inspect the output.
```bash
java -jar dda-serverspec-crate-standalone.jar test.edn targets.edn
```

### Remote-whitebox
![alt text](./ServerSpecRemoteWhitebox.png "ServerSpecRemoteWhitebox")

For the remote whitebox test, the serverspec crate can be used from a source machine to test remote target machines.
This can be achieed by either utilizing the jar provided on GitHub, or by calling the functions of the source code directly.

### Configuration
Two configuration file will be necessary to call the main method successfully. The first argument requires the configuration for the actualy tests for this crate, while the second configuration
file is responsible for the target nodes.

The following examples will make the creation of these files more clear. Please note that, we will reference the files for the test configuration and target configuration "test.edn"
and "targets.edn", respectively.

#### Targets config Example
```clojure
{:existing [{:node-name "test-vm1"
             :node-ip "35.157.19.218"}
            {:node-name "test-vm2"
             :node-ip "18.194.113.138"}]
 :provisioning-user {:login "ubuntu"}}
```
The keyword :existing has to be assigned a vector that contains maps of the information about the nodes.
The nodes are the target machines that will be tested. The node-name has to be set to be able to identify the target machine and the node-ip has to be set so that the source machine can reach it.
The provsioning-user has to be the same for all nodes that are to be tested. Furthermore, if the public-key of the executing host is authorized on all target nodes, a password for authorization can be omitted. If this is not the case the provisioning user has to contain a password. This can be seen in the schema for the targets.

#### Test config Example
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
The test config determines the test that are executed.
At the moment we have four different types of tests that can be configured. The exact details can be found down in the reference.

### Test with jar-file
For a more convenient usage we created a jar file of the serverspec-crate. It can be found at [here](https://github.com/DomainDrivenArchitecture/dda-serverspec-crate/releases) or by navigating to the releases page of this repo.

```bash
java -jar dda-serverspec-crate-standalone.jar test.edn targets.edn
```

## Reference
We provide two levels of API - domain is a high level API with many build in conventions. If this conventions doe not fit your needs, you can use our low-level (infra) API and realize your own conventions.

### Domain API

#### Targets
The schema is:
```clojure
(def ExistingNode {:node-name Str                   ; your name for the node
                   :node-ip Str                     ; nodes ip4 address       
                   })

(def ProvisioningUser {:login Str                   ; user account used for provisioning / executing tests
                       (optional-key :password) Str ; password, is no authorized ssh key is avail.
                       })

(def Targets {:existing [ExistingNode]              ; nodes to test or install
              :provisioning-user ProvisioningUser   ; common user account on all nodes given above
              })
```
The "targets.edn" has the schema of the Targets

#### Tests
The schema is:
```clojure
(def ServerTestDomainConfig {(optional-key :package)
                              [{:name Str            
                                (optional-key :installed?) Bool}]
                             (optional-key :netstat)
                             [{:process-name Str
                               :port Str
                               (optional-key :running?) Bool
                               (optional-key :ip) Str
                               (optional-key :exp-proto) Str}]
                             (optional-key :file)
                             [{:path Str
                              (optional-key :exist?) Bool}]
                             (optional-key :netcat)
                             [{:host Str
                              :port Num
                              (optional-key :reachable?) Bool}]})
```
The "tests.edn" has the schema of the ServerTestDomainConfig-variable.
The default-value is that the test expects a positive boolean (e.g. :reachable? true) and this value can be omitted.

### Infra API
The Infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions.
On infra level we distinguishe between collecting facts (done in the settings phase without side effects) and test (done in test phase intentionally without sideeffects).
Settings can also be used without tests in order to provide informations for conditional installations / configurations.

The schema is:
```clojure
(def ServerTestConfig {
 (optional-key :netcat-test)
 {Keyword {:reachable? Bool}},          ; keyword is used to match test against fact
 (optional-key :netcat-fact)            ; parsed result of "nc [host] -w [timeout] && echo $?"
 {Keyword {:port Num,
           :host Str,                   ; may be ip or fqdn
           :timeout Num}},              ; timeout given in seconds
 (optional-key :netstat-test)
 {Keyword {:ip Str,
           :running? Bool,
           :port Str,
           :exp-proto Str}},
 (optional-key :netstat-fact) Any,      ; parsed result of "netstat -tulpen". Any is ignored.
 (optional-key :file-test)
 {Keyword {:exist? Bool}},
 (optional-key :file-fact)              ; parsed result of "find [path] -prune -printf \"%p'%s'%u'%g'%m'%y'%c'%t'%a\\n\"
 {Keyword {:path Str}},
 (optional-key :package-test)
 {Keyword {:installed? Bool}},
 (optional-key :package-fact) Any})      ; parsed result of "dpkg -l". Any is ignored.
```
On the level of the infrastructure we break down the tests into gathering the facts and testing them against the expected value.
This results in a map that follows the schema depicted above.

## License
Published under [apache2.0 license](LICENSE.md)
