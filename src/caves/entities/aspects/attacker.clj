(ns caves.entities.aspects.attacker
  (:use [caves.entities.aspects.destructible :only [Destructible take-damage]]
        [caves.entities.core :only [defaspect]]))


(defaspect Attacker
  (attack [this world target]
    {:pre [(satisfies? Destructible target)]}
    (let [damage 1]
      (take-damage target world damage))))

