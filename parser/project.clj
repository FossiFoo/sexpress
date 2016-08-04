(defproject sexpress "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.taoensso/timbre "4.5.1"]
                 [midje "1.9.0-alpha3"]]
  :main ^:skip-aot sexpress.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
