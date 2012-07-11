(ns caves.entities.core)


(defprotocol Entity
  (tick [this world]
        "Update the world to handle the passing of a tick for this entity."))

