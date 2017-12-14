(ns darwinsport.config.runconfig
  (:require [darwinsport.monitoring.sumologic :as logger])
  (:gen-class))

(def framework
  {:tests
    {:test-list '("testfiles/test1.txt" "testfiles/test2.txt")
     :field-width 1000
     :field-height 620
     :hard-bounds true}
   :soccer-attribs
    {:run-speed 1
     :walk-speed 0.2
     :sprint-speed 1.2
     :space? 30
     :dribble-spacing 18
     :shot-spacing 20
     :dribble-force 1.5
     :shot-range 250
     :max-kick-force 10
     :goal-locations '((18 297) (980 297))}
   :port 5555
   :graphical? false
   :window-x 1000
   :window-y 620
   :send-log #(logger/endpoint "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV02hejcH69FH9sJ1hcLNfRdkmyphHE6krl3Oa0SEO07-rOvlDANYAlsn_Dr6boOHweSrdyEQCJT40R0VisjC8vpIBJhVlWhWeSu4MSGEhTOtg==" %)
})
