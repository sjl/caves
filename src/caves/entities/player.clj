(ns caves.entities.player
  (:use [caves.entities.core :only [Entity]]
        [caves.entities.aspects.mobile :only [Mobile move can-move?]]
        [caves.entities.aspects.digger :only [Digger dig can-dig?]]
        [caves.coords :only [destination-coords]]
        [caves.world :only [is-empty? get-tile-kind set-tile-floor]]))


(defrecord Player [id glyph color location])

(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))


(extend-type Player Entity
  (tick [this world]
    world))

(extend-type Player Mobile
  (move [this world dest]
    {:pre [(can-move? this world dest)]}
    (assoc-in world [:entities :player :location] dest))
  (can-move? [this world dest]
    (is-empty? world dest)))

(extend-type Player Digger
  (dig [this world dest]
    {:pre [(can-dig? this world dest)]}
    (set-tile-floor world dest))
  (can-dig? [this world dest]
    (check-tile world dest #{:wall})))



(defn make-player [location]
  (->Player :player "@" :white location))

(defn move-player [world dir]
  (let [player (get-in world [:entities :player])
        target (destination-coords (:location player) dir)]
    (cond
      (can-move? player world target) (move player world target)
      (can-dig? player world target) (dig player world target)
      :else world)))
