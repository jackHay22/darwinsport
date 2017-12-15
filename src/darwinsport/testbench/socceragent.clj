(ns darwinsport.testbench.socceragent
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]
            [darwinsport.testbench.soccerfield :as field]
            [darwinsport.testbench.statedriver.soccerutils :as utilities]
            [darwinsport.testbench.soccerdecisions :as interp]))

(import 'java.awt.geom.AffineTransform)
(import 'java.awt.image.AffineTransformOp)

; -----------------------------
; Players, supporting functions

;TODO: not working
(defn no-intersection
  "take player and list of players and check for intersects"
  [p all]
  (let [width (.getWidth (:assigned-image p))
        height (.getHeight (:assigned-image p))
        intersectfn (utilities/bounded-box-intersection? (:location p) width height)]
    (empty?
      (filter (fn [other] (intersectfn (:location other) width height)) all))))

(defn update-player
    "Take in player and make decisions with interpreter"
    [player all-players]
    ;NOTE: if opponent is within a radius of the player, probabalistically tackle
    (let [current-id (:id player)
          all-others (filter (fn [p] (not (= current-id (:id p)))) all-players)
          updated-player (interp/player-decide player)]
        (if (no-intersection updated-player all-others)
          updated-player
          player)))

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
        (map #(update-player % all-players) ball-aware)))

(defn draw-players
  "draw all players in list"
  [gr players]
  (doseq [p players] (draw-player gr p)))
