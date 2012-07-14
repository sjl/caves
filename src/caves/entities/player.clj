(ns caves.entities.player
  (:use [caves.entities.core :only [Entity add-aspect]]
        [caves.entities.aspects.mobile :only [Mobile move can-move?]]
        [caves.entities.aspects.digger :only [Digger dig can-dig?]]
        [caves.entities.aspects.attacker :only [Attacker attack]]
        [caves.coords :only [destination-coords]]
        [caves.world :only [get-entity-at]]))


(defrecord Player [id glyph color location])

(extend-type Player Entity
  (tick [this world]
    world))

(add-aspect Player Mobile)
(add-aspect Player Digger)
(add-aspect Player Attacker)

(defn make-player [location]
  (->Player :player "@" :white location))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)
        entity-at-target (get-entity-at world target)]
    (cond
      entity-at-target (attack player world entity-at-target)
      (can-move? player world target) (move player world target)
      (can-dig? player world target) (dig player world target)
      :else world)))
