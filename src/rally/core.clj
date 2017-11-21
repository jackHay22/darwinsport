(ns rally.core
  (:require [rally.monitoring.sumologic :as log])
  (:require [rally.tcpclient.sockettoinstance :as socket])
  (:gen-class))

(defn -main
  "Start remote test process"
  [& args]
  (let [socket (socket/start-server)] )
  ;start server, read instructions until END
  ;run instructions on each test case
  ;send fitness to cluster manager
  ;fitness sent to darwin master
  )
