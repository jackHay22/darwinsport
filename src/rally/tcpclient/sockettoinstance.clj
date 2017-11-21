(ns rally.tcpclient.sockettoinstance
  (:require [rally.tcpclient.netconfig :as config])
  (:require [clojure.core.async :as async])
  (:require [rally.testbench.testmanager :as manager])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net ServerSocket])

; ___________ Socket Server ___________

;swap to false to kill server
(def running (atom true))

;utilities
(def close-socket (fn [socket] (.close socket)))
(def socket-closed? (fn [socket] (.isClosed socket)))

(defn send-data
    "Send data over open socket"
    [data socket]
    (println "sending" socket)
    (let [writer (io/writer socket)]
        (.write writer data)
        (.flush writer)))

;TODO: send data
(defn persistent-server
    "persistent async TCP server to communicate with network load balancer"
    [socket]
      (future (while @running
          (send-data
            (manager/send-to-test (.readLine (io/reader socket))) socket)))
      running)

(defn start-server
    "start a tcp server"
    []
    (let [socket (.accept (ServerSocket. config/elb-port))]
        (persistent-server socket) socket))
