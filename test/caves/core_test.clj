(ns caves.core-test
  (:import [caves.core UI World Game])
  (:use clojure.test
        caves.core))

(defn current-ui [game]
  (:kind (last (:uis game))))


(deftest test-start
  (let [game (new Game nil [(new UI :start)] nil)]

    (testing "Enter wins at the starting screen."
      (let [result (process-input game :enter)]
        (is (= (current-ui result) :win))))

    (testing "Other keys lose at the starting screen."
      (let [results (map (partial process-input game)
                         [\space \a \A :escape :up :backspace])]
        (doseq [result results]
          (is (= (current-ui result) :lose)))))))
