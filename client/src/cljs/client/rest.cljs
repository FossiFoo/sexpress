(ns client.rest
  (:require [ajax.core :refer [POST GET]]
            [taoensso.timbre :refer-macros (log spy)]))

(defn login
  ""
  [user pass]
  (log :info "sending POST")
  ;; (POST "/login" {:params {:user user :pass pass}
  ;;                 :handler ws/reconnect})
  )
