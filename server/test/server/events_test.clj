(ns server.events-test
  (:require [server.events :as sut]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [hydrox.core :as hydrox]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.test :refer [deftest is] :as t]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(background (timbre/-log! anything anything anything anything anything anything anything anything anything anything) => nil)

^{:refer sut/append :added "0.1"}
(facts "append events"
       (fact "appends the given event to the back of the event log"
             (last (sut/append ..event..))
             => ..event..))

^{:refer sut/raw-log :added "0.1"}
(facts "gets the raw log"
       (fact "call returns log"
             (sut/raw-log)
             => [#:sexpress{:type :log-start, :uid :system}]))

(deftest checks-spec
  (let [checks (stest/summarize-results (stest/check (stest/enumerate-namespace 'server.events)))]
    (is (= (:total checks) (:check-passed checks)) checks)
    (is (> (:total checks) 0))))
