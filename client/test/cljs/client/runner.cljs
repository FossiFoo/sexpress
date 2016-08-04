(ns client.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [client.core-test]
              [client.handlers-test]))

(doo-tests
 'client.core-test
 'client.handlers-test)
