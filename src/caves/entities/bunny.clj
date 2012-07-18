(ns caves.entities.bunny
  (:use [caves.entities.core :only [Entity get-id add-aspect]]
        [caves.entities.aspects.destructible :only [Destructible]]
        [caves.entities.aspects.mobile :only [Mobile move]]
        [caves.world :only [find-empty-neighbor]]))


(defrecord Bunny [id glyph color location hp max-hp])

(defn make-bunny [location]
  (map->Bunny {:id (get-id)
               :glyph "v"
               :color :yellow
               :location location
               :hp 4
               :max-hp 4}))


(extend-type Bunny Entity
  (tick [this world]
    (if-let [target (find-empty-neighbor world (:location this))]
      (move this target world)
      world)))

(add-aspect Bunny Mobile)
(add-aspect Bunny Destructible)
