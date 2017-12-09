(ns darwinsport.testbench.soccer-window
  (:gen-class)
  (:require [seesaw.core :as sawcore]
            [seesaw.graphics :as sawgr]
            [darwinsport.testbench.soccer-test :as state]))

(def control-keys {java.awt.event.KeyEvent/VK_UP :up
                   java.awt.event.KeyEvent/VK_DOWN :down
                   java.awt.event.KeyEvent/VK_LEFT :left
                   java.awt.event.KeyEvent/VK_RIGHT :right
                   java.awt.event.KeyEvent/VK_SPACE :space})

(defn start-window
  "initialize the field window"
  [width height]
  (let [canvas (sawcore/canvas
                  :id :canvas
                  :background :red
                  :size [width :by height]
                  :paint (fn [c g]
                           (state/update-and-draw g)))
        panel (sawcore/vertical-panel
                  :id :panel
                  :items [canvas])
        frame (sawcore/frame
                  :title "DarwinSport 2018"
                  :width width
                  :height height
                  :content panel
                  :resizable? false
                  :id :frame
                  :listen [:key-pressed
                            (fn [e] (let [k (.getKeyCode e)]
                                (state/keypressed (control-keys k))))
                           :key-released
                            (fn [e] e)]
                  :on-close :exit)
        ;delay 20 is around 50 fps
        game-loop (sawcore/timer (fn [e] (sawcore/repaint! frame)) :delay 20 :start? false)]

    ;run window loop
    (sawcore/native!)
    (sawcore/show! frame)
    (.start game-loop)))
