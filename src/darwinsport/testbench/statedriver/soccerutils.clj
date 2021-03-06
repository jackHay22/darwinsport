(ns darwinsport.testbench.statedriver.soccerutils
  (:require [clojure.java.io :as io] )
  (:gen-class))

; -----------------
; General utilities

(defn pts-eqn
  "given pts, return equation"
  [p1 p2]
  (let [slope (/ (- (second p2) (second p1)) (- (first p2) (first p1)))]
    (fn [x] (+ (* (- x (first p1)) slope) (second p1)))))

(defn pt-in-bounds
  "take pt and check if in bounding box"
  [x y w h]
  (fn [x2 y2]
    (and
      (> x2 x) (< x2 (+ x w))
      (> y2 y) (< y2 (+ y h)))))

(defn radial-intersection?
  "take two upper left corner pts, two sets of
  w h, check intersection"
  [pt r]
  (let [x1 (first pt)
        y1 (second pt)
        dist-fn (fn [xy]
                  (let [x2 (first xy)
                        y2 (second xy)
                        ydif (- y2 y1)
                        xdif (- x2 x1)]
                    (Math/sqrt (+ (* xdif xdif) (* ydif ydif)))))]
    (fn [p2]
      (> r (dist-fn p2)))))

(defn load-image
  "load image from location"
  [location]
    (javax.imageio.ImageIO/read
    (clojure.java.io/resource location)))

(defn load-decision-code
  "load instructions from file"
  [location-file]
  (with-open [reader (clojure.java.io/reader (io/resource location-file))]
    (map (fn [instr-pair] (map #(clojure.string/split % #" ") instr-pair))
        (map (fn [line] (clojure.string/split line #" : "))
            (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader)))))))


(defn load-player
    "takes a player in file and loads images, code, etc..."
    [playerfile teamnumber id config]
    (with-open [reader (clojure.java.io/reader (io/resource playerfile))]
    (let [loaded-player (read-string (clojure.string/join (line-seq reader)))
          player-expansion
            {:team-number teamnumber
             :id id
             :directives '()
             :assigned-image (load-image (:assigned-image loaded-player))
             :target-goal (nth (:goal-locations (:soccer-attribs config)) teamnumber)
             :defend-goal (nth (:goal-locations (:soccer-attribs config)) (if (= teamnumber 1) 0 1))
             :defined-decisions (load-decision-code (:defined-decisions loaded-player))
             :goals 0
             :tackles 0
             :passes 0
             :total-touches 0
             }]
    (merge loaded-player player-expansion))))
