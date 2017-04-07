(ns server.handler-test
  (:require [server.handler :as sut]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [hydrox.core :as hydrox]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.test :as t]))

(background (timbre/-log! anything anything anything anything anything anything anything anything anything anything) => nil)

[[:chapter {:tag "api" :title "Public API"}]]

"Clojure (and other Lisp) coders know that there is a better way to represent code than in characters, methods, files, checkins. Deep down, almost all language boil down to the AST which the homoiconic goodness of Lisp already (mostly) is. This editor aims to make your code the first-class citizen that it really is on the Von-Neumann-Architecture and give you the power to manipulate it easily in a meaningful way."

"A part of this mission includes that code is not persisted as a character based file or checkin in a local or remote repository,but reachable as a service through the network, possibly being manipulated concurrently by a lot of different clients. To keep surprises to a minimum, all edits to code will be saved as a series of immutable operations. Also both importing and exporting to other formats, including plain text files, should be possible."

"This document describes the API of the editing server and how to interact with it."

[[:section {:tag "rest-api" :title "REST API"}]]

"Currently REST is only used for login calls."

"This might be extended later to include more REST calls for editing operations."

^{:refer sut/ring-handler :added "0.1"}
(facts "The REST API provides a login method."
       (fact ((sut/ring-handler {:db ..db..})
              (-> (mock/request :post "/login")
                  (assoc :body {:user "foo"})))
             => {:body "" :headers {} :status 200 :session {:uid "foo"}}
             (provided (#'sut/init-db ..db..) => ..db..)))

[[:section {:tag "sente-api" :title "Sente API"}]]

"The main API is currently implemented via sente, a clojure library for realtime web communication."


^{:refer sut/sente-handler :added "0.1"}
(facts "The handler supports the following event types:"
       (fact "nil id calls back with noop"
             ((sut/sente-handler nil) {:?reply-fn identity})
             => [:noop nil])
       (fact "passing no reply function does not throw"
             ((sut/sente-handler nil) {:?reply-fn nil}) => nil)
       (fact "A session list can be aquired"
             ((sut/sente-handler nil) {:?reply-fn identity :id :session/list}) => ..result..
             (provided (#'sut/handle-session-list nil) => ..result..)))

(fact "A session list can be aquired"
      ((sut/sente-handler nil) {:?reply-fn identity :id :session/list}) => ..result..
      (provided (#'sut/handle-session-list nil) => ..result..))
