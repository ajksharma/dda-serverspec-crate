### Infra-API
The infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions.
On infra level we distinguish between collecting facts (done in the settings phase without side effects) and testing (done in test phase intentionally without side effects).
Settings can also be used without tests in order to provide informations for conditional installations / configurations.

The schema is:
```clojure
(def ServerTestConfig {
  (optional-key :netstat-fact) Any,      ; parsed result of "netstat -tulpen". Any is ignored. Fact can only be collected by sudoers / root.
  (optional-key :package-fact) Any})     ; parsed result of "dpkg -l". Any is ignored.
  (optional-key :file-fact)              ; parsed result of "find [path] -prune -printf \"%p'%s'%u'%g'%m'%y'%c'%t'%a\\n\", fact can be collected only if executing user has access.
  {Keyword {:path Str}},
  (optional-key :netcat-fact)            ; parsed result of "nc [host] -w [timeout] && echo $?"
  {Keyword {:port Num,
            :host Str,                   ; may be ip or fqdn
            :timeout Num}},              ; timeout given in seconds
  (optional-key :certificate-file-fact)  ; fact can only be collected is executing user has access.
  {Keyword {:file Str}}                  ; with full path
  (optional-key :http-fact)
  {Keyword {:url Str}}                   ; full url e.g. https://google.com
  (optional-key :package-test)
  {Keyword {:installed? Bool}},
  (optional-key :netstat-test)
  {Keyword {:running? Bool,
            :port Str,
            (optional-key :ip) Str,
            (optional-key :exp-proto) Str}},
  (optional-key :file-test)
  {s/Keyword {:exist? Bool
              (optional-key :mod) Str
              (optional-key :user) Str
              (optional-key :group) Str
              (optional-key :type) Str
              (optional-key :link-to) Str}}
  (optional-key :netcat-test)
  {Keyword {:reachable? Bool}},
  (optional-key :certificate-file-test)
  {Keyword {:expiration-days Num}}
  (optional-key :http-test)
  {Keyword {:expiration-days Num}}})
```
On the level of the infrastructure we break down the tests into gathering the facts and testing them against the expected value.
These results are returned in a map that follows the schema depicted above.
