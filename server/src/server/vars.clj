(ns server.vars
  (:require [clojure.spec :as s]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.walk :as walk]
            [clojure.edn :as edn]
            [adi.core :as adi]
            [server.db :as db]))

(s/def :sexpress/var-list (s/coll-of :sexpress/var))

(defn create
  [db data]
  (let [ns-id (:id (:db (adi/select db {:namespace/namespace-name (:sexpress/namespace-name data)} :first :ids)))]
    (db/insert! db {:var {:ns-symbol (:sexpress/ns-symbol data)
                          :symbol (:sexpress/symbol data)
                          :namespace ns-id
                          :var-data (pr-str (:sexpress/var-data data))}})))

(s/fdef create
        :args (s/cat :db any? :data #{:sexpress/var})
        :ret any?)

(defn deserialize-var
  ""
  [db-val]
  (let [data (edn/read-string (:var-data db-val))]
    #:sexpress{:symbol (:symbol db-val)
               :namespace-name (:namespace-name (:namespace db-val))
               :var-data data}))

(defn list
  [db]
  (let [db-val (map :var (adi/select db :var :pull {:var {:namespace :checked}}))
        deserialized (map deserialize-var db-val)
        val (s/conform :sexpress/var-list deserialized)]
    (when (= :clojure.spec/invalid val)
      (s/explain :sexpress/var-list deserialized))
    val))

(s/fdef list
        :args (s/cat :db any?)
        :ret :sexpress/var-list)
