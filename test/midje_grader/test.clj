(ns midje-grader.test
  (:require [midje.sweet :refer :all]
            [midje-grader :refer :all]))

(facts "this is right" {:exercise :right
                        :points 1}
       true => true)

(facts "this is wrong" {:exercise :wrong
                        :points 1}
       false => true)

(facts "doodaa" {:exercise 1
                 :points 1}
       1 => 1
       1 =not=> 2)

(facts "more doodaa" {:exercise 1
                      :points 2}
       2 => 2
       2 => 3)
