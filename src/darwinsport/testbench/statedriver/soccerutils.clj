(ns darwinsport.testbench.statedriver.soccerutils
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

(defn bounded-box-intersection?
  "take two upper left corner pts, two sets of
  w h, check intersection"
  [xy1 w1 h1]
  (let [x11 (first xy1)
        y11 (second xy1)
        x12 (+ x11 w1)
        y12 (+ y11 h1)
        boundfn1 (pt-in-bounds x11 y11 w1 h1)]

  (fn [xy2 w2 h2]
    (let [x21 (first xy2)
          y21 (second xy2)
          x22 (+ x21 w2)
          y22 (+ y21 h2)
          boundfn2 (pt-in-bounds x21 y21 w2 h2)]
    (or
      ;TODO: fix how shitty this is
      (boundfn1 x21 y21) (boundfn1 x22 y21) (boundfn1 x21 y22) (boundfn1 x22 y22)
      (boundfn2 x21 y21) (boundfn2 x22 y21) (boundfn2 x21 y22) (boundfn2 x22 y22))))))

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

(defn associate-team-to-player
    "take a player, associate that player's team field with a team list"
    [t1 t2 result-team]
    (fn [player]
        (let [id (:id player)
              team (if (= (:team-number player) 1) t1 t2)]
          (assoc player result-team (filter (fn [p] (not (= (:id p) id))) team)))))

(defn associate-teams
  "associate teams with each player"
  [all-players]
  (let [team1 (filter (fn [p] (= (:team-number p) 1)) all-players)
        team2 (filter (fn [p] (= (:team-number p) 2)) all-players)
        team-aware (map (associate-team-to-player team1 team2 :team) all-players)]
        (map (associate-team-to-player team2 team1 :opponent) team-aware)))

(defn load-player
    "takes a player in file and loads images, code, etc..."
    [playerfile teamnumber id config]
    (let [loaded-player (read-string (slurp playerfile))
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
    (merge loaded-player player-expansion)))
