(ns midje-grader
  (:require [midje.emission.plugins.util :as util]
            [midje.emission.plugins.silence :as silence]
            [midje.emission.state :as state]
            [clojure.pprint :as pp]
            [clojure.data.json :as json]))

(def passed? (atom true))
(def total-points (atom {}))
(def earned-points (atom {}))

(defn when-fail [args]
  (swap! passed? (constantly false)))

(defn before-fact [fact]
  (if-let [exercise (:exercise (meta fact))]
    (do
      (swap! passed? (constantly true))
      (util/emit-one-line (format "----\nFor exercise %s" exercise)))))

(defn after-fact [fact]
  (if-let [exercise (:exercise (meta fact))]
    (let [points (:points (meta fact))
          got (if @passed? points 0)]
      (swap! total-points #(assoc % exercise (+ points (get % exercise 0))))
      (swap! earned-points #(assoc % exercise (+ got (get % exercise 0))))
      (util/emit-one-line (format "%d/%d points" got points)))))

(defn before-all []
  (swap! total-points (constantly {}))
  (swap! earned-points (constantly {})))

(defn after-all
  ([])
  ([_ _]
     (let [data (for [[exercise got] @earned-points
                      :let [out-of (get @total-points exercise)]]
                  {"exercise" exercise
                   "got" got
                   "out-of" out-of})]
       (util/emit-one-line "midje-grader:data")
       (util/emit-one-line (json/write-str data))
       (util/emit-one-line "midje-grader:data")
       (pp/print-table data))))

(state/install-emission-map
 (assoc silence/emission-map
   :fail when-fail
   :starting-to-check-fact before-fact
   :finishing-fact after-fact
   :starting-fact-stream before-all
   :finishing-fact-stream after-all))
