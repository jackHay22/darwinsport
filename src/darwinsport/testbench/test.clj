(ns darwinsport.testbench.tests
  (:gen-class))

(import javax.imageio.ImageIO)

(defn vehicle-start-attributes
  "given run config, generate start state"
  [config]
  (let [testconfig (:tests config)
        start-location (:vehicle-start testconfig)]
        {:x (first start-location)
         :y (second start-location)
         :speed (:launch-speed testconfig)
         :angle (:launch-angle testconfig)
         :mild-crash 0
         :severe-crash 0
         :moves-made 0}))

(def map-image
  (fn [n config] (javax.imageio.ImageIO/read
            (clojure.java.io/resource
              (nth (:test-list (:tests config)) n)))))

(defn subimage
  "get the subimage within the current map"
  [image]
  (fn [x y width height] (.getSubimage image x y width height)))

(defn change-attribute
  "change an attribute"
  [valmap attrib val]
  (assoc valmap attrib val))

(defn perform-move
  "for a mapfile, reduce moves to location map"
  [map-file config]
  (fn [location move]
    (cond
        (= (first move) "steering-angle") (simulate-move (change-attribute location :angle (second move)))
        (= (first move) "apply-break") ()
        (= (first move) "apply-gas") ()
        (= (first move) "while-straight") ()
        (= (first move) "call-to-angle") ()
        (= (first move) "break-traction") ()
        :else ((:send-log config) "ERROR:bad_instruction")
        )
    ))

(def simulate-rally
  "take in map and vehicle moves,
  return fitness performance value
  moves (where n is angle, f is force %):
  steering-angle <n> ;change steering angle
  apply-brake <f> ;apply brake %
  apply-gas <f> ;apply gas %
  while-straight ;while road is within epsilon of straight
  call-to-angle ;change steering angle to number based on codriver call
  break-traction ;use handbrake to break the traction of the car"
  [map-number movelist config]
  (let [start (vehicle-start-attributes config)
        map-file (map-image map-number config)
        get-sub-image (subimage map-file)
        make-move (perform-move map-file config)
        final-loc (reduce make-move start movelist)]
        {:final-loc (list (:x final-loc) (:y final-loc))
         :moves-used (:moves-made final-loc)
         :crash (:mild-crash final-loc)
         :severe-crash (:severe-crash final-loc)}
  ))
