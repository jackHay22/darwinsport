(ns darwinsport.config.runconfig
  (:require [darwinsport.monitoring.sumologic :as logger]
            [darwinsport.testbench.statedriver.soccerutils :as utilities])
  (:gen-class))

(defn fitness-calculator
  "take final player analytics and generate fitness"
  [player]
  (let [goals (:goals player)
        tackles (:tackles player)
        passes (:passes player)
        total-touches (:total-touches player)]
    0
    ))

(def framework
  {:tests
    {:test-list '()}
   :first-decision-only? true  ;note: if this is disabled, later moves have the chance of canceling earlier player movements
   :soccer-attribs
    {:run-speed 1
     :walk-speed 0.2
     :sprint-speed 1.1
     :lateral-speed 0.8
     :space? 55
     :dribble-spacing 15
     :shot-spacing 20
     :settle-radius 15
     :dribble-force 1.3
     :shot-range 250
     :max-kick-force 7
     :goal-locations '((0 297) (1000 297))}
   :fitness-calculus fitness-calculator
   :port 5555
   :window-x 1000
   :window-y 620
   :send-log #(logger/endpoint "https://endpoint2.collection.us2.sumologic.com/receiver/v1/http/ZaVnC4dhaV02hejcH69FH9sJ1hcLNfRdkmyphHE6krl3Oa0SEO07-rOvlDANYAlsn_Dr6boOHweSrdyEQCJT40R0VisjC8vpIBJhVlWhWeSu4MSGEhTOtg==" %)
})

(def roster
  ;player file and team number
  '(("testfiles/players/shelvey.txt" 1)
    ("testfiles/players/reis.txt" 1)
    ("testfiles/players/ritchie.txt" 1)
    ("testfiles/players/gayle.txt" 1)
    ("testfiles/players/yedlin.txt" 0)
    ("testfiles/players/lascelles.txt" 0)
    ("testfiles/players/howard.txt" 0)))

(def teams (map #(utilities/load-player (first %1) (second %1) %2 framework) roster (range)))
