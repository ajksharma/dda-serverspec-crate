# dda-servertest-crate

This crate provides integration tests for servers. Tests are driven by the pallet-node and executed either remote (by ssh) or on localhost (direct).
Tests are executed as follows:
1. phase settings
   1. execute a minimal bash on system under test
   2. parse script output in clojure.
   3. store parsed output as fact in session
2. phase test
   1. get fact from session
   2. compare expectation against fact

## compatability
dda-pallet is compatible to the following versions
 * pallet 0.8
 * clojure 1.7
 * (x)ubunutu 16.04

## Usage documentation
### Define Resources to test
You might also use the whole file as a resource which means we just create a copy of the file:

```clojure
{:package {:[package-name] {:installed? [true|false]}
           ... }
 :netstat {:[service-name] {:port "22"}
           ... }
 :file {:[file-name] {:path "[an absolut path]]"
                      :exist? [true|false]}
         ... }}
```

### Use server-test standalone

```clojure
(ns [your-ns-goes-here]
  (:require
    [dda.cm.operation :as operation]
    [dda.cm.existing :as existing]
    [dda.pallet.domain.dda-servertest-crate :as domain]))

(def provisioning-ip
  "[your ip]]")

(def provisioning-user
  {:login "[your username]"
   :password "[your password, if your ssh key is not allready authorized]"})

(def domain-config {:netstat {:sshd {:port "22"}}
                    :file {:root-sth {:path "/root"
                                      :exist? true}
                           :etc {:path "/etc"
                                 :exist? true}
                           :absent {:path "/absent"
                                    :exist? false}}})

(defn provider []
  (existing/provider provisioning-ip "[choose a node-id]" "dda-servertest-group"))

(defn integrated-group-spec []
  (merge
    (domain/dda-servertest-group (domain/crate-stack-configuration domain-config))
    (existing/node-spec provisioning-user)))

(defn server-test []
  (operation/do-server-test (provider) (integrated-group-spec)))

(server-test)
```

##More information
* dda-pallet is a clojure based config management system build upon the great config management framework [pallet](https://github.com/pallet/pallet) build by to Hugo Duncan.
* Website & Blog: [DomanDrivenArchitecture.org](https://domaindrivenarchitecture.org)
* Commercial: [meissa-gmbh.de](https://meissa-gmbh.de)
* Slack-Channel: [dda-pallet@clojurians.slack.com](https://clojurians.slack.com/messages/C5GDWDF28/)
* [Requirements & Architecture](https://dda.gitbooks.io/domaindrivenarchitecture/content/en/80_config_management/index.html)

## License
Published under [apache2.0 license](LICENSE.md)
