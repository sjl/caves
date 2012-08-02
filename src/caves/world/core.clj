(ns caves.world.core
  (:use [caves.coords :only [neighbors radial-distance]]))


; Constants -------------------------------------------------------------------
(def world-size [160 50])

; Data structures -------------------------------------------------------------
(defrecord World [tiles entities])
(defrecord Tile [kind glyph color])

(def tiles
  {:floor (->Tile :floor "." :white)
   :wall  (->Tile :wall  "#" :white)
   :up    (->Tile :up    "<" :white)
   :down  (->Tile :down  ">" :white)
   :bound (->Tile :bound "X" :black)})


; Convenience functions -------------------------------------------------------
(defn get-tile-from-tiles [tiles [x y]]
  (get-in tiles [y x] (:bound tiles)))

(defn random-coordinate []
  (let [[cols rows] world-size]
    [(rand-int cols) (rand-int rows)]))


; Querying a world ------------------------------------------------------------
(defn get-tile [world coord]
  (get-tile-from-tiles (:tiles world) coord))

(defn get-tile-kind [world coord]
  (:kind (get-tile world coord)))

(defn set-tile [world [x y] tile]
  (assoc-in world [:tiles y x] tile))

(defn set-tile-floor [world coord]
  (set-tile world coord (:floor tiles)))


(defn get-entities-at [world coord]
  (filter #(= coord (:location %))
          (vals (:entities world))))

(defn get-entity-at [world coord]
  (first (get-entities-at world coord)))

(defn get-entities-around
  ([world coord] (get-entities-around world coord 1))
  ([world coord radius]
     (filter #(<= (radial-distance coord (:location %))
                  radius)
             (vals (:entities world)))))

(defn is-empty? [world coord]
  (and (#{:floor} (get-tile-kind world coord))
       (not (get-entity-at world coord))))

(defn find-empty-tile [world]
  (loop [coord (random-coordinate)]
    (if (is-empty? world coord)
      coord
      (recur (random-coordinate)))))

(defn find-empty-neighbor [world coord]
  (let [candidates (filter #(is-empty? world %) (neighbors coord))]
    (when (seq candidates)
      (rand-nth candidates))))


(defn check-tile
  "Check that the tile at the destination passes the given predicate."
  [world dest pred]
  (pred (get-tile-kind world dest)))

