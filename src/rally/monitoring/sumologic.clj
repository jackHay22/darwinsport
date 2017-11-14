(ns rally.monitoring.sumologic
  (:require [clj-http.client :as client])
  (:gen-class))

(def logendpt "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV1uRUC4rk_lJ4fr39YQDCbl4ZqnnFUSMJ0gtl4cj6cOjIXK10KbiZKoO2zjazldBDMX2PKpfZf_7OqLPtL524L2Uy509UgqXG7dkLJNXkVwrw==")

(defn send-log
  "send an http log to sumologic"
  [log]
  (client/get (str logendpt "?" log)
        {:async? true}))
        ;; respond callback
        ;(fn [response] (println "response is:" response))
        ;; raise callback
        ;(fn [exception] (println "exception message is: " (.getMessage exception)))))

(defn write-log
  "write a text-file to s3 bucket"
  [message bucket]
  :STUB)
