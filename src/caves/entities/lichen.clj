(ns caves.entities.lichen
  (:use [caves.entities.core :only [Entity get-id add-aspect]]
        [caves.entities.aspects.destructible :only [Destructible take-damage]]
        [caves.world :only [find-empty-neighbor]]))


(defrecord Lichen [id glyph color location hp])

(defn make-lichen [location]
  (->Lichen (get-id) "F" :green location 1))

(defn should-grow []
  (< (rand) (/ 1 500)))

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

(add-aspect Lichen Destructible)
