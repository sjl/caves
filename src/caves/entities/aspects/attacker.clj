(ns caves.entities.aspects.attacker
  (:use [caves.entities.aspects.destructible :only [Destructible take-damage
                                                    defense-value]]
        [caves.entities.core :only [defaspect]]))


(defaspect Attacker
  (attack [this world target]
    {:pre [(satisfies? Destructible target)]}
    (let [damage (inc (rand-int (max 0 (- (attack-value this world)
                                          (defense-value target world)))))]
      (take-damage target world damage)))
  (attack-value [this world]
    (get this :attack 1)))

