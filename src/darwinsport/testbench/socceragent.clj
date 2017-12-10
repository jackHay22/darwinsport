(ns darwinsport.testbench.socceragent
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

(import 'java.awt.geom.AffineTransform)
(import 'java.awt.image.AffineTransformOp)

(defn load-image
  [location]
    (javax.imageio.ImageIO/read
    (clojure.java.io/resource location)))

(def player-images
  ;image locations for various players
  (map load-image
    '("images/team1player1.png"
      "images/team1player2.png"
      "images/team1player3.png"
      "images/team1player4.png"
      "images/team2player1.png"
      "images/team2player2.png"
      "images/team2player3.png"
      "images/team2player4.png")))

(defn update-player
    "Take in player and make decisions
    {:location (x y)
     :facing-angle
     :assigned-image #
     :team 1 or 2
     :team-locations ((x1 y1) (x2 y2))
     :opponent-locations (() ())
     :ball-location (x y)
     :possessing-ball? bool
     :defined-decisions '(...)}"
    [player]
    (let [current-x (first (:location player))
          current-y (second (:location player))
          current-angle (:facing-angle player)]
    ;interpret decision code and perform action
    ;speed is 0.5
    ;NOTE: if opponent is within a radius of the player, probabalistically tackle
    (assoc
    (assoc player :location (list (+ current-x 0.5) current-y))
    :facing-angle (+ current-angle 1))
    ))

(defn rotate-player-image
  [image ])

(defn draw-player
    "take graphics object, draw player based on current state (if graphics enabled)"
    [gr p]
    (let [assigned-image (nth player-images (:assigned-image p))
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

(defn update-and-decide
  "update each player in list, making a single decision per tick for each"
  [all-players]
    (map update-player all-players))

(defn draw-players
  "draw all players in list"
  [gr players]
  (doseq [p players] (draw-player gr p)))
