(ns rally.window.mainwindow
  (:require [rally.state.driver :as state])
  (:gen-class))

;Java import
(import '(java.awt Color Dimension Graphics Point))
(import '(java.awt.event KeyListener KeyEvent))
(import '(javax.swing JPanel JFrame SwingUtilities))

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
  (let [event-driver (proxy [java.awt.event.KeyListener] []
                 (keyPressed [^KeyEvent e] (if (= (.getKeyChar e) \q)
                          (System/exit 0)
                          (state/take-input (.getKeyChar e))))
                 (keyReleased [e])
                 (keyTyped [e]))]
  (doto frame
    (.setSize frame-width frame-height)   ;set frame size from preset
    (.setResizable false)
    (.add panel)
    (.pack)
    (.addKeyListener event-driver)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE) ; (repl problem)
    (.setVisible true)
  )
))

(defn start-window []
  (SwingUtilities/invokeLater #(init-window)))
