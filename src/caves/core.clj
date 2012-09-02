(ns caves.core
  (:use [caves.ui.core :only [->UI]]
        [caves.ui.drawing :only [draw-game]]
        [caves.entities.core :only [tick]]
        [caves.ui.input :only [get-input process-input]])
  (:require [lanterna.screen :as s]))


; Data Structures -------------------------------------------------------------
(defrecord Game [world uis input debug-flags])

; Main ------------------------------------------------------------------------
(defn tick-entity [world entity]
  (tick entity world))

(defn tick-all [world]
  (reduce tick-entity world (vals (:entities world))))

(defn clear-messages [game]
  (assoc-in game [:world :entities :player :messages] nil))


(defn run-game [game screen]
  (loop [{:keys [input uis] :as game} game]
    (when (seq uis)
      (if (nil? input)
        (let [game (update-in game [:world] tick-all)
              _ (draw-game game screen)
              game (clear-messages game)]
          (recur (get-input game screen)))
        (recur (process-input (dissoc game :input) input))))))

(defn new-game []
  (->Game nil [(->UI :start)] nil {:show-regions false}))

(defn main
  ([] (main :swing false))
  ([screen-type] (main screen-type false))
  ([screen-type block?]
   (letfn [(go []
             (let [screen (s/get-screen screen-type)]
               (s/in-screen screen
                            (run-game (new-game) screen))))]
     (if block?
       (go)
       (future (go))))))


(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                      (args ":swing") :swing
                      (args ":text")  :text
                      :else           :auto)]
    (main screen-type true)))


(comment
  (main :swing false)
  (main :swing true)
  )


