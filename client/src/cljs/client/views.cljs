(ns client.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch dispatch-sync]]
              [taoensso.timbre :refer-macros (logf log spy)]
              [re-com.core :as re-com]))


;; home

(defn sente-panel []
  (let [connected? (subscribe [:ws/connected])]
    (fn []
      (let [[connected? user-name] @connected?]
        (if connected?
          [:div "You are connected as " user-name]
          [:div "You are not connected"])))))

(defn user-panel
  ""
  []
  [re-com/h-box
   :children [[:button {:on-click #(dispatch [:account/login "fossi" "foo"])} "login as me" ]
              [sente-panel]]])

(defn- init-stuff
  ""
  []
  (log :info "init")
  (dispatch-sync [:console/immidiate [:session/join "foo"]])
  (js/setTimeout (fn []
                   (dispatch-sync [:console/immidiate [:project/create {:name "prj"}]])
                   (js/setTimeout (fn []
                                    (dispatch-sync [:console/immidiate [:session/attach {:session "foo" :project "prj"}]]))
                                  1000))
                 1000))

(defn console-form
  ""
  []
  (let [val (subscribe [:editor/command])
        exec-cmd-fn (fn [e] (.preventDefault e)
                      (dispatch [:editor/command-exec]))]
    (fn []
      [re-com/h-box
       :children [[:button {:on-click init-stuff} "init" ]
                  [:form {:on-submit exec-cmd-fn}
                   [re-com/input-text :model @val :change-on-blur? false :on-change #(dispatch [:editor/command-set %])]
                   [:button {:on-click exec-cmd-fn} "send" ]]]])))


(defn console-history
  ""
  []
  (let [cmd-history (subscribe [:editor/cmd-history])]
    (fn []
      [:div "history"
       (when cmd-history
         (prn-str @cmd-history))])))

(defn console-panel
  ""
  []
  [re-com/v-box
   :children [[console-history]
              [console-form]]])

(defn session-panel []
  (let [sessions (subscribe [:sessions/list])]
    (fn []
      (when @sessions
        [:div "sessions: " (prn-str @sessions)]))))

(defn project-panel []
  (let [projects (subscribe [:projects/list])]
    (fn []
      (when @projects
        [:div "projects: " (prn-str @projects)]))))


(defn home-title []
  [re-com/title
   :label "finally nice things"
   :level :level1])

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])



(defn header-panel []
  [re-com/h-box
   :gap "1em"
   :children [[home-title]
              [user-panel]]])

(defn main-panel []
  [re-com/v-box
   :gap "1em"
   :children [[session-panel]
              [project-panel]]])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[header-panel]
              [main-panel]
              [console-panel]]])

;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn app-panel []
  (let [active-panel (subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[panels @active-panel]]])))
