(ns caves.entities.aspects.digger
  (:use [caves.entities.core :only [defaspect]]
        [caves.world :only [check-tile set-tile-floor]]))


(defaspect Digger
  (dig [this world dest]
    {:pre [(can-dig? this world dest)]}
    (set-tile-floor world dest))
  (can-dig? [this world dest]
    (check-tile world dest #{:wall})))
