(ns server.html
  (:require
   (hiccup [core :refer [html]]
           [page :refer [html5 include-js include-css]])))

(defn index
  []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:meta {:name "description" :content "Server"}]
    [:title "Sexpress"]

    (include-css
      "//maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"
      "client/vendor/css/bootstrap.min.css"
      "client/vendor/css/bootstrap-theme.min.css"
      "client/vendor/css/material-design-iconic-font.min.css"
      "client/vendor/css/re-com.css"
      "client/css/screen.css")

    "<!--[if lt IE 9]>"
    [:script {:src "https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"}]
    [:script {:src "https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"}]
    "<![endif]-->"
    ]
   [:body
    [:div.container-fluid {:id "app"}]
    [:script {:src "client/js/compiled/app.js" :type "text/javascript"}]
    [:script {:type "text/javascript"} "client.core.init();"]
    (include-js
     "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"
     "client/vendor/bootstrap.min.js")]))
