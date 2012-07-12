(ns caves.entities.aspects.attacker)


(defprotocol Attacker
  (attack [this world target]
          "Attack the target."))

