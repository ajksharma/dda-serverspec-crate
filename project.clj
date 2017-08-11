(defproject dda/dda-serverspec-crate "0.2.1-SNAPSHOT"
  :description "A crate to get facts from server nodes and test these facst against your expectation."
  :url "https://domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.6"]
                 [com.palletops/pallet "0.8.12"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [dda/dda-pallet "0.5.3"]]
  :source-paths ["src" "test-utils"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:source-paths ["integration"]
                   :dependencies
                   [[org.domaindrivenarchitecture/pallet-aws "0.2.8.2"]
                    [com.palletops/pallet "0.8.12" :classifier "tests"]
                    [ch.qos.logback/logback-classic "1.2.3"]
                    [org.slf4j/jcl-over-slf4j "1.8.0-alpha2"]]
                   :plugins [[lein-sub "0.3.0"]]
                   :leiningen/reply
                   {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.22"]]
                    :exclusions [commons-logging]}}}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test" "integration"]
                        :resource-paths ^:replace ["dev-resources"]}})
