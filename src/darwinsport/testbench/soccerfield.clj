(ns darwinsport.testbench.soccerfield
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

(def game-state (atom
  {:ball-location '(x y)
   :ball-dx 0
   :ball-dy 0
   :score-1 0
   :score-2 0
  }))

(import '(java.awt Color Font))

(def text-color (Color. 27 72 105))
(def score-font (Font. "SansSerif" Font/PLAIN 20))

(defn load-image
  "load an image from resources"
  [loc]
  (sawicon/icon
    (javax.imageio.ImageIO/read
      (clojure.java.io/resource loc))))

;Define ball location, non-team-specific game attributes
(def field-image (load-image "images/field_night.png")) ;fieldv1
(def ball-image (load-image "images/ball.png"))
;TODO: confirm
(def upper-left-corner '(10 10))
(def playable-width 1000)
(def playable-height 600)

(def grass-friction 0.95) ;effects ball speed after each frame (by multiplication)

(defn draw-score
  [gr]
    (let [state (deref game-state)
          score (str (:score-1 state) " --- " (:score-2 state))]
          (.setColor gr text-color)
          (.setFont gr score-font)
          (.drawString gr score 485 20)))

(defn move-possible?
  ;take object x and y and determine if move allowed
  [x y]
  :STUB
  )

(defn update-ball
  "update the ball location at each frame"
  []
  (let [state (deref game-state)
        loc (:ball-location state)
        dx (:ball-dx state)
        dy (:ball-dx state)
        updated (list (+ (first loc) dx) (+ (second loc) dy))
        friction-dx (* dx grass-friction)
        friction-dy (* dy grass-friction)]
        ;TODO: check field bounds
        ;TODO: check if ball is in either goal, update score and reset accordingly
    (do
      (swap! game-state assoc :ball-location updated)
      (swap! game-state assoc :ball-dx friction-dx)
      (swap! game-state assoc :ball-dy friction-dy))))

(defn set-ball-move
  "set the ball movement attributes (dx dy)"
  [force angle]
  (let [dx ;TODO: calculate dx dy
        dy]
      (do
        (swap! game-state assoc :ball-dx dx)
        (swap! game-state assoc :ball-dy dy))))

(defn ball-location
  "get ball location for updating players"
  []
  (:ball-location (deref game-state)))

(defn draw-ball
  "take graphics object, draw game ball"
  [gr]
  (let [location (:ball-location (deref game-state))]
  (sawgr/draw gr
      (sawgr/image-shape (first location) (second location) ball-image)
          (sawgr/style))))

(def get-ball-location (fn [] (:ball-location (deref game-state))))

(defn draw-field
  "take graphics object, draw field"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 0 0 field-image)
          (sawgr/style)))
