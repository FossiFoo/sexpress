(ns server.systems
  (:require
   [taoensso.timbre :refer [log spy]]
   [system.core :refer [defsystem]]
   [com.stuartsierra.component :as component]
   (system.components
    [http-kit :refer [new-web-server]]
    [sente :refer [new-channel-socket-server sente-routes]]
    [datomic :refer [new-datomic-db]]
    [adi :refer [new-adi-db]]
    [endpoint :refer [new-endpoint]]
    [middleware :refer [new-middleware]]
    [handler :refer [new-handler]])
   [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]
   [taoensso.sente.packers.transit :refer (get-transit-packer)]
   [ring.middleware.defaults :refer [wrap-defaults]]
   [ring.middleware.transit :refer [wrap-transit-body]]
   [ring.middleware.json :refer [wrap-json-body]]
   [server.handler :refer [middleware-defaults ring-handler sente-handler]]
   [server.schema :refer [+schema+]]
   [environ.core :refer [env]]))

(def +port+ (try (Integer. (env :http-port)) (catch Exception e 3025)))

(defsystem dev-system
  [:web (component/using (new-web-server +port+) [:handler])
   ;; :db (new-datomic-db "datomic:mem://sexpress")
   :db (new-adi-db "datomic:mem://test" +schema+ true true)
   :sente-endpoint (component/using
                    (new-endpoint sente-routes)
                    [:sente])
   :routes (component/using
            (new-endpoint ring-handler)
            [:db])
   :middleware (new-middleware {:middleware [[wrap-transit-body wrap-json-body] [wrap-defaults :defaults]]
                                :defaults (middleware-defaults)
                                :not-found  "<h2>The requested page does not exist.</h2>"})
   :handler (component/using
             (new-handler)
             [:sente-endpoint :routes :middleware])
   :sente (component/using (new-channel-socket-server sente-handler
                                                      sente-web-server-adapter
                                                      {:wrap-component? true
                                                       :user-id-fn (fn [_] (str (java.util.UUID/randomUUID)))
                                                       ;; :packer (get-transit-packer)
                                                       })
                           [:db])])

(defsystem prod-system
  [:web (new-web-server +port+)])
