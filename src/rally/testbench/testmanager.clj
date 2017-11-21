(ns rally.testbench.testmanager
  (:gen-class))

(defn send-to-test
  "take complete individual map from socket, run across all tests"
  [config]
  (fn [individual]
      (let [testnumber (:test-number individual)
            ;test-to-perform (nth (:tests config) testnumber)
            codestack (:movestack individual)]
            ;send sumolog on failure
            ;send config to testing
            ;return individual to be shipped by socket
            ;(log/send-log-metadata "data=" codestack)
            (assoc individual :movestack (+ 1 codestack)))))
