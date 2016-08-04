(ns client.handlers-test
  (:require [cljs.test :refer-macros [deftest testing is use-fixtures]]
            [client.handlers :as sut]
            [client.rest :as rest]))

(defn unset-console-error
  []
  (println ";; rebinding console.error to .log because of phantomjs/doo bug")
  (set! (.-error js/console) (fn [x] (.log js/console x))))

(use-fixtures :once {:before unset-console-error})

(deftest handle-account-login
  (testing "logging in calls rest"
    (let [_user "foo"
          _pass "bar"]
      (with-redefs [rest/login (fn [user pass]
                                 (is (= _user user))
                                 (is (= _pass pass)))]
        (is (= {:account {:login _user}} (sut/handle-account-login {} [_user _pass])))))))
