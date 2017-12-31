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

(defn no-intersection
  "take player and list of players and check for intersects"
  [p all]
  (let [width (.getWidth (:assigned-image p))
        height (.getHeight (:assigned-image p))
        upper-left-correct (list (- (first (:location p)) (/ width 2))
                                 (- (second (:location p)) (/ height 2)))
        intersectfn (utilities/bounded-box-intersection? upper-left-correct width height)]
    (empty?
      (filter (fn [other] (intersectfn (:location other) width height)) all))))

(defn update-player
    "Take in player and make decisions with interpreter"
    [player all-players]
    ;NOTE/TODO: if opponent is within a radius of the player, probabalistically tackle
    (let [current-id (:id player)
          all-others (filter (fn [p] (not (= current-id (:id p)))) all-players)
          updated-player (interp/player-decide player)]
        (if (no-intersection updated-player all-others)
          updated-player
          player)))

(defn update-and-decide
  "update each player in list, making a single decision per tick for each"
  [all-players ball-location]
  (let [ball-aware (map #(assoc % :ball-location ball-location) all-players)]
        (map #(update-player % all-players) ball-aware)))

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
                (- (first (:location p)) center-x )
                (- (second (:location p)) center-y)
                (.filter op assigned-image nil))
              (sawgr/style))))

(defn draw-players
  "draw all players in list"
  [gr players]
  (doseq [p players] (draw-player gr p)))
