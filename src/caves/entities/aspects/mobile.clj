(ns caves.entities.aspects.mobile)


(defprotocol Mobile
  (move [this world dest]
        "Move this entity to a new location.")
  (can-move? [this world dest]
             "Return whether the entity can move to the new location."))

