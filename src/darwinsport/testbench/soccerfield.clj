(ns darwinsport.testbench.soccerfield
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

; -------------------------------
; Field/ball, physical game state

(def game-state (atom
  {:ball-location '(500 300)
   :ball-dx 0
   :ball-dy 0
   :score-1 0
   :score-2 0}))

(import '(java.awt Color Font))
(def text-color (Color. 191 53 47))
(def score-font (Font. "SansSerif" Font/PLAIN 20))

(defn load-image
  "load an image from resources"
  [loc]
  (sawicon/icon
    (javax.imageio.ImageIO/read
      (clojure.java.io/resource loc))))

;FIELD IMAGE LOCATIONS
(def field-image (load-image "images/fieldv3.png")) ;fieldv1
(def ball-image (load-image "images/ball.png"))
(def lighting (load-image "images/lightEffects.png"))

;FIELD ATTRIBUTES
(def upper-left-corner '(18 22))
(def lower-right-corner '(980 580))
(def goal-y-1 239)
(def goal-y-2 356)

(def grass-friction 0.98) ;effects ball speed after each frame (by multiplication)

(defn draw-test-pt
  "draw a pt for testing purposes"
  [gr x y]
    (.setColor gr text-color)
    (.fillRect gr x y 10 10))

(defn draw-score
  "display the game score at the top of the screen"
  [gr]
    (let [state (deref game-state)
          score (str (:score-1 state) " --- " (:score-2 state))]
          (.setColor gr text-color)
          (.setFont gr score-font)
          (.drawString gr score 465 22)))

(defn move-possible?
  "determine if a move to a coordinate in the game space is allowed"
  [x y]
  (let [upper-left-x (first upper-left-corner)
        upper-left-y (second upper-left-corner)
        lower-right-x (first lower-right-corner)
        lower-right-y (second lower-right-corner)]
  (and
    (and (> x upper-left-x) (> y upper-left-y))
    (and (< x lower-right-x) (< y lower-right-y)))))

(defn in-goal?
  "check if the ball is in one goal or the other"
  [x y]
  (let [team-1 (and (< x (first upper-left-corner)) (> y goal-y-1) (< y goal-y-2))
        team-2 (and (> x (first lower-right-corner)) (> y goal-y-1) (< y goal-y-2))
        state (deref game-state)
        score-1 (:score-1 state)
        score-2 (:score-2 state)]
    (do
        (if team-1 (swap! game-state assoc :score-2 (+ 1 score-1)))
        (if team-2 (swap! game-state assoc :score-1 (+ 1 score-2)))
      (or team-1 team-2))))

(defn update-ball
  "update the ball location at each frame"
  []
  (let [state (deref game-state)
        loc (:ball-location state)
        dx (* (:ball-dx state) grass-friction)
        dy (* (:ball-dy state) grass-friction)
        x-transform (+ (first loc) dx)
        y-transform (+ (second loc) dy)
        in-goal? (in-goal? x-transform y-transform)
        move-possible? (move-possible? x-transform y-transform)
        updated (cond
                  in-goal? '(498 300)
                  move-possible? (list x-transform y-transform)
                  :else loc)
        delta-check (cond
                  move-possible? 1
                  in-goal? 0
                  :else -0.2)]
        ;delta-check transforms dx and dy based on location
    (do
      (swap! game-state assoc :ball-location updated)
      (swap! game-state assoc :ball-dx (* dx delta-check))
      (swap! game-state assoc :ball-dy (* dy delta-check)))))

(defn set-ball-move
  "set the ball movement attributes (dx dy)"
  [force angle]
  (let [angle-r (Math/toRadians angle)
        dx (* force (Math/cos angle-r))
        dy (* force (Math/sin angle-r))]
      (do
        (swap! game-state assoc :ball-dx dx)
        (swap! game-state assoc :ball-dy dy))))

(def ball-location (fn [] (:ball-location (deref game-state))))

(defn draw-ball
  "take graphics object, draw game ball"
  [gr]
  (let [location (:ball-location (deref game-state))]
  (sawgr/draw gr
      (sawgr/image-shape (first location) (second location) ball-image)
          (sawgr/style))))

(defn draw-layer
  "take graphics object, draw field"
  [gr image]
  (sawgr/draw gr
      (sawgr/image-shape 0 0 image)
          (sawgr/style)))

;Load wrappers for images
(def draw-field
  (fn [gr] (draw-layer gr field-image)))

(def draw-lighting
  (fn [gr] (draw-layer gr lighting)))
