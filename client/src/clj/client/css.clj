(ns client.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:color "grey"
          :background-color "black"}]
  [:input {:background-color "black !important"
           :color "grey !important"}])
