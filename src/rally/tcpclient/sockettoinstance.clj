(ns rally.tcpclient.sockettoinstance
  (:require [rally.tcpclient.netconfig :as config])
  (:require [clojure.core.async :as async])
  (:requre [rally.testbench.testmanager :as manager])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net ServerSocket])

; ___________ Server Input Stack ___________

(def socket-input-stack (atom '()))

(def empty-stack? (fn [] (empty? (deref socket-input-stack))))

(defn input-stack-push
  "take data and push to input stack"
  [data]
  (cond
    (= data "END_INPUT")
      (manager/send-to-test (deref socket-input-stack))
    (= data "START_INPUT")
      (reset! socket-input-stack '())
    :else (swap! socket-input-stack conj data)))

(defn input-stack-pop
  "return and remove indiv from stack"
  []
  (let [stack-top (first (deref socket-input-stack))]
    (swap! socket-input-stack rest) stack-top))

; ___________ Socket Server ___________

;swap to false to kill server
(def running (atom true))


;utilities
(def close-socket (fn [socket] (.close socket)))
(def socket-closed? (fn [socket] (.isClosed socket)))

(defn send-data
    "Send data over open socket"
    [data socket]
    ;create writer
    (let [writer (io/writer socket)]
        (.write writer data)
        (.flush writer)))

(defn persistent-server
    "persistent async TCP server to communicate with network load balancer"
    [socket]
    (future (while @running
        ;read from open conn and push to input stack
        (input-stack-push (.readLine (io/reader socket))))) running)

(defn start-server
    "start a tcp server"
    []
    (let [socket (.accept (ServerSocket. config/elb-port))]
        (persistent-server socket) socket))
