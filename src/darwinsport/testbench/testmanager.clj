(ns darwinsport.testbench.testmanager
  (:gen-class)
  (:require [darwinsport.testbench.soccerwindow :as window]))

(defn send-to-test
  "take complete individual map from socket, run across all tests"
  [config]
  (fn [individual]
      (let [codestack (:movestack individual)
            all-tests (:test-list (:tests config))]
            ;(if (:graphical? config) (window/start-window (:window-x config) (:window-y config)))
            ;send sumolog on failure
            ;send config to testing
            ;return individual to be shipped by socket
            ;(log/send-log-metadata "data=" codestack)
            (Thread/sleep 10000)
            ;(println "data received")
            (assoc individual :movestack 10))))
