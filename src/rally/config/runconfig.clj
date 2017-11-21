(ns rally.config.runconfig
  (:require [rally.monitoring.sumologic :as logger])
  (:gen-class))

(def framework
  {:tests
    {:test-list '("testfiles/test1" "testfiles/test2")
     :map-width 100
     :map-height 800
     :vehicle-start '(50 780)
     :launch-speed 10
     :launch-angle 90}
   :port 5555
   :send-log #(logger/endpoint "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV02hejcH69FH9sJ1hcLNfRdkmyphHE6krl3Oa0SEO07-rOvlDANYAlsn_Dr6boOHweSrdyEQCJT40R0VisjC8vpIBJhVlWhWeSu4MSGEhTOtg==" %)
})
