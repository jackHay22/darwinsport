(ns darwinsport.testbench.soccer-field
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

(def game-state (atom
  {:ball-location '(x y)
   :team-one-locations '((x y))
   :team-two-locations '((x y))
   :score '(1 2)
  }))

;Define ball location, non-team-specific game attributes
(def field-image
    (sawicon/icon
      (javax.imageio.ImageIO/read
        (clojure.java.io/resource "resources/..."))))

(defn draw-field
  "take graphics object, draw field"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 0 0 field-image)
          (sawgr/style)))
