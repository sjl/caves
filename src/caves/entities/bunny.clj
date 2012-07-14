(ns caves.entities.bunny
  (:use [caves.entities.core :only [Entity get-id add-aspect]]
        [caves.entities.aspects.destructible :only [Destructible]]
        [caves.entities.aspects.mobile :only [Mobile move can-move?]]
        [caves.world :only [find-empty-neighbor]]))


(defrecord Bunny [id glyph color location hp])

(defn make-bunny [location]
  (->Bunny (get-id) "v" :yellow location 1))


(extend-type Bunny Entity
  (tick [this world]
    (if-let [target (find-empty-neighbor world (:location this))]
      (move this world target)
      world)))

(add-aspect Bunny Mobile)
(add-aspect Bunny Destructible)
