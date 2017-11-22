(ns darwin.problems.pathfinding
  ;(:require [darwin.gp.selection :as selection])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.problems.pathfindingtests.machine :as testing])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net Socket])

;push incoming socket connection to channel: (go (>! inbound-stack input))

(defn send-to-cluster
  "send an individual to the cluster"
  [indiv]
  (let [socket (Socket. target-ip target-port)
        writer (io/writer socket)
        reader (DataInputStream. (BufferedInputStream. (.getInputStream socket)))]
          (.write writer (str (pr-str indiv) "\n"))
          (.flush writer)
          ;readline blocks until server response
          (let [serverresponse (read-string (.readLine reader))]
            (.close socket) serverresponse)))

(defn test-on-node
    "for each test, send individual to node"
    [test]
    (fn [individual]
      (send-to-cluster (assoc individual :test-number test))))

(def configuration
  {:genomic true
   :instructions instructions
   :literals (range 90)
   :inputses '(())
   :program-arity 0
   :testcases (list
                (test-on-node 1)
                (test-on-node 2))
   :behavioral-diversity #(testing/calculate-behavior-div % 5) ; TODO: play with the frame
   :max-generations 500
   :population-size 200
   :initial-percent-literals 0.4
   :max-initial-program-size 100
   :min-initial-program-size 50
   :evolution-config {:selection novelty-selection
                      :crossover #(crossover/alternation-crossover %1 %2 0.2 6)
                      :percentages '([40 :crossover]
                                     [25 :deletion]
                                     [5 :addition]
                                     [30 :mutation])
                      :deletion-percent 7
                      :addition-percent 7
                      :mutation-percent 7
                      :keep-test-attribute :novelty
                      :individual-transform (fn [ind] (assoc ind :exit-states (map #(:move %) (:exit-states ind))))}}))
