(ns server.editor-test
  (:require [server.editor :as sut]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [hydrox.core :as hydrox]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.test :as t]))

(background (timbre/-log! anything anything anything anything anything anything anything anything anything anything) => nil)

^{:refer sut/sente-handler :added "0.1"}
(facts "editor commands"
       (fact "fallback command throws exception"
             (sut/command ..db.. :unknown nil)
             => (throws RuntimeException))
       (fact "foo command returns debug output"
             (sut/command ..db.. :foo nil)
             => [:debug/text "bar"]))
