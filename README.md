# dda-servertest-crate

## Overview

This crate realizes the execution of tests. The concept of the usage is to __define resources__ which are outputs of bash scripts. These __outputs can then be tested__ either on the machine running the test using clojure tools or on the target node using a bash script.

## Usage documentation

### Resources

Resources are outputs of bash scripts that are executed on the tested node. A resource is made up of

  * a unique __resource-key__, which must be used to identify the resource in tests
  * a __ bash script__, which creates the resource and the 
  * __output__ of this script. If we refer to "the output resource" we might write short "the resource" itself. 
  
To ensure uniqueness of resource-keys you should use namespaced keywords like `::my-example-resource`.

Aside to the output, there is the __transformed output__ of the script belonging to the resource. See below for more information.

For each phase run a __resource directory__ is created in `/home/pallet/state` and for each defined resource with a unique resource-key there will be a corresponding script (.sh) and output (.rc) file. 

### Defining Resources

Resources are defined using `define-resource-from-script` by providing a script and resource-key. 

Example: Create a resource containing all users and their home directories.

```clojure
(require '[org.domaindrivenarchitecture.pallet.serverstate.resources :as res])

; in test-phase:
(res/define-resource-from-script ::user-list "cut -d: -f1,6 /etc/passwd")
```

This uses `cut` select only the first the sixth entry from the passwd-file. The resource will look like this:
```
root:/root
daemon:/usr/sbin
bin:/bin
sys:/dev
sync:/bin
games:/usr/games
...
```

You might also use the whole file as a resource which means we just create a copy of the file:

```clojure
(require '[org.domaindrivenarchitecture.pallet.serverstate.resources :as res])

(res/define-resource-from-script ::passwd-file "cat /etc/passwd")
; there are shorthands for creating often used resources like files
; in this case we could write as well:
(res/define-resource-from-file ::passwd-file "/etc/passwd")
```

### Transforming Resources

Having a large string as resource might not be very convenient to work with. You can
specify a transform-fn when defining resource to parse the result. If no transformer
is supplied the output and the transformed output both refer to the raw output string.

Example: Transforming the user-list in a clojure structure

```clojure
; our transform functions takes the output of the script
; splits on lines and the ':' delimiter.
; then all empty entries are filtered
(defn transform-user-list
  [output] 
  (filter #(= 2 (count %)) 
    (map #(clojure.string/split % #":") (clojure.string/split-lines output))))
    
; use this when defining the resource in test-phase
(require '[org.domaindrivenarchitecture.pallet.serverstate.resources :as res])

(res/define-resource-from-script ::user-list "cut -d: -f1,6 /etc/passwd" transform-user-list) 
```

### Tests: Local in clojure

Doing a __local test in clojure__ means there are no further actions performed on the target node
when performing the test. This means you can only access a previous defined resource for the test.

Such __a test is a plain clojure function getting the (by default transformed) resource as argument__. The function must evaluate to `false` or `nil` if the test failed and can have any other result if the test passes.

Use __outputs to stdout__ (e.g. `println`) if you like to have any outputs in the test results.

To __enable the test__, use `testclj-resource` from the test package. You can choose to use the transformed output (default) or the raw string output.

Example: Checking for the existence of a user using the previous defined `::user-list` resource.
```
(defn test-palletuser-existing
  "Tests if there is a pallet user in the user-list. Here user-list must be
   a vector of entries where every entry is a vector containing username and
   homefolder as string.
   
   e.g: [ [\"root\" \"/root\"] ... ] "
  [user-list]
  (println "Testing if user pallet exists.")
  (= 1 (count (filter #(= (first %) "pallet") user-list))))
  
  
; In test-phase:
(require '[org.domaindrivenarchitecture.pallet.serverstate.tests :as tests])

(tests/testclj-resource ::user-list test-palletuser-existing)
```

### Tests: Bash on the remote node

You might as well perform some tests, that require more information than the resources itself. Regarding our user-list example this might be a test to ensure all home directories are existing.

__Remote tests in bash__ are scripts (in plaintext or pallet stevedore notation) that receive the specified resource as input and signal with their exit code if the test passed. Hereby passed means exit code 0 and a failure is indicated by any other exit code. To define such a test use `testnode-resource` from the test package.

Example: To get the idea -- the syntax is
```clojure
; In test-phase:
(require '[org.domaindrivenarchitecture.pallet.serverstate.tests :as tests])
(require '[pallet.stevedore :refer :all])

(tests/testnode-resource ::some-resource "echo I always pass && exit 0")
```

Example: Check if all home directories are existing
```clojure
; In test-phase:
(require '[org.domaindrivenarchitecture.pallet.serverstate.tests :as tests])
(require '[pallet.stevedore :refer :all])

(tests/testnode-resource
  ::user-list
  (script
   (set! exitcode 0)
   (while 
     ("read" line)
     (set! user @((pipe (println @line) ("cut -f1 -d:")))) 
     (set! homedir @((pipe (println @line) ("cut -f2 -d:"))))
     (if (not (directory? @homedir))
       (do
         (println "Home" @homedir "of user" @user "does not exist!")
         (set! exitcode 1))))
   ("exit" @exitcode)))
```

## Predefined resources and tests

### Apt package manager
(todo description)

### Processes running
(todo description)

## License
Published under [apache2.0 license](LICENSE.md)
