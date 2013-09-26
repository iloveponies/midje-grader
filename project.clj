(defproject midje-grader "0.1.0-SNAPSHOT"
  :description "Midje test runner that counts points based on meta data."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [midje "1.5.1"]]
  :profiles {:dev
             {:plugins [[lein-midje "3.0.0"]]}})
