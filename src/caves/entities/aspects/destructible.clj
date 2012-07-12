(ns caves.entities.aspects.destructible)


(defprotocol Destructible
  (take-damage [this world damage]
               "Take the given amount of damage and update the world appropriately."))
