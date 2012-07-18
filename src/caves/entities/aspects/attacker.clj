(ns caves.entities.aspects.attacker
  (:use [caves.entities.aspects.destructible :only [Destructible take-damage
                                                    defense-value]]
        [caves.entities.core :only [defaspect]]))


(declare get-damage)

(defaspect Attacker
  (attack [this target world]
    {:pre [(satisfies? Destructible target)]}
    (take-damage target (get-damage this target world) world))
  (attack-value [this world]
    (get this :attack 1)))


(defn get-damage [attacker target world]
  (let [attack (attack-value attacker world)
        defense (defense-value target world)
        max-damage (max 0 (- attack defense))
        damage (inc (rand-int max-damage))]
    damage))
