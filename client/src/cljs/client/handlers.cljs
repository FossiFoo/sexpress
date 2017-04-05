(ns client.handlers
  (:require [client.db :as db]
            [re-frame.core :as re-frame :refer [dispatch register-handler trim-v]]
            [taoensso.timbre :refer-macros (logf log spy)]
            [client.rest :as rest]
            [client.ws :as ws]))


(def client-middleware [trim-v])

;; DB init =========================================================================================

(register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

;; view ============================================================================================

(register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

;; Raw websocket ===================================================================================

(register-handler
  :ws/connected
  (fn [db [_ connected?]]
    (if connected? (ws/send! [:state/sync]))
    (assoc db :ws/connected connected?)))

(register-handler
  :ws/receive
  (fn [db [_  command data]]
    (log :info "Received:" command data)
    (dispatch [command data])
    db))

(register-handler
  :ws/send
  (fn [db [_  command data]]
    (logf :info "Sending: %s %s" command data)
    (ws/send! (if data [command data] [command]))
    db))

;; accounts ========================================================================================

(defn handle-account-login
  [db [user pass]]
  (log :info "logging in" user pass)
  (rest/login user pass)
  (assoc db :account {:login user}))

(register-handler
 :account/login
 client-middleware
 handle-account-login)

;; console commands ================================================================================

(register-handler
 :editor/command-set
  (fn [db [_ data]]
    (db/command-persist db data)))

(register-handler
 :console/immidiate
  (fn [db [_ [command data]]]
    (log :info "immidiate command" command data)
    (dispatch [:editor/command-exec])
    (db/command-direct db {:command command :params data})))

(register-handler
 :editor/command-exec
  (fn [db [_]]
    (let [{cmd :command params :params} (db/get-editor-command db)]
      (log :info "executing command" cmd params)
      (dispatch (conj [:ws/send cmd] params))
      (dispatch [:editor/command-set nil])
      (spy :error (db/add-editor-history-entry db [cmd params])))))

;; local state =====================================================================================

(register-handler
 :state/active-project
  (fn [db [_ data]]
    (log :debug "project active" data)
    (assoc db :state data)))

;; editing session =================================================================================

(register-handler
 :session/list
  (fn [db [_ data]]
    (log :debug "session list" data)
    (assoc-in db [:sessions :list] data)))

(register-handler
 :session/detail
  (fn [db [_ data]]
    (log :debug "session detail" data)
    (assoc-in db [:sessions :detail] data)))

;; projects ========================================================================================

(register-handler
 :projects/list
  (fn [db [_ data]]
    (log :debug "projects list" data)
    (assoc db :projects data)))

;; Outdated? =======================================================================================

(register-handler
  :state/sync
  (fn [db [_ new-db]]
    (logf :error "Syncing state %s" new-db)
    (assoc db :shared new-db)))

(register-handler
  :noop
  (fn [db _]
    db))
