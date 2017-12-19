(ns darwinsport.testbench.statedriver.ui
  (:gen-class)
  (:require [seesaw.graphics :as sawgr]
            [seesaw.icon :as sawicon]))

(import '(java.awt Color Font))
(def text-color (Color. 229 172 39))
(def selected-color (Color. 229 132 14))
(def score-font (Font. "SansSerif" Font/PLAIN 20))

(def display-state
  (atom {:options '({:text "Toggle analytics"
                     :state false}
                     {:text "Other"
                     :state false})
         :total-options 2
         :selected 0}))

(defn make-selection
  "select the current option"
  []
  (let [state (deref display-state)]
  ;TODO: some sort of swap!/update in
      ;(update-in state [1 :age] inc)))
      ))

(defn move
  "move to a new option based on input fn"
  [update]
  (let [disp (deref display-state)
        current (:selected disp)
        total (:selected disp)
        new (mod (update current) total)]
  (swap! display-state assoc :selected new)))

(defn get-ui-state
  "return the button state"
  []
  (:options (deref display-state)))

(defn draw-option
  "take option and draw"
  [gr status text x]

)

(defn draw-text-options
  "create a button based on an image and text"
  [gr]
  (let [state (deref display-state)
        text-elems (:options state)]
        ;(.setColor gr text-color)
        ;    (.setFont gr score-font)
        ;    (.drawString gr score 465 22)
        ))
