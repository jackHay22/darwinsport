(ns darwinsport.testbench.soccerfield
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

(def game-state (atom
  {:ball-location '(x y)
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
(def field-image (load-image "images/fieldv1.png"))
(def ball-image (load-image "images/ball.png"))
;TODO: confirm
(def upper-left-corner '(10 10))
(def playable-width 1000)
(def playable-height 600)

(def grass-friction 0.5) ;effects ball speed after each frame (by multiplication)

(defn draw-score
  [gr]
    (let [state (deref game-state)
          score (str (:score-1 state) " --- " (:score-2 state))]
          (.setColor gr text-color)
          (.setFont gr score-font)
          (.drawString gr score 485 20)))

(def move-possible?
  ;take object x and y and determine if move allowed
  [x y]
  )

(defn update-ball
  "update the ball location after some move event"
  [force angle]
  )

(defn draw-ball
  "take graphics object, draw game ball"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 50 50 ball-image)
          (sawgr/style)))

(def get-ball-location (fn [] (:ball-location (deref game-state))))

(defn draw-field
  "take graphics object, draw field"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 0 0 field-image)
          (sawgr/style)))
