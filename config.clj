(ns darwin.problems.pathfinding
  ;(:require [darwin.gp.selection :as selection])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.problems.pathfindingtests.machine :as testing])
  (:gen-class))

(def instructions
  '())

(defn test-on-node
  "for each test, send individual to node"
  [test]
  (fn [individual]
    (server/send-for-test (assoc individual :test-number test))))

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
                      :individual-transform (fn [ind] (assoc ind :exit-states (map #(:move %) (:exit-states ind))))}})
