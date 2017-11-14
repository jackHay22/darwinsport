(ns rally.core
  (:require [rally.monitoring.sumologic :as log])
  (:gen-class))

(defn -main
  "Start remote test process"
  [& args]
  (println "Log: Starting remote test client")
  (log/send-log "type=node_event message=slave_node_started")
  ;start server, read instructions until END
  ;run instructions on each test case
  ;send fitness to cluster manager
  ;fitness sent to darwin master

  )
