(ns rally.tcpclient.sockettoinstance
  (:require [rally.config.runconfig :as config])
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

(defn persistent-server
    "persistent async TCP server to communicate with network load balancer"
    [socket]
    (future (while @running
        ;get data, process in testbench, send back
        (let [new-data (.readLine (io/reader socket))
              processed (pr-str (manager/send-to-test (read-string new-data)))
              writer (io/writer socket)]
                (.write writer processed)
                (.flush writer)))) running)

(defn start-server
    "start and accept a connection to a tcp socket server"
    [] (let [socket (.accept (ServerSocket. config/port))]
        ;use socket to create async persistent server
        (persistent-server socket) socket))
