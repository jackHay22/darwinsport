(ns darwinsport.testbench.soccerfield
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

(def game-state (atom
  {:ball-location '(x y)
   :team-one-locations '((x y))
   :team-two-locations '((x y))
   :score-1 0
   :score-2 0
  }))

(import '(java.awt Color Font))

(def text-color (Color. 27 72 105))
(def score-font (Font. "SansSerif" Font/PLAIN 20))

;Define ball location, non-team-specific game attributes
(def field-image
    (sawicon/icon
      (javax.imageio.ImageIO/read
        (clojure.java.io/resource "images/fieldv1.png"))))

(def ball-image
  (sawicon/icon
    (javax.imageio.ImageIO/read
      (clojure.java.io/resource "images/ball.png"))))

(defn draw-score
  [gr]
    (let [state (deref game-state)
          score (str (:score-1 state) " --- " (:score-2 state))]
          (.setColor gr text-color)
          (.setFont gr score-font)
          (.drawString gr score 485 20)))

(defn draw-ball
  "take graphics object, draw game ball"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 50 50 ball-image)
          (sawgr/style)))

(defn draw-field
  "take graphics object, draw field"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 0 0 field-image)
          (sawgr/style)))
