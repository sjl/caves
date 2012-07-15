(ns caves.entities.lichen
  (:use [caves.entities.core :only [Entity get-id add-aspect]]
        [caves.entities.aspects.destructible :only [Destructible]]
        [caves.world :only [find-empty-neighbor]]))


(defrecord Lichen [id glyph color location hp max-hp])

(defn make-lichen [location]
  (map->Lichen {:id (get-id)
                :glyph "F"
                :color :green
                :location location
                :hp 6
                :max-hp 6}))

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
