(ns darwinsport.testbench.soccerdecisions
  (:require [darwinsport.testbench.soccerfield :as fieldstate]
            [darwinsport.config.runconfig :as config])
  (:gen-class))

; ----------------
; Decision interpreter

(def space-distance (:space? (:soccer-attribs config/framework)))
(def run-speed (:run-speed (:soccer-attribs config/framework)))
(def walk-speed (:walk-speed (:soccer-attribs config/framework)))
(def sprint-speed (:sprint-speed (:soccer-attribs config/framework)))
(def dribble-spacing (:dribble-spacing (:soccer-attribs config/framework)))
(def shot-spacing (:shot-spacing (:soccer-attribs config/framework)))
(def dribble-force (:dribble-force (:soccer-attribs config/framework)))
(def shot-range (:shot-range (:soccer-attribs config/framework)))
(def max-kick-force (:max-kick-force (:soccer-attribs config/framework)))

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
    (+ 90 (Math/toDegrees (Math/atan (/ dx dy))))))

(defn pt-at-angle
  "given angle dist and pt, generate resultant pt"
  [angle pt dist]
  (let [x (first pt)
        y (second pt)]
        (list
          (+ x (* dist (Math/cos angle)))
          (+ y (* dist (Math/sin angle))))))

(defn kick-ball
  "kick the game ball if close enough"
  [player target]
  (let [player-center-x (+ (/ (.getWidth (:assigned-image player)) 2) (first (:location player)))
        player-center-y (+ (/ (.getHeight (:assigned-image player)) 2) (second (:location player)))
        dist-player-to-ball (distance (fieldstate/ball-location) (list player-center-x player-center-y))
        calculate-angle (angle-to-target (:location player) target)
        calculate-velocity (/ (distance (:location player) target) 10)
        throttle-force (if (> calculate-velocity max-kick-force) max-kick-force calculate-velocity)]
        ;(println "Velocity: " calculate-velocity " Throttled: " throttle-force " player loc: " (:location player) "target: " target)
    (if (> shot-spacing dist-player-to-ball)
      (fieldstate/set-ball-move throttle-force calculate-angle))))

(defn dribble
  "dribble move"
  [player speed]
  (let [ball-location (fieldstate/ball-location)
        player-img (:assigned-image player)
        player-center-x (+ (/ (.getWidth player-img) 2) (first (:location player)))
        player-center-y (+ (/ (.getHeight player-img) 2) (second (:location player)))
        angle (:facing-angle player)
        dist-player-to-ball (distance ball-location (list player-center-x player-center-y))]
  (if (> dribble-spacing dist-player-to-ball)
    (fieldstate/set-ball-move (* speed dribble-force) angle))))

(defn move
  "player transform for running at angle"
  [player speed]
  (let [x (first (:location player))
        y (second (:location player))
        angle (Math/toRadians (:facing-angle player))
        new-x (+ x (* speed (Math/cos angle)))
        new-y (+ y (* speed (Math/sin angle)))]
    (if (fieldstate/move-possible? new-x new-y)
      (assoc player :location (list new-x new-y)) player)))

(defn move-bisect
  "player transform to move to a pt between two target pts."
  [player speed p1 p2]
  (let [angle-pts (angle-to-target p1 p2)
        half-dist-pts (/ (distance p1 p2) 2)
        target (pt-at-angle angle-pts p1 half-dist-pts)
        new-angle (angle-to-target (:location player) target)]
        ;TODO: angle +90 screwing something up
    (move
      (assoc player :facing-angle new-angle) speed)))

(defn get-possesser
  "take player, get player on either team possessing ball"
  [player]
  (first
    (filter #(:possessing-ball? %) (concat (:opponent player) (:team player)))))

(defn check-predicate
  "UTILITY: take a predicate with one or more arguments and evaluate"
  [pred player]
  (let [opponent (:opponent player)
        team (:team player)
        distfn #(distance (:location player) %)]
  (cond
    (= (first pred) "and") (reduce #(and %1 %2) (map #(check-predicate % player) (rest pred)))
    (= (first pred) "or") (reduce #(or %1 %2) (map #(check-predicate % player) (rest pred)))
    (= (subs pred 0 1) "!") (not (check-predicate (subs pred 1) player))
    (= pred "self-ball-possessed?") (:possessing-ball? player)
    (= pred "shooting-range?") (> shot-range (distance (:location player) (:target-goal player)))
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
    (vector? action)  (reduce #(perform-action %2 %1) player action)
    (= action "action-longest-pass-forward") player
    (= action "action-self-dribble-forward") (do (dribble player run-speed) (move player run-speed))
    (= action "directive-shoot") player
    (= action "directive-self-pass") player
    (= action "action-short-pass-forward") player
    (= action "action-clear") player
    (= action "action-shoot") (do (kick-ball player (:target-goal player)) (move (assoc player :possessing-ball? false) walk-speed))
    (= action "action-tackle") player
    (= action "action-follow-ball") player
    (= action "action-intersect-path-defend-ball") (move-bisect player sprint-speed (:defend-goal player) (:location (get-possesser player)))
    (= action "action-defensive-drop") (move (assoc player :facing-angle (angle-to-target (:location player) (:defend-goal player))) sprint-speed)
    :else player)
  )

(defn player-decide
  "given a player and the players decision code, make a game play decision"
  [player]
  (let [decisions (:defined-decisions player)
        ;directives (:directives player)
        ]
          (reduce
            (fn [p b]
              (if
                (check-predicate (first b) p)
                (perform-action (second b) p) p))
           player decisions)))
