(ns darwinsport.testbench.statedriver.soccerutils
  (:require [darwinsport.config.runconfig :as config])
  (:gen-class))

; -----------------
; General utilities

(defn load-image
  "load image from location"
  [location]
    (javax.imageio.ImageIO/read
    (clojure.java.io/resource location)))

(defn load-decision-code
  "load instructions from file"
  [location-file]
  (map (fn [instr-pair] (map #(clojure.string/split % #" ") instr-pair))
      (map (fn [line] (clojure.string/split line #" : "))
            (clojure.string/split-lines (slurp location-file)))))

(defn load-player
    "takes a player in file and loads images, code, etc..."
    [playerfile teamnumber id]
    (let [loaded-player (read-string (slurp playerfile))
          player-expansion
            {:team-number teamnumber
             :id id
             :directives '()
             :assigned-image (load-image (:assigned-image loaded-player))
             :target-goal (nth (:goal-locations (:soccer-attribs config/framework)) teamnumber)
             :defend-goal (nth (:goal-locations (:soccer-attribs config/framework)) (if (= teamnumber 1) 0 1))
             :defined-decisions (load-decision-code (:defined-decisions loaded-player))
             }]
    (merge loaded-player player-expansion)))
