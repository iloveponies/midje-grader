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
  (when-let [exercise (:exercise (meta fact))]
    (swap! passed? (constantly true))))

(defn after-fact [fact]
  (when-let [exercise (:exercise (meta fact))]
    (let [max-points (:points (meta fact))
          points (if @passed? max-points 0)]
      (swap! total-points
             #(assoc % exercise (+ max-points (get % exercise 0))))
      (swap! earned-points
             #(assoc % exercise (+ points (get % exercise 0)))))))

(defn before-all []
  (swap! total-points (constantly {}))
  (swap! earned-points (constantly {})))

(defn after-all
  ([])
  ([_ _]
     (let [data (for [[exercise points] @earned-points
                      :let [max-points (get @total-points exercise)]]
                  {:exercise exercise
                   :points points
                   :max-points max-points})
           points-total (apply + (vals @earned-points))
           max-points-total (apply + (vals @total-points))]
       (util/emit-one-line "midje-grader:data")
       (util/emit-one-line (json/write-str data))
       (util/emit-one-line "midje-grader:data")
       (pp/print-table (cons {:exercise "total"
                              :points points-total
                              :max-points max-points-total}
                             data)))))

(state/install-emission-map
 (assoc silence/emission-map
   :fail when-fail
   :starting-to-check-fact before-fact
   :finishing-fact after-fact
   :starting-fact-stream before-all
   :finishing-fact-stream after-all))
