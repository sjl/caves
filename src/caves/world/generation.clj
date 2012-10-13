(ns caves.world.generation
  (:use [clojure.set :only (union difference)]
        [caves.entities.player :only [make-player]]
        [caves.entities.lichen :only [make-lichen]]
        [caves.entities.bunny :only [make-bunny]]
        [caves.entities.silverfish :only [make-silverfish]]
        [caves.world.core :only [tiles get-tile-from-tiles random-coordinate
                                 world-size ->World tile-walkable?
                                 find-empty-tile]]
        [caves.coords :only [neighbors]]))


; Convenience -----------------------------------------------------------------
(def all-coords
  (let [[cols rows] world-size]
    (for [x (range cols)
          y (range rows)]
      [x y])))

(defn get-tile-from-level [level [x y]]
  (get-in level [y x] (:bound tiles)))


; Region Mapping --------------------------------------------------------------
(defn filter-walkable
  "Filter the given coordinates to include only walkable ones."
  [level coords]
  (set (filter #(tile-walkable? (get-tile-from-level level %))
               coords)))


(defn walkable-neighbors
  "Return the neighboring coordinates walkable from the given coord."
  [level coord]
  (filter-walkable level (neighbors coord)))

(defn walkable-from
  "Return all coordinates walkable from the given coord (including itself)."
  [level coord]
  (loop [walked #{}
         to-walk #{coord}]
    (if (empty? to-walk)
      walked
      (let [current (first to-walk)
            walked (conj walked current)
            to-walk (disj to-walk current)
            candidates (walkable-neighbors level current)
            to-walk (union to-walk (difference candidates walked))]
        (recur walked to-walk)))))


(defn get-region-map
  "Get a region map for the given level.

  A region map is a map of coordinates to region numbers.  Unwalkable
  coordinates will not be included in the map.  For example, the map:

  .#.
  ##.

  Would have a region map of:

    x y  region
  {[0 0] 0
   [2 0] 1
   [2 1] 1}

  "
  [level]
  (loop [remaining (filter-walkable level all-coords)
         region-map {}
         n 0]
    (if (empty? remaining)
      region-map
      (let [next-coord (first remaining)
            next-region-coords (walkable-from level next-coord)]
        (recur (difference remaining next-region-coords)
               (into region-map (map vector
                                     next-region-coords
                                     (repeat n)))
               (inc n))))))


; Random World Generation -----------------------------------------------------
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


; Creatures -------------------------------------------------------------------
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


; Actual World Creation -------------------------------------------------------
(defn random-world []
  (let [world (->World (random-tiles) {})
        world (nth (iterate smooth-world world) 3)
        world (populate-world world)]
    (assoc world :regions (get-region-map (:tiles world)))))
