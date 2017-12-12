(ns darwinsport.testbench.soccerutils
  (:require [darwinsport.testbench.soccerfield :as fieldstate])
  (:gen-class))

;LOAD UTILITIES

(defn load-image
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
    "takes a player in file and loads"
    [playerfile teamnumber id]
    (let [loaded-player (read-string (slurp playerfile))
          player-expansion
            {:team-number teamnumber
             :id id
             :directives '()
             :assigned-image (load-image (:assigned-image loaded-player))
             :defined-decisions (load-decision-code (:defined-decisions loaded-player))
             }]
    (merge loaded-player player-expansion)))

;INTERP UTILITIES

(defn in-distance?
  "UTILITY: check if player in radius"
  [pt dist]
  (fn [xy]
    (let [x1 (first pt) y1 (second pt)
          x2 (first xy) y2 (second xy)
          xdif (- x2 x1) ydif (- y2 y1)]
    (> dist (Math/sqrt (+ (* xdif xdif) (* ydif ydif)))))))

(defn check-predicate
  "UTILITY: take a predicate with one or more arguments and evaluate"
  [pred player]
  (let [opponent (:opponent player)
        team (:team player)
        distfn (in-distance? (:location player) 10)]
  (cond (= (first pred) "and") (reduce #(and %1 %2) (map check-predicate (rest pred)))
        (= (first pred) "or") (reduce #(or %1 %2) (map check-predicate (rest pred)))
        (= (subs pred 0 1) "!") (check-predicate (subs pred 1))
        (= pred "self-ball-posessed?") (:possessing-ball? player)
        (= pred "self-space?") (empty? (filter (fn [p] (distfn (:location p))) opponent))
        (= pred "team-mate-open?") (not (empty? (filter (fn [p] (:open? p)) team)))
        (= pred "self-defensive-third?")
        (= pred "self-offensive-third?")
        (= pred "team-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) team)))
        (= pred "opponent-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) opponent)))
        (= pred "true") true
        (= pred "false") false
        :else false)))

(defn player-decide
  "given a player and the players decision code, make a game play decision"
  [player]
  (let [decisions (:defined-decisions player)
        directives (:directives player)
        first-decision (reduce (fn [_ a]
          (if (check-predicate (first a)) (reduced (second a)) a)) decisions)]


  player))
