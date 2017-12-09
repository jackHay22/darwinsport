(ns darwinsport.core
  (:require [darwinsport.config.runconfig :as config])
  (:require [darwinsport.tcpclient.sockettoinstance :as socket])
  (:gen-class))

(defn -main
  "Start remote testing node:
    -socket server listens for individuals
    -performs async test on individual and returns
  "
  [instance]
  ;add current instance id to config
  (socket/start-server
    (assoc config/framework :send-log ((:send-log config/framework) instance))))
