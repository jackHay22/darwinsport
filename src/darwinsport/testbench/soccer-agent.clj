(ns darwinsport.testbench.soccer-agent
  (:gen-class))

(defn update-player
    "Take in player and make decisions
    {:location (x y)
     :team-locations ((x1 y1) (x2 y2))
     :opponent-locations (() ())
     :ball-location (x y)
     :defined-decisions '(...)}"
    [player]
    (let [offensive-third? ...])
    ;interpret decision code and perform action
    ;NOTE: if opponent is within a radius of the player, probabalistically tackle
)

(defn draw-player
    "take graphics object, draw player based on current state (if graphics enabled)"
    [gr]
    (sawgr/draw gr (sawgr/rect
      ;position to draw player
      x y player-width player-height)
      (sawgr/style :background :yellow)))
