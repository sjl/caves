(ns caves.entities.aspects.mobile
  (:use [caves.entities.core :only [defaspect]]
        [caves.world :only [is-empty?]]))


(defaspect Mobile
  (move [this dest world]
    {:pre [(can-move? this dest world)]}
    (assoc-in world [:entities (:id this) :location] dest))
  (can-move? [this dest world]
    (is-empty? world dest)))

