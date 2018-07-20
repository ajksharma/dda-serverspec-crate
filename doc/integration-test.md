(in-ns 'dda.pallet.dda-serverspec-crate.app.instantiate-existing)
(serverspec :domain "integration/resources/http-cert.edn"
            :targets "integration/resources/remote-pwd-target.edn")
(serverspec :domain "integration/resources/http-cert.edn"
            :targets "integration/resources/localhost-target.edn")
(serverspec :domain "integration/resources/http-cert.edn"
            :targets "integration/resources/remote-key-target.edn")
