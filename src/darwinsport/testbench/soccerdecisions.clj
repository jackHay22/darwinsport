(ns darwinsport.testbench.soccerdecisions
  (:require [darwinsport.testbench.soccerfield :as fieldstate]
            [darwinsport.testbench.statedriver.soccerutils :as utilities]
            [darwinsport.config.runconfig :as config])
  (:gen-class))

; ----------------
; Decision interpreter

(def config-data (:soccer-attribs config/framework))
(def first-decision-only? (:first-decision-only? config/framework))

;soccer-specific constants
(def space-distance (:space? config-data))
(def run-speed (:run-speed config-data))
(def walk-speed (:walk-speed config-data))
(def lateral-speed (:lateral-speed config-data))
(def sprint-speed (:sprint-speed config-data))
(def dribble-spacing (:dribble-spacing config-data))
(def shot-spacing (:shot-spacing config-data))
(def dribble-force (:dribble-force config-data))
(def shot-range (:shot-range config-data))
(def max-kick-force (:max-kick-force config-data))
(def settle-radius (:settle-radius config-data))

(defn increment-analytic
  "take player and analytic type, increment
  possible:
    :goals
    :tackles
    :passes
    :total-touches"
  [player type]
  (assoc player type (+ 1 (type player))))

(defn distance
  "UTILITY: check if player in radius"
  [xy1 xy2]
    (let [x1 (first xy1) y1 (second xy1)
          x2 (first xy2) y2 (second xy2)
          xdif (- x2 x1) ydif (- y2 y1)]
    (Math/sqrt (+ (* xdif xdif) (* ydif ydif)))))

(defn angle-to-target
    "finds the angle between the location and the target"
    [xy1 xy2]
    (let [dx (- (first xy2) (first xy1))
          dy (- (second xy2) (second xy1))]
    ;find (complement?) of angle between pts -- still potentially broken
    (Math/toDegrees (Math/atan2 dy dx))))

(defn pt-at-angle
  "given angle dist and pt, generate resultant pt"
  [angle pt dist]
  (let [x (first pt)
        y (second pt)]
        (list
          (+ x (* dist (Math/cos angle)))
          (+ y (* dist (Math/sin angle))))))

(defn self-closest-to-ball
  "check if player is the closest to the ball on their team"
  [player]
  ;TODO
  )

(defn kick-ball
  "kick the game ball if close enough"
  [player target]
  (let [player-center-x (+ (/ (.getWidth (:assigned-image player)) 2) (first (:location player)))
        player-center-y (+ (/ (.getHeight (:assigned-image player)) 2) (second (:location player)))
        dist-player-to-ball (distance (fieldstate/ball-location) (list player-center-x player-center-y))
        calculate-angle (angle-to-target (:location player) target)
        calculate-velocity (/ (distance (:location player) target) 10)
        throttle-force (if (> calculate-velocity max-kick-force) max-kick-force calculate-velocity)]
    (if (> shot-spacing dist-player-to-ball)
      (do (increment-analytic player :total-touches)  ;TODO: check if this action was effective
          (fieldstate/set-ball-move throttle-force calculate-angle)))))

(defn dribble
  "dribble move"
  [player speed]
  (let [ball-location (:ball-location player)
        player-img (:assigned-image player)
        player-center-x (+ (/ (.getWidth player-img) 2) (first (:location player)))
        player-center-y (+ (/ (.getHeight player-img) 2) (second (:location player)))
        angle (:facing-angle player)
        dist-player-to-ball (distance ball-location (list player-center-x player-center-y))]
  (if (> dribble-spacing dist-player-to-ball)
    (do (increment-analytic player :total-touches)
        (fieldstate/set-ball-move (* speed dribble-force) angle)))))

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

(defn lateral-move-between-pts
  "take p1, p2, find y value on line, move"
  [player p1 p2]
  (let [player-x (first (:location player))
        player-y (second (:location player))
        line-eqn (utilities/pts-eqn p1 p2)
        target-y (line-eqn player-x)
        dif (- target-y player-y)
        throttle-speed (cond
                (> dif lateral-speed) lateral-speed
                (< dif (- 0 lateral-speed)) (- 0 lateral-speed)
                :else dif)]
        ;TODO: use move instead of calc
        (assoc player :location (list player-x (+ player-y throttle-speed)))))

(defn move-bisect
  "player transform to move to a pt between two target pts."
  [player speed p1 p2]
  (let [line-eqn (utilities/pts-eqn p1 p2)
        new-x (/ (+ (first p2) (first p1)) 2)
        new-y (line-eqn new-x)
        new-angle (angle-to-target (:location player) (list new-x new-y))]
        ;TODO: wrong direction? (also, different than lateral-move-between-pts)
        ;potentially set x, y
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
    (= pred "ball-settle-radius?") (> settle-radius (distance (:location player) (:ball-location player)))
    (= pred "shooting-range?") (> shot-range (distance (:location player) (:target-goal player)))
    (= pred "self-space?") (empty? (filter (fn [p] (> space-distance (distfn (:location p)))) opponent))
    (= pred "team-mate-open?") (not (empty? (filter (fn [p] (:open? p)) team)))
    (= pred "open-forward-pass?") false ;check if there is a team player far enough from an opponent
    (= pred "self-defensive-third?") false ;TODO
    (= pred "self-offensive-third?") false ;TODO
    (= pred "self-defending-square-to-goal") false ;TODO
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
    (= action "action-settle-ball")
            (do (fieldstate/set-ball-move 0 (:facing-angle player)) (assoc player :posessing-ball? true))
    (= action "directive-shoot") player
    (= action "directive-self-pass") player
    (= action "action-short-pass-forward") player
    (= action "action-clear") player
    (= action "action-shoot")
            (do (kick-ball player (:target-goal player)) (move (assoc player :possessing-ball? false) walk-speed))
    (= action "action-tackle") player
    (= action "action-follow-ball") player
    (= action "action-lateral-goal-tend")
            (assoc (lateral-move-between-pts player (:defend-goal player) (:ball-location player))
                :facing-angle (angle-to-target (:ball-location player) (:location player)))
    (= action "action-intersect-path-defend-ball")
            (move-bisect player sprint-speed (:defend-goal player) (:location (get-possesser player)))
    (= action "action-defensive-drop") ;TODO: not working
            (move (assoc player :facing-angle
                  (angle-to-target (:location player) (:defend-goal player))) sprint-speed)
    :else player)
  )

(defn player-decide
  "given a player and the players decision code, make a game play decision or all possible decisions"
  [player]
  (let [decisions (:defined-decisions player)
        ;directives (:directives player)
        ]
          (reduce
            (fn [p b]
              (if
                (check-predicate (first b) p)
                (let [player-res (perform-action (second b) p)]
                  (if first-decision-only? (reduced player-res) player-res))
                 p))
           player decisions)))
