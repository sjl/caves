(ns caves.entities.lichen
  (:use [caves.entities.core :only [Entity get-id]]
        [caves.world :only [find-empty-neighbor]]))


(defrecord Lichen [id glyph color location])

(defn make-lichen [location]
  (->Lichen (get-id) "F" :green location))

(defn should-grow []
  (< (rand) 0.01))

(defn grow [lichen world]
  (if-let [target (find-empty-neighbor world (:location lichen))]
    (let [new-lichen (make-lichen target)]
      (assoc-in world [:entities (:id new-lichen)] new-lichen))
    world))

(extend-type Lichen Entity
  (tick [this world]
    (if (should-grow)
      (grow this world)
      world)))



