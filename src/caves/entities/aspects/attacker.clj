(ns caves.entities.aspects.attacker
  (:use [caves.entities.core :only [defaspect]]))


(defprotocol Attacker
  (attack [this world target]
          "Attack the target."))

