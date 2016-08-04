(def documentation {:site   "sexpress"
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
                    :link {:auto-tag  true
                           :auto-number true}})
(set-env!
 :source-paths   #{"src" "test"}
 :resource-paths #{"resources"}
 :documentation documentation
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [environ "1.0.3"]
                 [boot-environ "1.0.3"]
                 [org.danielsz/system "0.3.0-SNAPSHOT"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-transit "0.1.6"]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [com.taoensso/sente "1.9.0"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [midje "1.8.3"]
                 [com.taoensso/timbre "4.5.1"]
                 [com.datomic/datomic-free "0.9.5350"]
                 [im.chit/adi "0.3.2"]
                 [hiccup "1.0.5"]
                 [clj-http "2.0.1"]
                 [org.clojure/data.json "0.2.6"]
                 [zilti/boot-midje "0.1.2" :scope "test"]
                 [ring/ring-mock "0.3.0" :scope "test"]
                 [boot-hydrox "0.1.17-SNAPSHOT" :scope "test"]
                 [pandeiro/boot-http "0.7.3"]
                 [deraen/boot-livereload "0.1.3-SNAPSHOT"]
                 [midje "1.9.0-alpha3"]])

(require
 '[boot.pod :as pod]
 '[clojure.pprint :refer [pprint]]
 '[taoensso.timbre :refer [log spy]]
 '[server.systems :refer [dev-system prod-system]]
 '[environ.boot :refer [environ]]
 '[system.boot :refer [system run]]
 '[zilti.boot-midje :refer :all]
 '[boot-hydrox :refer [hydrox]]
 '[cloverage.coverage :as cloverage]
 '[pandeiro.boot-http :refer [serve]]
 '[deraen.boot-livereload :refer [livereload]])

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port "3025"})
   (watch :verbose true)
   (system :sys #'dev-system :auto true :files ["handler.clj"])
   (repl :server true)))


(deftask dev-run
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port "3025"})
   (run :main-namespace "server.core" :arguments [#'dev-system])
   (wait)))

(deftask prod-run
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port "8008"})
   (run :main-namespace "server.core" :arguments [#'prod-system])
   (wait)))

(deftask docs
  []
  (comp
   (serve :dir "docs")
   (livereload :path "docs")
   (wait)))

(def ^:private deps
  '[[cloverage "1.0.6"]])

(deftask cloverage
  "cloverage analysis"
  []
  (let [pod (-> (get-env)
                (update-in [:dependencies] into deps)
                pod/make-pod
                future)]
    (with-post-wrap fileset
      (let [namespaces (fileset-namespaces fileset)
            ns-names (map name namespaces)
            args (into [] ns-names)]
        (log :info "running cloverage with " args)
        (pod/with-call-in @pod (cloverage.coverage/-main ~args))))))


(deftask hydrox-try
  "hydrox documentation generation"
  []
  (comp
   (watch)
   (hydrox)
   (wait)))
