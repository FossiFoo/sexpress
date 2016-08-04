(defproject server "0.1.0-SNAPSHOT"
  :dependencies []

  :plugins [[lein-hydrox "0.1.15"]]

  :source-paths ["src"]
  :profiles
  {:dev
   {:dependencies [[helpshift/hydrox "0.1.15"]]
    :plugins      [[lein-figwheel "0.5.4-3"]]}}

  :documentation {:site   "sexpress"
                  :output "docs"
                  :template {:path "template"
                             :copy ["assets"]
                             :defaults {:template     "article.html"
                                        :navbar       [:file "partials/navbar.html"]
                                        :dependencies [:file "partials/deps-web.html"]
                                        :navigation   :navigation
                                        :article      :article}}
                  :paths ["test"]
                  :files {"api"
                          {:input "test/server/handler_test.clj"
                           :title "API"
                           :subtitle "remote controlling your AST"}}
                  :link {:auto-tag    true
                         :auto-number  true}}
)
