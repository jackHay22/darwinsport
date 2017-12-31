(ns darwinsport.testbench.soccerdecisions
  (:require [darwinsport.testbench.soccerdecisionutils :as decisionutils]
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

(defn check-predicate
  "UTILITY: take a predicate with one or more arguments and evaluate"
  [pred player]
  (let [opponent (:opponent player)
        team (:team player)
        distfn #(decisionutils/distance (:location player) %)]
  (cond
    (= (first pred) "and") (reduce #(if (check-predicate %2 player) %1 (reduced false)) true (rest pred))
    (= (first pred) "or") (reduce #(if (check-predicate %2 player) (reduced true) %1) false (rest pred))
    (= (subs pred 0 1) "!") (not (check-predicate (subs pred 1) player))
    (= pred "self-ball-possessed?") (:possessing-ball? player)
    (= pred "ball-settle-radius?") (> settle-radius (decisionutils/distance (:location player) (:ball-location player)))
    (= pred "shooting-range?") (> shot-range (decisionutils/distance (:location player) (:target-goal player)))
    (= pred "self-space?") (empty? (filter (fn [p] (> space-distance (distfn (:location p)))) opponent))
    (= pred "team-mate-open?") (not (empty? (filter (fn [p] (:open? p)) team)))
    (= pred "ball-forward?") (let [px (first (:location player))   ;note: there is no ball-back as this can just be negated
                                   bx (first (:ball-location player))
                                   gx (first (:target-goal player))]
                                   (or (and (> bx px) (> gx px))
                                       (and (< bx px) (< gx px))))
    (= pred "self-closest-to-ball?")
          (let [player-ball-dist (distfn (:ball-location player))]
              (empty? (filter #(< (decisionutils/distance (:location %) (:ball-location player)) player-ball-dist) team)))
    (= pred "self-defending-square-to-goal?") (decisionutils/approx-bisecting-pts? player (:defend-goal player)
                                                  (:ball-location player) 60) ;settle-radius) ;TODO: sort this out
    (= pred "tackle-range?") (> space-distance (decisionutils/distance (:location player) (:location (decisionutils/get-possesser player))))
    (= pred "team-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) team)))
    (= pred "opponent-possessing-ball?") (not (empty? (filter (fn [p] (:possessing-ball? p)) opponent)))
    (= pred "true") true
    (= pred "false") false
    :else (do (println "DEBUG: " pred " predicate not parsed") false))))

(defn perform-action
  "UTILITY: perform the action(s) described
  RETURN: each actoin should return the player
  and produce any ball manipulation side effects"
  [action player]
  (cond
    (vector? action)  (reduce #(perform-action %2 %1) player action)
    (= action "action-longest-pass-forward") player
    (= action "action-dribble-forward") (let [facing-goal (assoc player :facing-angle
                                              (decisionutils/angle-to-target (:location player) (:target-goal player)))]
                                          (decisionutils/dribble facing-goal run-speed dribble-spacing dribble-force)
                                          (decisionutils/move facing-goal run-speed))
    (= action "action-settle-ball")
            (do (decisionutils/ball-move 0 (:facing-angle player)) (assoc player :possessing-ball? true))
    (= action "action-recover-ball")
          (decisionutils/move
                (assoc player :facing-angle (decisionutils/angle-to-target (:location player) (:ball-location player))) run-speed)
    (= action "action-leading-pass") (decisionutils/leading-pass player
                                     (decisionutils/get-best-open-target player) sprint-speed max-kick-force shot-spacing)
    (= action "action-clear") player
    (= action "action-shoot")
            (do (decisionutils/kick-ball player (:target-goal player) max-kick-force shot-spacing)
                (decisionutils/move (assoc player :possessing-ball? false) walk-speed))
    (= action "action-tackle") player
    (= action "action-forward-run") (decisionutils/move (decisionutils/forward-run player) run-speed)
    (= action "action-lateral-goal-tend")
            (assoc (decisionutils/lateral-move-between-pts player (:defend-goal player) (:ball-location player) lateral-speed)
                  :facing-angle (decisionutils/angle-to-target (:ball-location player) (:location player)))
    (= action "action-intersect-path-defend-ball")
            (decisionutils/move-bisect player (:defend-goal player) (:location (decisionutils/get-possesser player)) sprint-speed)
    (= action "action-defensive-drop")
            (decisionutils/move (assoc player :facing-angle
                  (decisionutils/angle-to-target (:location player) (:defend-goal player))) sprint-speed)
    (= action "action-step-to-possesser") (decisionutils/move (assoc player :facing-angle
                  (decisionutils/angle-to-target (:location player) (:location (decisionutils/get-possesser player)))) sprint-speed)
    (= action "action-outside-run") (decisionutils/move (decisionutils/outside-forward-run player) sprint-speed)
    :else (do (println "DEBUG: " action " action not parsed") player)))

(defn player-decide
  "given a player and the players decision code, make a game play decision or all possible decisions"
  [player]
  (let [decisions (:defined-decisions player)]
          (reduce
            (fn [p b]
              (if
                (check-predicate (first b) p)
                (let [player-res (perform-action (second b) p)]
                  (if first-decision-only? (reduced player-res) player-res))
                 p))
           player decisions)))
