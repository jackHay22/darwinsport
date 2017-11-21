(ns rally.testbench.testmanager
  (:require [rally.config.runconfig :as config])
  (:gen-class))

(defn send-to-test
  "take complete individual map from socket, run across all tests"
  [individual]
      (let [testnumber (:test-number individual)
            test-to-perform (nth (:tests config/framework) testnumber)
            codestack (:movestack individual)]
            ;send sumolog on failure

            ;return individual to be shipped by socket
            (assoc individual :movestack (+ 1 codestack))))
