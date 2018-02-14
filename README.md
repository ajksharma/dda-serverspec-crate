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
This crate provides integration tests for servers. E.g. it can be used to check if certain files or folders are existing, if packages are installed, if certain programs are running, etc.

### Usage Summary
1. Download the jar from the releases page of this repository (e.g. dda-serverspec-crate-x.x.x-standalone.jar)
2. Deploy the jar on the source machine
3. Create test.edn (Domain-Schema for Tests) and target.edn (Schema for Targets) according to the reference and our example configs
4. (optional) If you want to ensure, that certain test tools (like netcat or netstat) are present on the target system, you should execute a one-time install (using the ```--install-dependencies``` option):
```bash
java -jar dda-serverspec-crate-standalone.jar --install-dependencies --targets targets.edn test.edn
```
5. Run the jar with the following options and inspect the output.
```bash
java -jar dda-serverspec-crate-standalone.jar --targets targets.edn test.edn
```

### Remote white-box
![ServerSpecRemoteWhitebox](./doc/ServerSpecRemoteWhitebox.png)

For the remote white-box test, the serverspec crate can be used from a source machine to test different aspects of the remote target machines.
This can be achieved by either utilizing the jar provided on GitHub (as described above), or by calling the functions of the Clojure source code directly.

### Configuration
Two configuration files are necessary to call the main method successfully. These files specify both WHAT to test resp. WHERE. In detail: the first file defines the configuration for the actual tests performed by this crate, while the second configuration file specifies the target nodes/systems, on which the tests will be performed.

The following examples will make the creation of these files more clear. Please note, that we will reference the files for the test configuration and target configuration "test.edn" and "targets.edn", respectively.

#### Targets config example
```clojure
{:existing [{:node-name "test-vm1"
             :node-ip "35.157.19.218"}
            {:node-name "test-vm2"
             :node-ip "18.194.113.138"}]
 :provisioning-user {:login "ubuntu"}}
```
The keyword ```:existing``` has to be assigned a vector, that contains maps with the information about the nodes.
The nodes are the target machines that will be tested. The ```node-name``` has to be set to be able to identify the target machine and the ```node-ip``` has to be set so that the source machine can reach it.
The ```provisioning-user``` has to be the same for all nodes that will be tested. Furthermore, if the public-key of the executing host is authorized on all target nodes, a password for authorization can be omitted. If this is not the case, the provisioning user has to contain a password. This can be seen in the schema for the targets.

#### Test config example
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
The test config file determines the tests that are executed. For example the part containing ```{:path "/root"}``` checks if the folder ```/root``` exists.
At the moment we have four different types of tests that can be configured. The exact details can be found in the reference below.

### Test with jar-file
For a more convenient usage we created a jar file of the serverspec-crate. It can be found at [here](https://github.com/DomainDrivenArchitecture/dda-serverspec-crate/releases) or by navigating to the releases page of this repo.

```bash
java -jar dda-serverspec-crate-standalone.jar test.edn targets.edn
```

## Reference
We provide two levels of API - domain is a high level API with many built-in conventions. If these conventions don't fit your needs, you can use our low-level API (infra) and realize your own conventions.

### Domain API

#### Targets
The schema of the domain layer for the targets is:
```clojure
(def ExistingNode
  "Represents a target node with ip and its name."
  {:node-name s/Str
   :node-ip s/Str})

(def ExistingNodes
  "A sequence of ExistingNodes."
  {s/Keyword [ExistingNode]})

(def ProvisioningUser
  "User used for provisioning."
  {:login s/Str
   (s/optional-key :password) secret/Secret})

(def Targets
  "Targets to be used during provisioning."
  {:existing [ExistingNode]
   (s/optional-key :provisioning-user) ProvisioningUser})
```
The "targets.edn" file has to match this schema.

#### Tests
The schema for the tests is:
```clojure
(def ServerTestDomainConfig
  {(s/optional-key :package) [{:name s/Str
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
                              (s/optional-key :reachable?) s/Bool}]
   (s/optional-key :certificate) [{:file s/Str                ;incl path as e.g. /path/file.crt
                                   :expiration-days s/Num}]   ;min days certificate must be valid
   (s/optional-key :http) [{:url s/Str                        ;url e.g. http://google.com
                            :expiration-days s/Num}]})        ;min days certificate must be valid
```
The "tests.edn" file has to match this schema.
The default value is that the test expects a positive boolean (e.g. :reachable? true) and this value can be omitted.

### Infra API
The infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions.
On infra level we distinguish between collecting facts (done in the settings phase without side effects) and testing (done in test phase intentionally without side effects).
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
This results are returned in a map that follows the schema depicted above.

## License
Published under [apache2.0 license](LICENSE.md)
