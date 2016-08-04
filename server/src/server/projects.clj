(ns server.projects
  (:require [adi.core :as adi]))

(defn create
  [db data]
  (adi/insert! db {:project data}))

(defn list
  [db]
  (adi/select db :project))
