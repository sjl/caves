(ns caves.world
  (:use [caves.coords :only [neighbors]]))


; Constants -------------------------------------------------------------------
(def world-size [160 50])

; Data structures -------------------------------------------------------------
(defrecord World [tiles entities])
(defrecord Tile [kind glyph color])

(def tiles
  {:floor (->Tile :floor "." :white)
   :wall  (->Tile :wall  "#" :white)
   :bound (->Tile :bound "X" :black)})


; Convenience functions -------------------------------------------------------
(defn get-tile-from-tiles [tiles [x y]]
  (get-in tiles [y x] (:bound tiles)))

(defn random-coordinate []
  (let [[cols rows] world-size]
    [(rand-int cols) (rand-int rows)]))


; World generation ------------------------------------------------------------
(defn random-tiles []
  (let [[cols rows] world-size]
    (letfn [(random-tile []
              (tiles (rand-nth [:floor :wall])))
            (random-row []
              (vec (repeatedly cols random-tile)))]
      (vec (repeatedly rows random-row)))))


(defn get-smoothed-tile [block]
  (let [tile-counts (frequencies (map :kind block))
        floor-threshold 5
        floor-count (get tile-counts :floor 0)
        result (if (>= floor-count floor-threshold)
                 :floor
                 :wall)]
    (tiles result)))

(defn block-coords [x y]
  (for [dx [-1 0 1]
        dy [-1 0 1]]
    [(+ x dx) (+ y dy)]))

(defn get-block [tiles x y]
  (map (partial get-tile-from-tiles tiles)
       (block-coords x y)))

(defn get-smoothed-row [tiles y]
  (mapv (fn [x]
          (get-smoothed-tile (get-block tiles x y)))
        (range (count (first tiles)))))

(defn get-smoothed-tiles [tiles]
  (mapv (fn [y]
          (get-smoothed-row tiles y))
        (range (count tiles))))

(defn smooth-world [{:keys [tiles] :as world}]
  (assoc world :tiles (get-smoothed-tiles tiles)))


(defn random-world []
  (let [world (->World (random-tiles) {})
        world (nth (iterate smooth-world world) 3)]
    world))


; Querying a world ------------------------------------------------------------
(defn get-tile [world coord]
  (get-tile-from-tiles (:tiles world) coord))

(defn get-tile-kind [world coord]
  (:kind (get-tile world coord)))

(defn set-tile [world [x y] tile]
  (assoc-in world [:tiles y x] tile))

(defn set-tile-floor [world coord]
  (set-tile world coord (:floor tiles)))


(defn get-entity-at [world coord]
  (first (filter #(= coord (:location %))
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

