(ns darwinsport.testbench.statedriver.soccerstate
  (:gen-class)
  (:require [darwinsport.testbench.soccerfield :as field]
            [darwinsport.testbench.statedriver.soccerutils :as utilities]
            [darwinsport.testbench.socceragent :as players]
            [darwinsport.config.runconfig :as config]))

; ------------------------------
; Game manager and update driver

(def window? (atom false))
(def paused? (atom false))
(def players-state (atom
      (list
        (utilities/load-player "testfiles/players/ritchie.txt" 1 0)
        (utilities/load-player "testfiles/players/shelvey.txt" 1 1)
        (utilities/load-player "testfiles/players/reis.txt" 1 2)

        (utilities/load-player "testfiles/players/yedlin.txt" 0 0)
        )))

(defn update-and-draw
  "update the graphical window"
  [gr]
    (let [pstate (deref players-state)
          update? (not (deref paused?))
          updated-players (if update?
                    (players/update-and-decide pstate (field/ball-location)) pstate)
          updated-ball (if update?
                    (field/update-ball))]
      (field/draw-field gr)
      (field/draw-ball gr)
      (field/draw-score gr)
      (reset! players-state updated-players)
      (players/draw-players gr updated-players)
      (field/draw-lighting gr)
  ))

(defn update-no-draw
  "update the players without drawing to window"
  []

  )

(defn keypressed
  "respond to keypress event"
  [key]
  ;update player characteristics based on end of key press
  (if (= key :p)
    (let [p (deref paused?)]
      (reset! paused? (not p)))))
