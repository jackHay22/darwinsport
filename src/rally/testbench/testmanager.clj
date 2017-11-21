(ns rally.testbench.testmanager
(:gen-class))

(def testsuite '())

(defn send-to-test
  "take complete individual map from socket, run across all tests"
  [data]
      (let [individual (read-string data)
            testnumber (:test-number individual)
            codestack (:movestack individual)]
            ;send sumolog on failure
            ;return individual to be shipped by socket
            (assoc individual :movestack (+ 1 codestack))))
