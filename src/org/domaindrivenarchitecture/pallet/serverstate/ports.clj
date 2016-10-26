(ns org.domaindrivenarchitecture.pallet.serverstate.ports
  (:require
    [org.domaindrivenarchitecture.pallet.serverstate.resources :refer :all]
    [org.domaindrivenarchitecture.pallet.serverstate.tests :refer :all]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :refer :all]))

;;; Predefined resource: open ports
(def res-id-open-ports ::open-ports)
(defn define-resources-netstat
  "Defines the netstat resource. 
   This is automatically done serverstate crate is used."
  []
  (define-resource-from-script res-id-open-ports "netstat -tulpen"))

(script/defscript script-test-port-open
  "Checks if a port is open."
  [port-number])
(script/defimpl script-test-port-open :default 
  [port-number]
  (if ("grep " (str ":" ~port-number))
    (do 
      (println (str "The port " ~port-number " is open"))
      (exit 0))
    (do 
      (println (str "The port  " ~port-number " is not open."))
      (exit 1))))
(defn test-port-open
  "Tests if a created resource is not empty (=success) or is empty (=failure)"
  [port-number]
  (testnode-resource  res-id-open-ports (script-test-port-open port-number)))

(script/defscript script-test-port-open-on-process
  "Checks if a port is open on a specified process-name."
  [port-number process-name])
(script/defimpl script-test-port-open-on-process :default 
  [port-number process-name]
  (if (pipe ("grep " (str ":" ~port-number)) ("grep " ~process-name))
    (do 
      (println (str "The port " ~port-number " is open on process " ~process-name))
      (exit 0))
    (do 
      (println (str "The port  " ~port-number " is not open on process " ~process-name))
      (exit 1))))
(defn test-port-open-on-process
  "Tests if a created resource is not empty (=success) or is empty (=failure)"
  [port-number process-name]
  (testnode-resource  res-id-open-ports (script-test-port-open-on-process port-number process-name)))