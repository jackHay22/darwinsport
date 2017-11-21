(ns rally.core
  (:require [rally.config.runconfig :as config])
  (:require [rally.tcpclient.sockettoinstance :as socket])
  (:gen-class))

(defn -main
  "Start remote testing node:
    -socket server listens for individuals
    -performs async test on individual and returns
  "
  [instance]
  (socket/start-server
    (assoc config/framework :send-log ((:send-log config/framework) instance))))
