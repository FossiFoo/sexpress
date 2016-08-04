(ns client.db
  (:require [clojure.string :as s]
            [taoensso.timbre :refer-macros (log spy)]
            [cljs.reader :as reader]))

(def default-db
  {:ws/connected false
   :editor {:command ""}})

(defn parse-command
  ""
  [cmd-text]
  (when-not (s/blank? cmd-text)
    (let [[cmd-str param-str] (s/split (s/trim cmd-text) #"\s+" 2)
          cmd (keyword cmd-str)
        params (when param-str (reader/read-string param-str))]
      {:command cmd :params params})))


(defn command-direct [db cmd]
  (log :info "setting command" cmd)
  (assoc-in db [:editor :command] cmd))

(defn command-persist [db cmd-text]
  (command-direct db (parse-command cmd-text)))
