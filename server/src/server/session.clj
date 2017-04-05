(ns server.session
  (:require [adi.core :as adi]
            [adi.data.common :refer [iid]]
            [taoensso.timbre :refer [log spy]]
            [datomic.api :as datomic]))

(defn list
  [db]
  (adi/select db :session))

(defn join
  "joins user with given name into the specified session"
  [db session-name username]
  (let [userid (:id (:db (adi/select db {:account/user username} :first :ids)))]
    (when userid
      (adi/transact! db [{:db/id (iid)
                          :session/name session-name
                          :session/users userid}]))))

(defn attach
  "attaches a session to a project"
  [db session-name project-name]
  (let [project-id (:id (:db (adi/select db {:project/name project-name} :first :ids)))]
    (when project-id
      (adi/transact! db [{:db/id (iid)
                          :session/name session-name
                          :session/projects project-id}]))))

(defn detail
  [db session-name]
  (adi/select db {:session/name session-name}
              :pull {:session {:users :checked
                               :projects :checked
                               :name :checked}}))
