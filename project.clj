(defproject org.domaindrivenarchitecture/dda-servertest-crate "0.1.0-SNAPSHOT"
  :description "common utils for dda pallet"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.3"]
                 [ch.qos.logback/logback-classic "1.1.8"]
                 [com.palletops/pallet "0.8.12"]
                 [org.domaindrivenarchitecture/dda-pallet-commons "0.3.0"]
                 [org.domaindrivenarchitecture/dda-pallet "0.3.0"]                 
                 ]
  :source-paths ["src" "test-utils"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev
             {:source-paths ["integration"]
              :dependencies
              [[org.clojure/test.check "0.9.0"]
               [com.palletops/stevedore "0.8.0-beta.7"]
               [com.palletops/pallet "0.8.12" :classifier "tests"]
               [org.domaindrivenarchitecture/dda-user-crate "0.3.5-SNAPSHOT"]
               [org.domaindrivenarchitecture/dda-init-crate "0.2.1-SNAPSHOT"]
               [org.domaindrivenarchitecture/pallet-aws "0.2.8-SNAPSHOT"]
               [ch.qos.logback/logback-classic "1.1.8"]
               [org.slf4j/jcl-over-slf4j "1.7.22"]
              ]}
             :plugins [[lein-sub "0.3.0"]]
             :leiningen/reply
             {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.22"]]
              :exclusions [commons-logging]}}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test" "integration"]
                        :resource-paths ^:replace []}})