(ns caves.entities.lichen
  (:use [caves.entities.core :only [Entity get-id]]
        [caves.entities.aspects.destructible :only [Destructible take-damage]]
        [caves.world :only [find-empty-neighbor]]))


(defrecord Lichen [id glyph color location hp])

(defn make-lichen [location]
  (->Lichen (get-id) "F" :green location 1))

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

(extend-type Lichen Destructible
  (take-damage [{:keys [id] :as this} world damage]
    (let [damaged-this (update-in this [:hp] - damage)]
      (if-not (pos? (:hp damaged-this))
        (update-in world [:entities] dissoc id)
        (update-in world [:entities id] assoc damaged-this)))))
