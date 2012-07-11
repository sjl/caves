(ns caves.entities.aspects.digger)

(defprotocol Digger
  (dig [this world dx dy]
       "Dig a location.")
  (can-dig? [this world dx dy]
            "Return whether the entity can dig the new location."))
