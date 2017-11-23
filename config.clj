(ns darwin.problems.pathfinding
  ;(:require [darwin.gp.selection :as selection])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.problems.pathfindingtests.machine :as testing])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net Socket])

(def target-ip "")
(def target-port 5555)

;push incoming socket connection to channel: (go (>! inbound-stack input))

(defn send-to-cluster
  "send an individual to the cluster"
  [indiv test-number]
  (let [mark-indiv (assoc indiv :test-number test-number)
        socket (Socket. target-ip target-port)
        writer (io/writer socket)
        reader (DataInputStream. (BufferedInputStream. (.getInputStream socket)))]
          (.write writer (str (pr-str mark-indiv) "\n"))
          (.flush writer)
          ;readline blocks until server response
          (let [serverresponse (read-string (.readLine reader))]
            (.close socket) serverresponse)))


(def configuration
  {:genomic true
   :instructions instructions
   :literals (range 90)
   :inputses '(())
   :program-arity 0
   :testcases (list
                #(send-to-cluster % 1)
                #(send-to-cluster % 2))
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
