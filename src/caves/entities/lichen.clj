(ns caves.entities.lichen
  (:use [caves.entities.core :only [Entity get-id add-aspect]]
        [caves.entities.aspects.receiver :only [send-message-nearby]]
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

(defn grow [{:keys [location]} world]
  (if-let [target (find-empty-neighbor world location)]
    (let [new-lichen (make-lichen target)
          world (assoc-in world [:entities (:id new-lichen)] new-lichen)
          world (send-message-nearby location "The lichen grows." world)]
      world)
    world))


(extend-type Lichen Entity
  (tick [this world]
    (if (should-grow)
      (grow this world)
      world)))

(add-aspect Lichen Destructible)
