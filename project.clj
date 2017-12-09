(defproject darwinsport "0.1.0-SNAPSHOT"
  :description "Remote test bench for Darwin solver"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [clj-http "3.7.0"]]
  :main ^:skip-aot darwinsport.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
