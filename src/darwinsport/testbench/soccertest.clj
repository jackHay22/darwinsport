(ns darwinsport.testbench.soccertest
  (:gen-class)
  (:require [darwinsport.testbench.soccerfield :as field]
            [darwinsport.testbench.socceragent :as players]
            [darwinsport.config.runconfig :as config]))

(def window? (atom false))
(def players-state (atom '(
        {:location (55 55)
         :facing-angle 45
         :assigned-image 0   ;0-3 team1, 4-7 team2
         :team-locations ()
         :opponent-locations (() ())
         :ball-location (10 10)
         :possessing-ball? false
         :defined-decisions ()})))

(defn update-and-draw
  "update the graphical window"
  [gr]
  (let [updated-players (players/update-and-decide (deref players-state))]
    (field/draw-field gr)
    (field/draw-ball gr)
    (field/draw-score gr)
    (reset! players-state updated-players)
    (players/draw-players gr updated-players)
  ))

(defn update-no-draw
  "update the players without drawing to window"
  []

  )

(defn keypressed
  "respond to keypress event"
  [key]
  ;update player characteristics based on end of key press
  (if (= key :right)
    (println "key press")))
