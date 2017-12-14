(ns darwinsport.core
  (:require [darwinsport.config.runconfig :as config])
  (:require [darwinsport.tcpclient.sockettoinstance :as socket])
  (:require [darwinsport.testbench.statedriver.soccerwindow :as window])
  (:gen-class))

(defn -main
  "Start remote testing node:
    -socket server listens for individuals
    -performs async test on individual and returns
  "
  [instance]
  (if (= instance "-demo")
    (do
        (println "Running graphical mode...")
        (println "Port Server not started")
        (println "External logging disabled")
        (window/start-window 1000 620))
    (do
        (println "Running as compute node...")
        (println "Starting port server on 5555...")
        (println "External logging to sumologic enabled: \n")
        (socket/start-server
            (assoc config/framework :send-log ((:send-log config/framework) instance))))))
