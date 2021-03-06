(ns integration-test
  (:require  [midje.sweet :refer :all]

             [server.editor :as editor]
             [server.namespaces :as namespaces]
             [server.vars :as vars]
             [server.accounts :as accounts]
             [server.projects :as projects]

             [server.schema :refer [+schema+]]
             [adi.core :as adi]
             [system.components.adi :refer [new-adi-db]]))

(def +db+ (adi/connect! "datomic:mem://test" +schema+ true true))

(facts :integration
  (fact "insert a user"
    (accounts/create +db+ {:user-name "testuser"}) => (contains {:account {:user-name "testuser"}})
    (adi/select +db+ :account) => (just {:account {:user-name "testuser"}})
    (accounts/list +db+) => (contains #:sexpress{:user-name "testuser"}))
  (fact "insert a project"
    (projects/create +db+ {:project-name "project 1"}) => (contains {:project {:project-name "project 1"}}))
  (fact "add a namespace"
    (editor/command +db+ :ns-create #:sexpress{:namespace-name "foo" :namespace-data {:foo 1}} nil))
  (fact "load the namespace"
    (namespaces/list +db+) => (contains #:sexpress{:namespace-name "foo" :namespace-data {:foo 1}}))
  (fact "add a var"
    (editor/command +db+ :var-create #:sexpress{:symbol "bar" :namespace-name "foo" :var-data {}} nil))
  (fact "load the var"
    (vars/list +db+) => (contains #:sexpress{:symbol "bar" :namespace-name "foo" :var-data {}})))
