(ns darwinsport.config.runconfig
  (:require [darwinsport.monitoring.sumologic :as logger])
  (:gen-class))

(def framework
  {:tests
    {:test-list '("testfiles/test1.txt" "testfiles/test2.txt")
     :field-width 200
     :field-height 400
     :hard-bounds true}
   :port 5555
   :send-log #(logger/endpoint "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV02hejcH69FH9sJ1hcLNfRdkmyphHE6krl3Oa0SEO07-rOvlDANYAlsn_Dr6boOHweSrdyEQCJT40R0VisjC8vpIBJhVlWhWeSu4MSGEhTOtg==" %)
})
