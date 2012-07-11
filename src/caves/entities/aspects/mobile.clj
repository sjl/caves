(ns caves.entities.aspects.mobile)


(defprotocol Mobile
  (move [this world dx dy]
        "Move this entity to a new location.")
  (can-move? [this world dx dy]
             "Return whether the entity can move to the new location."))

