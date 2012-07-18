(ns caves.entities.silverfish
  (:use [caves.entities.core :only [Entity get-id add-aspect]]
        [caves.entities.aspects.destructible :only [Destructible]]
        [caves.entities.aspects.mobile :only [Mobile move can-move?]]
        [caves.world :only [get-entity-at get-tile-kind]]
        [caves.coords :only [neighbors]]))


(defrecord Silverfish [id glyph color location hp max-hp])

(defn make-silverfish [location]
  (map->Silverfish {:id (get-id)
                    :glyph "~"
                    :color :white
                    :location location
                    :hp 15
                    :max-hp 15}))


(extend-type Silverfish Entity
  (tick [this world]
    (let [target (rand-nth (neighbors (:location this)))]
      (if (can-move? this target world)
        (move this target world)
        world))))

(add-aspect Silverfish Mobile
  (can-move? [this dest world]
    (and (#{:floor :wall} (get-tile-kind world dest))
         (not (get-entity-at world dest)))))

(add-aspect Silverfish Destructible)
