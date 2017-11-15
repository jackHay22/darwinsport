(ns rally.monitoring.sumologic
  (:require [clj-http.client :as client])
  (:gen-class))

(def logendpt "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV02hejcH69FH9sJ1hcLNfRdkmyphHE6krl3Oa0SEO07-rOvlDANYAlsn_Dr6boOHweSrdyEQCJT40R0VisjC8vpIBJhVlWhWeSu4MSGEhTOtg==")

(defn send-log
  "send an http log to sumologic"
  [log]
  (client/get (str logendpt "?" log)
        {:async? true}))
        
(defn write-log
  "write a text-file to s3 bucket"
  [message bucket]
  :STUB)
