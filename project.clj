(defproject midje-grader "0.1.0-SNAPSHOT"
  :description "Midje test runner that counts points based on meta data."
  :url "http://github.com/iloveponies/midje-grader"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.3"]
                 [midje "1.5.1"]]
  :profiles {:dev
             {:plugins [[lein-midje "3.0.0"]]}})
