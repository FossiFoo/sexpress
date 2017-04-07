(ns server.editor
  (:require [adi.core :as adi]
   [taoensso.timbre :refer [log spy]]))

(defmulti handle-editor-command (fn [cmd _] (spy :error cmd)))

(defmethod handle-editor-command :foo
  [cmd data]
  (log :info "got foo")
  [:debug/text "bar"])

(defmethod handle-editor-command :default
  [cmd data]
  (log :info "got default")
  (throw (RuntimeException. "Editor command not implemented.")))

(defn command
  [db command data]
  (log :info "got editor command" command)
  (handle-editor-command command data))
