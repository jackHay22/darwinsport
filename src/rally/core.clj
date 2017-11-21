(ns rally.core
  (:require [rally.monitoring.sumologic :as log])
  (:require [rally.tcpclient.sockettoinstance :as socket])
  (:gen-class))

(defn -main
  "Start remote testing node:
    -socket server listens for individuals
    -performs async test on individual and returns
  "
  [& args]
  (socket/start-server))
