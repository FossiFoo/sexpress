(ns server.accounts
  (:require [clojure.spec :as s]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.walk :as walk]
            [server.db :as db]))

(s/def :sexpress/account-list (s/coll-of :sexpress/account))

(defn create
  [db data]
  (db/insert! db {:account data}))

(s/fdef create
        :args (s/cat :db any? :data #{:sexpress/account})
        :ret any?)

(defn namespace-keywords
  ""
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(keyword "sexpress" (name k)) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn list
  [db]
  (let [db-val (map :account (db/select db :account))
        transformed (namespace-keywords db-val)
        val (s/conform :sexpress/account-list transformed)]
    (when (= :clojure.spec/invalid val)
      (s/explain :sexpress/account-list transformed))
    val))

(s/fdef list
        :args (s/cat :db any?)
        :ret :sexpress/account-list)
