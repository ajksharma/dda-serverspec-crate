(defproject dda/dda-servertest-crate "0.2.0-SNAPSHOT"
  :description "common utils for dda pallet"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.3"]
                 [com.palletops/pallet "0.8.12"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [org.domaindrivenarchitecture/dda-pallet-commons "0.3.1"]
                 [dda/dda-pallet "0.4.0-SNAPSHOT"]]
  :source-paths ["src" "test-utils"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:source-paths ["integration"]
                   :dependencies
                   [[org.domaindrivenarchitecture/pallet-aws "0.2.8-SNAPSHOT"]
                    [org.clojure/test.check "0.9.0"]
                    [com.palletops/pallet "0.8.12" :classifier "tests"]
                    [ch.qos.logback/logback-classic "1.1.8"]
                    [org.slf4j/jcl-over-slf4j "1.7.22"]]
                   :plugins [[lein-sub "0.3.0"]]
                   :leiningen/reply
                   {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.22"]]
                    :exclusions [commons-logging]}}}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test" "integration"]
                        :resource-paths ^:replace []}})
