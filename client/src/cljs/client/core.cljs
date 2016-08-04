(ns client.core
    (:require [taoensso.timbre :refer-macros (log)]
              [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [client.handlers]
              [client.ws :as ws]
              [client.subs]
              [client.routes :as routes]
              [client.views :as views]
              [client.config :as config]))


(defn dev-setup []
  (when config/debug?
    (log :info "dev mode")
    (devtools/install!)))

(defn mount-root []
  (reagent/render [views/app-panel]
                  (.getElementById js/document "app")))


(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root)
  (ws/start-router!)
  :running)
