(ns rally.core
  (:require [rally.window.mainwindow :refer :all])
  (:gen-class))

(defn -main
  "Start game"
  [& args]
  (start-window)
  )
