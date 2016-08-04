(ns sexpress.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [sexpress.core :refer :all]
            [clojure.walk :as walk]))

(def _ns-hunk (list (symbol "ns") "foobar"))
(def _ns-model {:type :namespace :name "foobar" :source _ns-hunk})

(def _fn1-ast (list (symbol "clojure.core" "fn") [(symbol "x")] (list (symbol "+") 2 (symbol "x"))))
(def _fn1-code (clojure.core/fn [x] (+ 2 x)))

(def _fn1-expand (walk/macroexpand-all _fn1-ast))


(def _func1-ast (list (symbol "def") "func1" _fn1-ast))
(def _func1-expand (walk/macroexpand-all _func1-ast))
(def _def1-model {:type :def :name "func1" :source _func1-ast :expand _func1-expand})
(def _func1-model [_def1-model [{:type :unknown, :source nil, :expand nil}]])

(def _content-hunk (list _func1-ast))
(def _content-model _func1-model)

(def _file-hunk (cons _ns-hunk _content-hunk))
(def _file-model [_ns-model _content-model] )

(facts "about parsing a readable"
       (fact "parsing a string reader returns an s-expression"
             (parse-readable (java.io.StringReader. "{:foo [:bar]}"))
             => '({:foo [:bar]})))

(facts "about parsing namespaces"
       (fact "parsing ns will produce a model"
             (parse-namespace _ns-hunk)
             => {:type :namespace :name "foobar" :source _ns-hunk }))

(facts "about parsing sexpressions"
       (fact "parsing a 3 part def will produce a model"
             (first (parse-def _ns-model _func1-expand _func1-ast))
             => {:type :def :name "func1" :source _func1-ast :expand _func1-expand})
       (fact "parsing a def will call parse-hunk"
             (parse-def _ns-model _func1-ast nil)
             => _func1-model
             (provided
              (parse-hunk _ns-model _func1-expand) => nil)))

(facts "about parsing clojure files"
       (fact "making models is great"
             (make-model _ns-model _content-model))
       (fact "converting a namespace will produce a file model"
             (convert-namespace _file-hunk)
             => _file-model))

(facts "about parsing a fn"
       (fact "parsing a fn will return a fn model"
             (parse-fn _ns-model _fn1-ast)
             => {:type :fn :params [] :expand _fn1-expand}))

(facts "about parsing a hunk"
       (fact "an unknown hunk returns an unknown model"
             (parse-hunk _ns-model [:foo :bar])
             => [{:type :unknown :source [:foo :bar] :expand [:foo :bar]}])
       (fact "a def hunk is returning a def model"
             (parse-hunk _ns-model _func1-ast)
             => ..model..
             (provided (parse-def _ns-model _func1-expand _func1-ast) => ..model..)))
