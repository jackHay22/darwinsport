(ns darwinsport.testbench.testmanager
  (:require [darwinsport.testbench.statedriver.soccerstate :as test])
  (:gen-class))

(defn send-to-test
  "take complete individual map from socket, run across all tests"
  [config]
  (fn [individual]
      (let [codestack (:movestack individual)
            all-tests (:test-list (:tests config))]
            ;test be repeatedly making no-draw updates with state and determine fitness
            (Thread/sleep 1000)
            (assoc individual :movestack 10))))
