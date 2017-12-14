(ns darwinsport.testbench.soccerdecisions
  (:require [darwinsport.testbench.soccerfield :as fieldstate])
  (:gen-class))

; ----------------
; Decision interpreter

(defn distance
  "UTILITY: check if player in radius"
  [xy1 xy2]
    (let [x1 (first xy1) y1 (second xy1)
          x2 (first xy2) y2 (second xy2)
          xdif (- x2 x1) ydif (- y2 y1)]
    (Math/sqrt (+ (* xdif xdif) (* ydif ydif)))))

(defn angle-to-target
    "finds the angle between the vehicle location and the target"
    [xy1 xy2]
    (let [dx (- (first xy2) (first xy1))
          dy (- (second xy2) (second xy1))]
    (Math/toDegrees (Math/atan (/ dx dy)))))

(defn kick-ball
  "kick the game ball if close enough"
  [player target type]
  (let [player-ball-distance (distance (fieldstate/ball-location) (:location player))
        calculate-angle (angle-to-target (:location player) target)
        calculate-velocity (distance (:location player) target)]
    (fieldstate/set-ball-move calculate-velocity calculate-angle)))

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
    (= pred "close-to-goal?") false ;TODO
    (= pred "tackle-range?") false ;TODO
    (= pred "team-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) team)))
    (= pred "opponent-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) opponent)))
    (= pred "true") true
    (= pred "false") false
    :else false)))

(defn perform-action
  "UTILITY: perform the action(s) described
  RETURN: each actoin should return the player
  and produce any ball manipulation side effects"
  [action player]
  (cond
    (= action "action-longest-pass-forward") false
    (= action "action-self-dribble-forward") false
    (= action "directive-shoot") false
    (= action "directive-self-pass") false
    (= action "action-short-pass-forward") false
    (= action "action-clear") false
    (= action "action-shoot") false
    (= action "action-tackle") false
    (= action "action-follow-ball") false
    (= action "action-defensive-drop") false
    :else player)
  )

(defn player-decide
  "given a player and the players decision code, make a game play decision"
  [player]
  (let [decisions (:defined-decisions player)
        directives (:directives player)]
          (reduce
            (fn [p b]
              (if
                (check-predicate (first b) p)
                (perform-action (second b) p) p))
           player decisions)))
