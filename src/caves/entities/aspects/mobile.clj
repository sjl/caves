(ns caves.entities.aspects.mobile
  (:use [caves.entities.core :only [defaspect]]
        [caves.world :only [is-empty?]]))


(defaspect Mobile
  (move [this world dest]
    {:pre [(can-move? this world dest)]}
    (assoc-in world [:entities (:id this) :location] dest))
  (can-move? [this world dest]
    (is-empty? world dest)))

