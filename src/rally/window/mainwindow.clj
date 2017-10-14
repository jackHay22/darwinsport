(ns rally.window.mainwindow
  (:gen-class))

;Java import
(import '(java.awt Color Dimension Graphics Point))
(import '(javax.swing JPanel JFrame JLabel))

;graphical setup
(def frame-width 1000)
(def frame-height 750)

;Java window components
(def frame (JFrame. "Clojure Rally"))
(def panel (JPanel.))
(.setPreferredSize panel (Dimension. frame-width frame-height))

(def window-color (Color. 10 53 63))
(def background-color  (Color. 69 106 114))
;set panel size

(defn init-window
  "get jpanel in jframe, setup prefs"
  []
  (doto frame
    (.setSize frame-width frame-height)   ;set frame size from preset
    (.setResizable false)
    (.add panel)
    (.pack)
    ;(.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE) ; (repl problem)
    (.setVisible true)
  )
)

(defn start-window
  "initialize plotter window and build graphical elements"
  []
  (init-window)       ;build up window
  (Thread/sleep 500)  ;needs a slight delay (can dial this back for optimization)
  "Window created..."
)
