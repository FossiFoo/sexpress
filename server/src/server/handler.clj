(ns server.handler
  (:require
   [taoensso.timbre :refer [log spy]]
   [compojure.route :as route]
   [compojure.core :refer [routes GET POST ANY]]
   [ring.util.response :refer [response content-type charset]]
   [ring.util.request :refer [body-string]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :refer [file-response resource-response
                                        status content-type]]
   [server.db :refer :all]
   [server.html :as html]
   [server.editor :as editor]
   [server.session :as session]
   [server.projects :as projects]
   [adi.core :as adi]
   [system.repl :refer [system]]))

(require '("boot.util"))

(defn middleware-defaults []
  (assoc-in site-defaults [:security :anti-forgery] false))

(defn maybe-reply
  [?reply-fn body]
  (when ?reply-fn
    (?reply-fn body)))

(defmacro assert-reply
  [?reply-fn body]
  `(if-not ~?reply-fn
    (log :error "no reply fn")
    (~?reply-fn ~body)))

(defn- init-db
  [db]
  (adi/insert! db {:account {:user "fossi"}}))

(defn- handle-login
  [db session user pass]
  (init-db db)
  (log :error session user pass)
  {:status 200 :session (assoc session :uid user)})

(defn ring-handler [{db :db}]
  (routes
   (GET "/" [] (html/index))
   (POST "/login" {session :session {user :user pass :pass} :body}
         (handle-login db session user pass))
   (route/files "/" {:root "resources/public" :allow-symlinks? true})
   (route/not-found "404")))

(defn- handle-project-list
  [db]
  [:projects/list (projects/list db)])

(defn- handle-project-create
  [db data ?reply-fn]
  (projects/create db (select-keys data [:name]))
  (maybe-reply ?reply-fn (handle-project-list db)))

(defn- handle-session-detail
  [db session-name]
  [:session/detail (session/detail db session-name)])

(defn- handle-session-attach
  [db {session-name :session project-name :project} ?reply-fn]
  (session/attach db session-name project-name)
  (maybe-reply ?reply-fn (handle-session-detail db session-name)))

(defn- handle-session-join
  [db uid session-name ?reply-fn]
  (session/join db session-name uid)
  (maybe-reply ?reply-fn (handle-session-detail db session-name)))

(defn- handle-session-list
  [db]
  (let [sessions (session/list db)]
    (log :error "session list" sessions)
    [:session/list sessions]))

(defn- handle-edit-command
  [db [command data] reply-fn]
  (reply-fn (editor/command db command data)))


(defn reply-logged
  [?reply-fn reply]
  (when ?reply-fn
    (log :info "answer" reply)
    (?reply-fn reply)))

(defn- handle-exception
  [e]
  [:error/exception e])

(defn sente-handler [{db :db}]
  (fn [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
    (let [session (:session ring-req)
          headers (:headers ring-req)
          reply-fn #(reply-logged ?reply-fn %)
          uid     (:uid session)
          data    ?data]

      (log :info "event:" event)
      (log :info "id:" id)
      (log :info "?data:" ?data)
      (log :info "uid:" uid)

      (try
        (case id
          :test/init (init-db db)
          :session/list (assert-reply ?reply-fn (handle-session-list db))
          :session/join (handle-session-join db uid data reply-fn)
          :session/attach (handle-session-attach db data reply-fn)
          :session/detail (assert-reply ?reply-fn (handle-session-detail db data))
          :project/list (assert-reply ?reply-fn (handle-project-list db))
          :project/create (handle-project-create db data reply-fn)
          :editor/command (handle-edit-command db data reply-fn)
          :chsk/uidport-open nil
          :chsk/uidport-close nil
          (do
            (log :warn "ignoring unknown event" id)
            (assert-reply ?reply-fn [:noop nil])))
        (catch Throwable e (reply-fn (handle-exception e)))))))
