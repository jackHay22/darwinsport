(ns rally.monitoring.sumologic
  (:require [clj-http.client :as client])
  (:require [rally.config.runconfig :as config])
  (:gen-class))

(defn send-log
  "send an http log to sumologic"
  [log]
  (client/get (str (:log-endpt config/framework) "?" log) {:async? true}))

(defn write-log
  "write a text-file to s3 bucket"
  [message bucket]
  :STUB)
