(ns server.editor
  (:require [adi.core :as adi]
   [taoensso.timbre :refer [log spy]]))

(defn command
  [db command data]
  (log :info "got editor command" command))
