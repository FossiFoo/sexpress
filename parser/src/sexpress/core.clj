(ns sexpress.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre :refer [spy log]]
            [clojure.walk :as walk]))

(defn parse-readable
  [reader]
  (with-open [pb-reader (java.io.PushbackReader. reader)]
    (binding [*read-eval* false]
      (let [exp-reader (repeatedly #(read pb-reader false nil))]
        (doall (take-while identity exp-reader))))))


;; (defmulti parse-hunk
;;   "Parse a TOP-LEVEL-SEXP."
;;   (fn [_] :default))

(def parse-hunk)

(defn parse-fn
  "parses a clojure fn"
  [ns-model expanded-hunk]
  (println "parse-fn: " expanded-hunk)
  {:type :fn :params [] :expand expanded-hunk})

(defn parse-fn*
  "parses a clojure fn"
  [ns-model expanded-hunk]
  (println "parse-fn*: " expanded-hunk)
  {:type :fn :params (first expanded-hunk) :body (rest expanded-hunk) :expand expanded-hunk})

(defn- parse-def-internal
  [def-name def-body ns-model expanded-hunk top-level-sexp]
  [{:type :def :name def-name :source top-level-sexp :expand expanded-hunk}
     (parse-hunk ns-model def-body)])


(defn parse-def [ns-model expanded-hunk top-level-sexp]
  (let [def-name (second expanded-hunk)
        def-body (spy (nth expanded-hunk 2 nil))]
    (parse-def-internal def-name def-body ns-model expanded-hunk top-level-sexp)))

(defn parse-hunk [ns-model top-level-sexp]
  (log :debug "parsing " top-level-sexp)
  (let [expanded-hunk (macroexpand top-level-sexp)
        token (spy (keyword (first expanded-hunk)))]
    (case token
      :def (parse-def ns-model expanded-hunk top-level-sexp)
      :clojure.core/fn (parse-fn ns-model expanded-hunk)
      :fn* (parse-fn* ns-model expanded-hunk)
      [{:type :unknown :source top-level-sexp :expand expanded-hunk}])))

(defn make-model [namespace-meta namespace-content]
  [namespace-meta namespace-content])

(defn parse-namespace [namespace-hunk]
  (assert (= (symbol "ns") (first namespace-hunk)) "first hunk of file has to be ns")
  ;; FIXME: use clojure.tools.namespace here
  {:type :namespace :name (second namespace-hunk) :source namespace-hunk})

(defn parse-file
  [f]
  (with-open [reader (io/reader f)]
    (parse-readable reader)))

(defn- merge-models [models]
  (apply concat models))

(defn convert-namespace
  [hunks]
  (let [namespace-hunk (first hunks)
        namespace-model (parse-namespace namespace-hunk)
        rest-hunks (rest hunks)
        file-model (make-model
                    namespace-model
                    (merge-models (map (partial parse-hunk namespace-model) rest-hunks)))]
    file-model))


(defn -main
  [& args]
  (let [hunks (parse-file (or (first args) "src/sexpress/core.clj"))
        file-model (convert-namespace hunks)]
    ;; (pprint file-model)
    (println "\n\n\n\n")
    (println "ns:" (:name (first file-model)))
    (println "defs:" (count (first (rest file-model))))))
