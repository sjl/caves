(ns caves.entities.lichen
  (:use [caves.entities.core :only [Entity get-id]]))


(defrecord Lichen [id glyph color location])

(defn should-grow []
  (< (rand) 0.01))


(extend-type Lichen Entity
  (tick [this world]
    world))


(defn make-lichen [location]
  (->Lichen (get-id) "F" :green location))


