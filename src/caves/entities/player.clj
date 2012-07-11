(ns caves.entities.player
  (:use [caves.entities.core :only [Entity]]
        [caves.entities.aspects.mobile :only [Mobile move can-move?]]
        [caves.entities.aspects.digger :only [Digger dig can-dig?]]
        [caves.world :only [find-empty-tile get-tile-kind set-tile-floor]]))


(defrecord Player [id loc])

(defn offset-coords [[x y] dx dy]
  [(+ x dx) (+ y dy)])

(defn check-tile
  "Take a player and an offset, and check that the tile at the destination
  passes the given predicate."
  [player world dx dy pred]
  (let [[x y] (offset-coords (:loc player) dx dy)
        dest-tile (get-tile-kind world x y)]
    (pred dest-tile)))

(defn dir-to-offset [dir]
  (case dir
    :w  [-1 0]
    :e  [1 0]
    :n  [0 -1]
    :s  [0 1]
    :nw [-1 -1]
    :ne [1 -1]
    :sw [-1 1]
    :se [1 1]))


(extend-type Player Entity
  (tick [this world]
    world))

(extend-type Player Mobile
  (move [this world dx dy]
    (if (can-move? this world dx dy)
      (update-in world [:player :loc] offset-coords dx dy)
      world))
  (can-move? [this world dx dy]
    (check-tile this world dx dy #{:floor})))

(extend-type Player Digger
  (dig [this world dx dy]
    (if (can-dig? this world dx dy)
      (let [[tx ty] (offset-coords (:loc this) dx dy)]
        (set-tile-floor world tx ty))
      world))
  (can-dig? [this world dx dy]
    (check-tile this world dx dy #{:wall})))


(defn make-player [world]
  (->Player :player (find-empty-tile world)))

(defn move-player [world direction]
  (let [player (:player world)
        [dx dy] (dir-to-offset direction)]
    (cond
      (can-move? player world dx dy) (move player world dx dy)
      (can-dig? player world dx dy) (dig player world dx dy)
      :else world)))
