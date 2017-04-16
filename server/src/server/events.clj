(ns server.events
  (:require [taoensso.timbre :refer [log spy]]
            [clojure.spec :as s]))

(s/def :sexpress/type #{:log-start :ns-create})
(s/def :sexpress/uid (s/or :user string? :system #{:system}))
(s/def :sexpress/data any?)

(s/def :sexpress/event (s/keys :req [:sexpress/type :sexpress/uid] :opt-un [:sexpress/data]))

(s/def :sexpress/event-log (s/coll-of :sexpress/event))

(defn make-log-start-event
  []
  {:sexpress/type :log-start
   :sexpress/uid :system})

(defn make-log
  []
  [(make-log-start-event)])

(s/fdef make-log
        :args (s/cat)
        :ret :sexpress/event-log)

(def event-log (atom (make-log)))

(defn append
  "append an event to the opened projects event log"
  [event]
  (conj @event-log event))

(s/fdef append
        :args (s/cat :event :sexpress/event)
        :ret :sexpress/event-log)

(defn raw-log
  []
  @event-log)
