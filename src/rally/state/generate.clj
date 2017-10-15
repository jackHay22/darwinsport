(ns rally.state.generate
  (:gen-class)
)

;ex road state: '((road-edge-x road-loc-y road-width))

(defn load-road-start
  "creates a list of road objects to draw"
  []

  )

(defn update-road-state
    "updates a list of road objects to draw"
    [road-state max-y]
    (let
      [current-backlog (first (first road-state)) ;some negative
      new-state (map (fn [frame] '((first frame)(inc (second frame))(nth frame 2))))]
    (filter
    (fn [slice] (> (second slice) max-y)) (if (> current-backlog -20)
        (repeatedly ()) new-state)))) ;add new randoms to state

(defn add-road-state
  "takes current road state, adds slices at top"
  [state]

  )
