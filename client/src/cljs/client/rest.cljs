(ns client.rest
  (:require [ajax.core :refer [POST GET]]
            [client.ws :as ws]
            [taoensso.timbre :refer-macros (log spy)]))

(defn login
  ""
  [user pass]
  (log :info "sending POST")
  (POST "/login" {:params {:user user :pass pass}
                  :handler ws/reconnect})
  )
