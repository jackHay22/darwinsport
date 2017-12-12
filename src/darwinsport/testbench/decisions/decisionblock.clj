(ns darwinsport.testbench.decisions.decisionblocks
  (:require [darwinsport.testbench.decisions.genericinstrs :as coreinstr]
            [darwinsport.testbench.soccerfield :as fieldstate])
  (:gen-class))

;MAIN decisions for all players
;Can modify field state

(defn ritchie1
  "takes in player, performs decision tree,
  RETURNS player and associated action"
  [player]
  (cond (and
          (coreinstr/self-ball-posessed? player)
          (coreinstr/self-space? player 10))
            (coreinstr/self-dribble <angle>)
        (and
          (coreinstr/self-ball-posessed? player)
          (not (coreinstr/self-space? player 10))
          (coreinstr/team-mate-open? player))
            (coreinstr/action-pass-forward-to-open player)

  )
  )
