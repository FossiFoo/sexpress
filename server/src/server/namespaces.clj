(ns server.namespaces
  (:require [clojure.spec :as s]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.walk :as walk]
            [server.db :as db]))

(s/def :sexpress/namespace-list (s/coll-of :sexpress/namespace))

(defn create
  [db data]
  (s/assert :sexpress/namespace data)
  (db/insert! db {:namespace data}))

(s/fdef create
        :args (s/cat :db any? :data #{:sexpress/namespace})
        :ret any?)

(defn namespace-keywords
  ""
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(keyword "sexpress" (name k)) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn list
  [db]
  (let [db-val (map :namespace (db/select db :namespace))
        transformed (namespace-keywords db-val)
        val (s/conform :sexpress/namespace-list transformed)]
    (when (= :clojure.spec/invalid val)
      (s/explain :sexpress/namespace-list transformed))
    val))

(s/fdef list
        :args (s/cat :db any?)
        :ret :sexpress/namespace-list)
