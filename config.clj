(ns darwin.problems.pathfinding
  ;(:require [darwin.gp.selection :as selection])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.gp.crossover :as crossover])
  ;(:require [darwin.problems.pathfindingtests.machine :as testing])
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net Socket])
(impoty '[java.io DataOutputStream BufferedOutputStream])

(def inbound-stack (chan))

(defn cluster-socket
  "make connection to computing cluster"
  [ip port]
  (.accept (Socket. ip port)))

(defn inbound-listener
  "listen to socket an push to channel"
  [socket]
  ;get socket input, send to channel
  (go (>! inbound-stack input)))

;push incoming socket connection to channel: (go (>! inbound-stack input))

(defn send-to-cluster
  "send an individual to the cluster"
  [indiv socket]
  (let [ouput (DataOutputStream. (BufferedOutputStream. (.getOutputStream socket)))]
        (.writeUTF output indiv)))

(def instructions
  '())

(defn test-on-node
  "for each test, send individual to node"
  [test socket]
  (fn [individual]
    ;send individual
    (let [])
    (server/send-to-cluster (assoc individual :test-number test) socket)
    ;wait for tranformed individual from cluster on inbound channel
    (<!! (go (<! inbound-stack)))))

(def configuration
  (let [socket (cluster-socket "" 5555)
        inbound (inbound-listener socket)]
  {:genomic true
   :instructions instructions
   :literals (range 90)
   :inputses '(())
   :program-arity 0
   :testcases (list
                (test-on-node 1 socket)
                (test-on-node 2 socket))
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
