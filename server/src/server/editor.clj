(ns server.editor
  (:require [adi.core :as adi]
            [taoensso.timbre :refer [log spy]]
            [clojure.spec :as s]
            [server.events :as events]
            [server.namespaces :as ns]
            [server.vars :as vars]))

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
  (s/assert :sexpress/namespace-data data)
  (events/append (make-event :ns-create uid (:sexpress/namespace-name data)))
  (ns/create db data))

(defn transform-var
  ""
  [data]
  (let [ns (:sexpress/namespace-name data)
        symbol (:sexpress/symbol data)]
    #:sexpress{:symbol symbol
               :namespace-name ns
               :var-data (:sexpress/var-data data)
               :ns-symbol (str ns symbol)}))


(defmethod handle-editor-command :var-create
  [cmd data uid db]
  (log :info "got var create" data)
  (let [var-data (transform-var data)]
    (s/assert :sexpress/var var-data)
    (vars/create db var-data)))

(defmethod handle-editor-command :default
  [cmd data uid db]
  (log :info "got default")
  (throw (RuntimeException. (str "Editor command not implemented: " cmd))))

(defn command
  [db command data uid]
  (log :info "got editor command" command uid)
  (handle-editor-command command data uid db))
