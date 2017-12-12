(ns darwinsport.testbench.soccerinstrs
  (:gen-class))

;Name space of functions that are allowed to be invoked at runtime by the DSL
(defn self-ball-posessed?
  "check if self possesses the ball
  RETURNS: true if self posessing ball"
  [player]
  (:possessing-ball? player))

(defn team-mate-open?
  "check if any players on team are open to receive ball
  RETURNS: player that is open or false"
  [player]
  (let [team (:team player)]
    (reduce
      (fn [b p] (if (:open? p) (reduced p) b))
      false
      team)))

(defn self-announce-open
  "tell teammates that self open to receive ball
  RETURNS: individual transform that sets :open?"
  [player]
  (assoc player :open? true))

(defn self-announce-marked
  "tell teammates that self not open to recieve ball
  RETURNS: individual transfrom that sets :open?"
  [player]
  (assoc player :open? false))

(defn self-defensive-third?
  "check if self in defensive third of field"
  [player]
  )

(defn self-offensive-third?
    "check if self in defensive third of field"
    [player]
  )

(defn team-possessing-ball?
  "check if team posesses ball
  RETURNS: player possessing ball "
  (let [team (:team player)]
    (reduce
      (fn [b p] (if (:posessing-ball? p) (reduced p) b))
      false
      team)))

(defn opponent-possessing-ball?
  "check if opponent on oppossing team possesses ball
  RETURNS: individual on opposing team that possesses ball or false"
  [player]
  (let [team (:opponent player)]
    (reduce
      (fn [b p] (if (:posessing-ball? p) (reduced p) b))
      false
      team)))

(defn action-pass-forward-to-open
  "action: return action transform
  RETURN: action"
  [player]

)
