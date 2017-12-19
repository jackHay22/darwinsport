(ns darwinsport.core
  (:require [darwinsport.config.runconfig :as config])
  (:require [darwinsport.tcpclient.sockettoinstance :as socket])
  (:require [darwinsport.testbench.statedriver.soccerwindow :as window])
  (:gen-class))

(defn -main
  "Start remote testing node or graphical system
    -socket server listens for individuals
      -performs async test on individual and returns
    -graphical mode runs in realtime with predefined players"
  [instance]
  (if (= instance "-demo")
    (do
        (println "------ Demo Mode ------")
        (println ">> Running graphical mode")
        (println ">> Port Server not started")
        (println ">> External logging disabled")
        (window/start-window
          (:window-x config/framework)
          (:window-y config/framework)))
    (do
        (println "------ Compute Mode ------")
        (println ">> Running as compute node #" instance)
        (println ">> Starting port server on" (:port config/framework))
        (println ">> External logging to sumologic enabled: ")
        (print ">> Log ")
        (socket/start-server
            (assoc config/framework :send-log
              ((:send-log config/framework) instance))))))
