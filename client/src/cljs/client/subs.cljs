(ns client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame :refer [register-sub]]
            [taoensso.timbre :refer-macros (logf log spy)]
            [clojure.string :as s]
            [client.db :as db]))

(register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(register-sub
 :ws/connected
 (fn [db]
   (reaction [(:ws/connected @db) (or (get-in @db [:account :login]) "anonymous")])))

(register-sub
 :editor/command
 (fn [db]
   (let [cmd (reaction (db/get-editor-command @db))]
     (reaction (let [cmd-str (subs (str (:command @cmd)) 1)
                     params (:params @cmd)
                     params-str (if params (pr-str params) "")]
                 (s/join " " (remove s/blank? [cmd-str params-str])))))))

(register-sub
 :editor/cmd-history
 (fn [db]
   (reaction (take 10 (db/get-editor-history @db)))))

(register-sub
 :sessions/list
 (fn [db]
   (reaction (first (:list (:sessions @db))))))

(register-sub
 :projects/list
 (fn [db]
   (reaction (first (:projects @db)))))
