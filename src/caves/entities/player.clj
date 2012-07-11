(ns caves.entities.player
  (:use [caves.entities.core :only [Entity]]
        [caves.entities.aspects.mobile :only [Mobile move can-move?]]
        [caves.entities.aspects.digger :only [Digger dig can-dig?]]
        [caves.coords :only [destination-coords]]
        [caves.world :only [find-empty-tile get-tile-kind set-tile-floor]]))


(defrecord Player [id loc])

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
    (assoc-in world [:player :loc] dest))
  (can-move? [this world dest]
    (check-tile world dest #{:floor})))

(extend-type Player Digger
  (dig [this world dest]
    {:pre [(can-dig? this world dest)]}
    (set-tile-floor world dest))
  (can-dig? [this world dest]
    (check-tile world dest #{:wall})))


(defn make-player [world]
  (->Player :player (find-empty-tile world)))

(defn move-player [world dir]
  (let [player (:player world)
        target (destination-coords (:loc player) dir)]
    (cond
      (can-move? player world target) (move player world target)
      (can-dig? player world target) (dig player world target)
      :else world)))
