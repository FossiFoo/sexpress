(ns server.namespaces
  (:require [clojure.spec :as s]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.walk :as walk]
            [clojure.edn :as edn]
            [server.db :as db]))

(s/def :sexpress/namespace-list (s/coll-of :sexpress/namespace))

(defn create
  [db data]
  (db/insert! db {:namespace {:namespace-name (:sexpress/namespace-name data)
                              :namespace-data (pr-str (:sexpress/namespace-data data))}}))

(s/fdef create
        :args (s/cat :db any? :data #{:sexpress/namespace})
        :ret any?)

(defn deserialize-namespace
  ""
  [db-val]
  (let [data (edn/read-string (:namespace-data db-val))]
    #:sexpress{:namespace-name (:namespace-name db-val)
               :namespace-data data}))

(defn list
  [db]
  (let [db-val (map :namespace (db/select db :namespace))
        deserialized (map deserialize-namespace db-val)
        val (s/conform :sexpress/namespace-list deserialized)]
    (when (= :clojure.spec/invalid val)
      (s/explain :sexpress/namespace-list deserialized))
    val))

(s/fdef list
        :args (s/cat :db any?)
        :ret :sexpress/namespace-list)
