(ns server.editor-test
  (:require [server.editor :as sut]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [hydrox.core :as hydrox]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.test.check :as tc]
            [clojure.test :refer [deftest is] :as t]
            [clojure.spec.test :as stest]
            [server.events :as events]
            [server.namespaces :as ns]))

(background (timbre/-log! anything anything anything anything anything anything anything anything anything anything) => nil)

^{:refer sut/make-event :added "0.1"}
(facts "events"
       (fact "generates valid event from command"
             (sut/make-event :unknown ..uid.. nil)
             => #:sexpress{:type :unknown :uid ..uid.. :data nil}))

^{:refer sut/command :added "0.1"}
(facts "editor commands"
       (fact "fallback command throws exception"
             (sut/command ..db.. :unknown nil ..uid..)
             => (throws RuntimeException))
       (fact "foo command returns debug output"
             (sut/command ..db.. :foo nil ..uid..)
             => [:debug/text "bar"])
       (fact "ns-create command appends to log"
             (sut/command ..db.. :ns-create nil ..uid..)
             => ..return..
         (provided (events/append anything) => ..log..
                   (ns/create anything anything) => ..return..)))

(deftest checks-spec
  (let [checks (stest/summarize-results (stest/check (stest/enumerate-namespace 'server.editor)))]
    (is (= (:total checks) (:check-passed checks)) checks)
    (is (> (:total checks) 0))))
