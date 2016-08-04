(ns client.ws
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.timbre :refer-macros (log spy)]
            [taoensso.sente :as sente :refer (cb-success?)]
            [re-frame.core :as re-frame]))

(def +default-timeout+ 1000)

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
       {:type :auto ; e/o #{:auto :ajax :ws}
       })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (log :info "Unhandled event:" event))

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (log :debug "got chsk/state:" ?data)
  (mapv
   (fn [data] (re-frame/dispatch [:ws/connected (spy :warn (:open? data))]))
   ?data))


(defmethod event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (log :debug "handshake:" ?data)
  ; ???
  )


(defmethod event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (log :debug "received:" ?data)
  (re-frame/dispatch [:ws/receive ?data]))


(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (event-msg-handler ev-msg))

(defn callback-handler [reply]
  (if (sente/cb-success? reply)
    (re-frame/dispatch (conj [:ws/receive (first reply)] (rest reply)))
    (log :error "error during callback" reply)))


(defn send!
  ([event]
  (send! event +default-timeout+ callback-handler))
  ([event timeout cb]
  (chsk-send! event timeout cb)))

(defn reconnect
  ""
  []
  (log :warn "reconnecting ws")
  (sente/chsk-reconnect! chsk))


(def router (atom nil))
(defn stop-router! [] (when-let [stop-f @router] (stop-f)))
(defn start-router! []
  (stop-router!)
  (log :info "starting router")
  (reset! router (sente/start-chsk-router! ch-chsk event-msg-handler*)))
