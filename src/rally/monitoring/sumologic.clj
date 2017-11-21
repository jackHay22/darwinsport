(ns rally.monitoring.sumologic
  (:require [clj-http.client :as client])
  (:gen-class))

(defn endpoint
  "send a log to endpt"
  [endpt instance]
  (fn [message] (client/get (str endpt "?instance=" instance ", message=" message) {:async? true}
            (fn [response] (println "response is:" response))
            (fn [exception] (println "exception message is: " (.getMessage exception))))))

(defn write-log
  "write a text-file to s3 bucket"
  [message bucket]
  :STUB)
