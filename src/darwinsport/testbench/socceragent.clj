(ns darwinsport.testbench.socceragent
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]
            [darwinsport.testbench.soccerfield :as field]
            [darwinsport.testbench.soccerutils :as interp]))

(import 'java.awt.geom.AffineTransform)
(import 'java.awt.image.AffineTransformOp)

(defn update-player
    "Take in player and make decisions
     :location (x y)
     :facing-angle
     :assigned-image #
     :team 1 or 2
     :team-locations ((x1 y1) (x2 y2))
     :opponent-locations (() ())
     :possessing-ball? bool
     :defined-decisions (...)"
    [player]
    ;(let [current-x (first (:location player))
    ;      current-y (second (:location player))
    ;      current-angle (:facing-angle player)
    ;      ball-location (field/get-ball-location)]
    ;check if move possible
    ;interpret decision code and perform action
    ;NOTE: if opponent is within a radius of the player, probabalistically tackle
    ;  (assoc
    ;      (assoc player :location (list (+ current-x 0.5) current-y))
    ;    :facing-angle (+ current-angle 1))
      player
      )
;)

(defn draw-player
    "take graphics object, draw player based on current state (if graphics enabled)"
    [gr p]
    (let [assigned-image (:assigned-image p)
          radians (Math/toRadians (:facing-angle p))
          center-x (/ (.getWidth assigned-image) 2)
          center-y (/ (.getHeight assigned-image) 2)
          tx (AffineTransform/getRotateInstance radians center-x center-y)
          op (AffineTransformOp. tx AffineTransformOp/TYPE_BILINEAR)]
          (sawgr/draw gr
            (sawgr/image-shape
                (first (:location p))
                (second (:location p))
                (.filter op assigned-image nil))
              (sawgr/style))))

(defn associate-team-to-player
  "take a player, associate that player's team field with a team list"
  [t1 t2 result-team]
  (fn [player]
    (let [id (:id player)
          team (if (= (:team-number player) 1) t1 t2)]
        (assoc player result-team (filter (fn [p] (not (= (:id p) id))) team)))))

(defn update-and-decide
  "update each player in list, making a single decision per tick for each"
  [all-players ball-location]
  (let [team1 (filter (fn [p] (= (:team-number p) 1)) all-players)
        team2 (filter (fn [p] (= (:team-number p) 2)) all-players)
        team-aware (map (associate-team-to-player team1 team2 :team) all-players)
        opponent-aware (map (associate-team-to-player team2 team1 :opponent) team-aware)
        ball-aware (map #(assoc % :ball-location ball-location) opponent-aware)]
        (map update-player ball-aware)))

(defn draw-players
  "draw all players in list"
  [gr players]
  (doseq [p players] (draw-player gr p)))
