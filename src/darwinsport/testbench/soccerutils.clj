(ns darwinsport.testbench.soccerutils
  (:require [darwinsport.testbench.soccerfield :as fieldstate])
  (:gen-class))

;LOAD UTILITIES

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
             :defined-decisions (load-decision-code (:defined-decisions loaded-player))
             }]
    (merge loaded-player player-expansion)))

;INTERP UTILITIES

(defn distance
  "UTILITY: check if player in radius"
  [xy1 xy2]
    (let [x1 (first xy1) y1 (second xy1)
          x2 (first xy2) y2 (second xy2)
          xdif (- x2 x1) ydif (- y2 y1)]
    (> dist (Math/sqrt (+ (* xdif xdif) (* ydif ydif))))))


(defn check-predicate
  "UTILITY: take a predicate with one or more arguments and evaluate"
  [pred player]
  (let [opponent (:opponent player)
        team (:team player)
        space-distance 10
        distfn #(distance (:location player) %)]
  (cond
    (= (first pred) "and") (reduce #(and %1 %2) (map check-predicate (rest pred)))
    (= (first pred) "or") (reduce #(or %1 %2) (map check-predicate (rest pred)))
    (= (subs pred 0 1) "!") (check-predicate (subs pred 1))
    (= pred "self-ball-posessed?") (:possessing-ball? player)
    (= pred "self-space?") (empty? (filter (fn [p] (> space-distance (distfn (:location p)))) opponent))
    (= pred "team-mate-open?") (not (empty? (filter (fn [p] (:open? p)) team)))
    (= pred "self-defensive-third?") false ;TODO
    (= pred "self-offensive-third?") false ;TODO
    (= pred "team-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) team)))
    (= pred "opponent-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) opponent)))
    (= pred "true") true
    (= pred "false") false
    :else false)))

(defn perform-action
  "UTILITY: perform the action(s) described"
  [action-seq player]
  (cond
    (= (first action-seq) "action-longest-pass-forward")
    (= (first action-seq) "action-self-dribble-forward")
    (= (first action-seq) "directive-shoot")
    (= (first action-seq) "directive-self-pass")
    (= (first action-seq) "action-short-pass-forward")
    (= (first action-seq) "action-clear")
    (= (first action-seq) "action-shoot")
    (= (first action-seq) "action-tackle")
    (= (first action-seq) "action-follow-ball")
    :else "done"))

(defn player-decide
  "given a player and the players decision code, make a game play decision"
  [player]
  (let [decisions (:defined-decisions player)
        directives (:directives player)
        decisions (filter (fn [pa] (check-predicate (first a))) decisions)
        actions-to-perform (map second decisions)]


        ;TODO: make sure changes made to player are returned
  player))
