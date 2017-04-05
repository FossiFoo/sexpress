(ns client.db
  (:require [clojure.string :as str]
            ;; [cljs.spec :as s]
            [taoensso.timbre :refer-macros (log spy)]
            [cljs.reader :as reader]))

;; (s/def :ws/connected boolean)
;; (s/def ::editor (s/keys :opt-un [:command :cmd-history]))
;; (s/def ::main (s/keys :req [:ws/connected ::editor]))

(def default-db
  {:ws/connected false
   ::editor {:command ""}})

(defn parse-command
  ""
  [cmd-text]
  (when-not (str/blank? cmd-text)
    (let [[cmd-str param-str] (str/split (str/trim cmd-text) #"\s+" 2)
          cmd (keyword cmd-str)
        params (when param-str (reader/read-string param-str))]
      {:command cmd :params params})))


(defn command-direct [db cmd]
  (log :info "setting command" cmd)
  (assoc-in db [::editor :command] cmd))

(defn command-persist [db cmd-text]
  (command-direct db (parse-command cmd-text)))

(defn get-editor-command
  ""
  [db]
  (:command (::editor db)))

(defn get-editor-history
  ""
  [db]
  (:cmd-history (::editor db)))

(defn add-editor-history-entry
  ""
  [db entry]
  (update-in db [::editor :cmd-history] #(conj % entry)))
