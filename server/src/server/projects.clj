(ns server.projects
  (:require [clojure.spec :as s]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.walk :as walk]
            [server.db :as db]))

(s/def :sexpress/project-list (s/coll-of :sexpress/project))

(defn create
  [db data]
  (db/insert! db {:project data}))

(s/fdef create
        :args (s/cat :db any? :data #{:project})
        :ret any?)

(defn namespace-keywords
  ""
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(keyword "sexpress" (name k)) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn list
  [db]
  (let [db-val (map :project (db/select db :project))
        transformed (namespace-keywords db-val)
        val (s/conform :sexpress/project-list transformed)]
    (when (= :clojure.spec/invalid val)
      (s/explain :sexpress/project-list transformed))
    val))

(s/fdef list
        :args (s/cat :db any?)
        :ret :sexpress/project-list)
