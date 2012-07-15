(ns caves.ui.input
  (:use [caves.world :only [random-world smooth-world find-empty-tile]]
        [caves.ui.core :only [->UI]]
        [caves.entities.player :only [move-player make-player]]
        [caves.entities.lichen :only [make-lichen]]
        [caves.entities.bunny :only [make-bunny]]
        [caves.entities.silverfish :only [make-silverfish]])
  (:require [lanterna.screen :as s]))


(defn add-creature [world make-creature]
  (let [creature (make-creature (find-empty-tile world))]
    (assoc-in world [:entities (:id creature)] creature)))

(defn add-creatures [world make-creature n]
  (nth (iterate #(add-creature % make-creature)
                world)
       n))

(defn populate-world [world]
  (let [world (assoc-in world [:entities :player]
                        (make-player (find-empty-tile world)))]
    (-> world
      (add-creatures make-lichen 30)
      (add-creatures make-bunny 20)
      (add-creatures make-silverfish 4))))

(defn reset-game [game]
  (let [fresh-world (random-world)]
    (-> game
      (assoc :world fresh-world)
      (update-in [:world] populate-world)
      (assoc :uis [(->UI :play)]))))


(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

(defmethod process-input :start [game input]
  (reset-game game))


(defmethod process-input :play [game input]
  (case input
    :enter     (assoc game :uis [(->UI :win)])
    :backspace (assoc game :uis [(->UI :lose)])
    \q         (assoc game :uis [])

    \h (update-in game [:world] move-player :w)
    \j (update-in game [:world] move-player :s)
    \k (update-in game [:world] move-player :n)
    \l (update-in game [:world] move-player :e)
    \y (update-in game [:world] move-player :nw)
    \u (update-in game [:world] move-player :ne)
    \b (update-in game [:world] move-player :sw)
    \n (update-in game [:world] move-player :se)

    game))

(defmethod process-input :win [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(->UI :start)])))

(defmethod process-input :lose [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(->UI :start)])))


(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))
