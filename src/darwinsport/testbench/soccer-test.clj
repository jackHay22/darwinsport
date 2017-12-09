(ns darwinsport.testbench.soccer-test
  (:gen-class))

(def window? (atom false))

(defn update-and-draw
  "update the graphical window"
  [gr]
  )

(defn keypressed
  "respond to keypress event"
  [key]
  ;update player characteristics based on end of key press
  (if (= key :right)
    (println "key press")))
