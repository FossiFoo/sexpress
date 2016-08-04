(ns client.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [client.core :as sut]))

(deftest init-starts-everything
  (testing "init starts things"
    (with-redefs [sut/mount-root (fn [] nil)
                  sut/dev-setup (fn [] nil)]
      (is (= :running (sut/init))))))
