(ns midje-grader
  (:require [midje.emission.plugins.util :as util]
            [midje.emission.plugins.default :as default]
            [midje.emission.state :as state]
            [clojure.pprint :as pp]))

(def passed? (atom true))
(def total-points (atom {}))
(def earned-points (atom {}))

(defn when-fail [args]
  (swap! passed? (constantly false))
  ((:fail default/emission-map) args))

(defn before-fact [fact]
  (let [exercise (:exercise (meta fact))]
    (swap! passed? (constantly true))
    (util/emit-one-line (format "----\nFor exercise %s" exercise))))

(defn after-fact [fact]
  (let [exercise (:exercise (meta fact))
        points (:points (meta fact))
        got (if @passed? points 0)]
    (swap! total-points #(assoc % exercise (+ points (get % exercise 0))))
    (swap! earned-points #(assoc % exercise (+ got (get % exercise 0))))
    (util/emit-one-line (format "%d/%d points" got points))))

(defn after-all
  ([])
  ([_ _]
     (pp/print-table
      (for [[exercise got] @earned-points
            :let [out-of (get @total-points exercise)]]
        {"exercise" exercise
         "got" got
         "out-of" out-of}))))

(state/install-emission-map
 (assoc default/emission-map
   :fail when-fail
   :starting-to-check-fact before-fact
   :finishing-fact after-fact
   :finishing-fact-stream after-all))
