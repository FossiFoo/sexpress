(ns server.db
  (:require [clojure.spec :as s]
            [adi.core :as adi]))

(s/def ::db any?)
(s/def :server.db/select-result any?)

(defn insert!
  "fdef wrapper for inserts"
  [db data]
  (adi/insert! db data))

(s/fdef insert!
        :args (s/cat :db ::db :data any?)
        :ret #{true})

(defn select
  "fdef wrapper for select"
  [db selector & opts]
  (adi/select db selector opts))

(s/fdef select
        :args (s/cat :db ::db :selector any?)
        :ret :server.db/select-result)
