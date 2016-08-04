(ns user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [server.systems :refer [dev-system]]
            [environ.boot :refer [environ]]))

;; (System/setProperty "http-port" "3028")
(set-init! #'dev-system)
