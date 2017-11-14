(defproject rally "0.1.0-SNAPSHOT"
  :description "Simple rally game in clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot rally.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
