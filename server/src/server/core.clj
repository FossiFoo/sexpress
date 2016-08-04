(ns server.core
  (:require [system.repl :refer [set-init! go]]
            [server.systems :refer [prod-system]]))

(defn -main
  [& args]
  (let [system (or (first args) #'prod-system)]
    (set-init! system)
    (go)))
