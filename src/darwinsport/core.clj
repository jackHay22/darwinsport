(ns darwinsport.core
  (:require [darwinsport.config.runconfig :as config])
  (:require [darwinsport.tcpclient.sockettoinstance :as socket])
  (:require [darwinsport.testbench.soccerwindow :as window])
  (:gen-class))

(defn -main
  "Start remote testing node:
    -socket server listens for individuals
    -performs async test on individual and returns
  "
  [instance mode]
  (if (= mode "demo") (println "demo"))
  (window/start-window 1042 640))
  ;add current instance id to config

  ;(socket/start-server
  ;  (assoc config/framework :send-log ((:send-log config/framework) instance))))
