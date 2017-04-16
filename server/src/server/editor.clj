(ns server.editor
  (:require [adi.core :as adi]
            [taoensso.timbre :refer [log spy]]
            [clojure.spec :as s]
            [server.events :as events]
            [server.namespaces :as ns]))

(defn make-event
  [cmd uid data]
  #:sexpress{:type cmd
             :uid uid
             :data data})

(s/fdef make-event
        :args (s/cat :cmd :sexpress/type :uid :sexpress/uid :sexpress/data (s/nilable any?))
        :ret :sexpress/event)

(defmulti handle-editor-command (fn [cmd _ _ _] cmd))

(defmethod handle-editor-command :foo
  [cmd data uid db]
  (log :info "got foo")
  [:debug/text "bar"])

(defmethod handle-editor-command :ns-create
  [cmd data uid db]
  (log :info "got ns create" data)
  (let [ns-data (s/conform :sexpress/namespace-data data)]
    (events/append (make-event :ns-create uid (:sexpress/namespace-name ns-data)))
    (ns/create db ns-data)))

(defmethod handle-editor-command :default
  [cmd data uid db]
  (log :info "got default")
  (throw (RuntimeException. (str "Editor command not implemented: " cmd))))

(defn command
  [db command data uid]
  (log :info "got editor command" command uid)
  (handle-editor-command command data uid db))
