(ns darwinsport.testbench.soccerutils
  (:gen-class))

;LOAD UTILITIES

(defn load-image
  [location]
    (javax.imageio.ImageIO/read
    (clojure.java.io/resource location)))

(defn load-decisions
  "take decision file and turn into structure"
  [dec-file]
  (read-string (slurp dec-file)))

(defn load-player
    "takes a player in file and loads"
    [playerfile teamnumber id]
    (let [loaded-player (read-string (slurp playerfile))
          player-expansion
            {:team-number teamnumber
             :id id
             :directives '()
             :assigned-image (load-image (:assigned-image loaded-player))
             :defined-decisions (load-decisions (:defined-decisions loaded-player))}]
    (merge loaded-player player-expansion)))


;INTERP UTILITIES
(defn player-decide
  "given a player and the players decision code, make a game play decision"
  [player]
  (let [decisions (:defined-decisions player)
        directives (:directives player)]

        ;use ((ns-resolve darwinsport.testbench.soccerinstrs decisions) player)

  player))
