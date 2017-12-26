(ns darwinsport.testbench.soccerdecisionutils
  (:require [darwinsport.testbench.statedriver.soccerutils :as utilities]
            [darwinsport.testbench.soccerfield :as fieldstate]
            [darwinsport.config.runconfig :as config])
  (:gen-class))

(defn increment-analytic
  "take player and analytic type, increment
  possible:
    :goals
    :tackles
    :passes
    :total-touches"
  [player type]
  (assoc player type (+ 1 (type player))))

(defn ball-move
  "wrap fieldstate"
  [force angle]
  (fieldstate/set-ball-move force angle))

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
  [player target max-kick-force shot-spacing]
  (let [player-center-x (+ (/ (.getWidth (:assigned-image player)) 2) (first (:location player)))
        player-center-y (+ (/ (.getHeight (:assigned-image player)) 2) (second (:location player)))
        dist-player-to-ball (distance (fieldstate/ball-location) (list player-center-x player-center-y))
        calculate-angle (angle-to-target (:location player) target)
        calculate-velocity (/ (distance (:location player) target) 10)
        throttle-force (if (> calculate-velocity max-kick-force) max-kick-force calculate-velocity)]
    (if (> shot-spacing dist-player-to-ball)
      (do (increment-analytic player :total-touches)  ;TODO: check if this action was effective
          (ball-move throttle-force calculate-angle)))))

(defn leading-pass
  "make leading pass to space"
  [player target target-speed max-kick-force]
  (let [target-loc-current (:location target)
        target-angle (:facing-angle target)
        p2 (pt-at-angle target-angle target-loc-current target-speed)
        target-trajectory-fn (utilities/pts-eqn target-loc-current p2)
        relative-frame-distance (/ (distance (:location player) target-loc-current) max-kick-force)
        x-guess 0 ;TODO: using correct direction, estimate frames ahead using relative frame distance and calculate y
        y-guess 0]

    ))

(defn dribble
  "dribble move"
  [player speed dribble-spacing dribble-force]
  (let [ball-location (:ball-location player)
        player-img (:assigned-image player)
        player-center-x (+ (/ (.getWidth player-img) 2) (first (:location player)))
        player-center-y (+ (/ (.getHeight player-img) 2) (second (:location player)))
        angle (:facing-angle player)
        dist-player-to-ball (distance ball-location (list player-center-x player-center-y))]
  (if (> dribble-spacing dist-player-to-ball)
    (do (increment-analytic player :total-touches)
        (ball-move (* speed dribble-force) angle)))))

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
  [player p1 p2 lateral-speed]
  (let [player-x (first (:location player))
        player-y (second (:location player))
        line-eqn (utilities/pts-eqn p1 p2)
        target-y (line-eqn player-x)
        dif (- target-y player-y)
        throttle-speed (cond
                (> dif lateral-speed) lateral-speed
                (< dif (- 0 lateral-speed)) (- 0 lateral-speed)
                :else dif)]
        (assoc player :location (list player-x (+ player-y throttle-speed)))))

(defn move-bisect
  "player transform to move to a pt between two target pts."
  [player p1 p2 speed]
  (let [line-eqn (utilities/pts-eqn p1 p2)
        new-x (/ (+ (first p2) (first p1)) 2)
        new-y (line-eqn new-x)
        new-angle (angle-to-target (:location player) (list new-x new-y))]
    (move
      (assoc player :facing-angle new-angle) speed)))

(defn forward-run
  "player makes forward run at sprint speed"
  [player]
  (let [attacking-goal (:target-goal player)]
    (if (> (first attacking-goal) (first (:location player)))
      (assoc player :facing-angle 0)
      (assoc player :facing-angle 180))))

(defn approx-bisecting-pts?
  "check if player is approximately between two pts"
  [player p1 p2 epsilon]
  (let [ln-eqn (utilities/pts-eqn p1 p2)
        px (first (:location player))
        py (second (:location player))
        line-y (ln-eqn px)]
        (> epsilon (Math/abs (int (- line-y py))))))

(defn get-possesser
  "take player, get player on either team possessing ball"
  [player]
  (first
    (filter #(:possessing-ball? %) (concat (:opponent player) (:team player)))))

(defn get-best-open-target
  "find the best positioned forward player"
  [player]
  (let [open-team (filter #(:open? %) (:team player))
        forward-dir-x (first (:target-goal player))]
        ;TODO: check for players between player and target goal, if none, first open
  ))
