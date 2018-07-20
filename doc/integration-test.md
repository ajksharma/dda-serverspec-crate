# Running integration tests
```
(in-ns 'dda.pallet.dda-serverspec-crate.app.instantiate-existing)
```

# certificate
```
(serverspec :domain "integration/resources/certificate-file.edn"
            :targets "integration/resources/remote-pwd-target.edn")
(serverspec :domain "integration/resources/certificate-file.edn"
            :targets "integration/resources/localhost-target.edn")
(serverspec :domain "integration/resources/certificate-file.edn"
            :targets "integration/resources/remote-key-target.edn")
```

# command
```
(serverspec :domain "integration/resources/command.edn"
            :targets "integration/resources/remote-pwd-target.edn")
(serverspec :domain "integration/resources/command.edn"
            :targets "integration/resources/localhost-target.edn")
(serverspec :domain "integration/resources/command.edn"
            :targets "integration/resources/remote-key-target.edn")
```

# file
```
(serverspec :domain "integration/resources/file.edn"
            :targets "integration/resources/remote-pwd-target.edn")
(serverspec :domain "integration/resources/file.edn"
            :targets "integration/resources/localhost-target.edn")
(serverspec :domain "integration/resources/file.edn"
            :targets "integration/resources/remote-key-target.edn")
```


# http
```
(serverspec :domain "integration/resources/http-cert.edn"
            :targets "integration/resources/remote-pwd-target.edn")
(serverspec :domain "integration/resources/http-cert.edn"
            :targets "integration/resources/localhost-target.edn")
(serverspec :domain "integration/resources/http-cert.edn"
            :targets "integration/resources/remote-key-target.edn")
```
