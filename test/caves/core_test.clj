(ns caves.core-test
  (:import [caves.core UI World Game])
  (:use clojure.test
        caves.core))

(defn current-ui [game]
  (:kind (last (:uis game))))


(deftest test-start
  (let [game (new Game nil [(new UI :start)] nil)]
    (testing "Any key sends you to the play screen and generates a world."
      (let [results (map (partial process-input game)
                         [:enter \space \a \A :escape :up :backspace])]
        (doseq [result results]
          (is (= (current-ui result) :play))
          (is (not= nil (:world result))))))))
