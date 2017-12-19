(ns darwinsport.config.runconfig
  (:require [darwinsport.monitoring.sumologic :as logger]
            [darwinsport.testbench.statedriver.soccerutils :as utilities])
  (:gen-class))

(def framework
  {:tests
    {:test-list '()}
   :first-decision-only? false  ;note: if this is disabled, later moves have the chance of canceling earlier player movements
   :soccer-attribs
    {:run-speed 1
     :walk-speed 0.2
     :sprint-speed 1.2
     :lateral-speed 0.8
     :space? 30
     :dribble-spacing 18
     :shot-spacing 20
     :settle-radius 15
     :dribble-force 1.5
     :shot-range 250
     :max-kick-force 10
     :goal-locations '((0 297) (1000 297))}
   :port 5555
   :window-x 1000
   :window-y 620
   :send-log #(logger/endpoint "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV02hejcH69FH9sJ1hcLNfRdkmyphHE6krl3Oa0SEO07-rOvlDANYAlsn_Dr6boOHweSrdyEQCJT40R0VisjC8vpIBJhVlWhWeSu4MSGEhTOtg==" %)
})

(def load-with-framework (fn [file team id]
    (utilities/load-player file team id framework)))

(def teams {:teams
      (list
         (load-with-framework "testfiles/players/shelvey.txt" 1 1)
         (load-with-framework "testfiles/players/reis.txt" 1 2)
         (load-with-framework "testfiles/players/ritchie.txt" 1 0)
         (load-with-framework "testfiles/players/yedlin.txt" 0 3)
         (load-with-framework "testfiles/players/howard.txt" 0 4)
      )})
