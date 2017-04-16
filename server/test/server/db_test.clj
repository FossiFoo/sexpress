(ns server.db-test
  (:require [server.db :as sut]
            [midje.sweet :refer :all]
            [hydrox.core :as hydrox]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.test :refer [deftest is] :as t]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]
            [adi.core :as adi]))

(background (timbre/-log! anything anything anything anything anything anything anything anything anything anything) => nil)

^{:refer sut/insert! :added "0.1"}
(facts "inserts to database"
  (fact "inserts"
    (sut/insert! ..db.. ..data..)
    => ..result..
    (provided (adi/insert! ..db.. ..data..) => ..result..)))

^{:refer sut/select :added "0.1"}
(facts "selects from database"
  (fact "selects"
    (sut/select ..db.. ..data..)
    => ..result..
    (provided (adi/select ..db.. ..data..) => ..result..)))

(deftest checks-spec
  (with-redefs [adi/insert! (fn [a b] true)
                adi/select (fn [a b] true)]
    (let [checks (stest/summarize-results (stest/check (stest/enumerate-namespace 'server.db)
                                                       {:clojure.spec.test.check/opts {:num-tests 1}}))] ; don't test like a maniac
      (is (= (:total checks) (:check-passed checks)) checks)
      (is (> (:total checks) 0)))))
