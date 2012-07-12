(ns caves.entities.core)


(def ids (ref 0))

(defprotocol Entity
  (tick [this world]
        "Update the world to handle the passing of a tick for this entity."))


(defn get-id []
  (dosync
    (let [id @ids]
      (alter ids inc)
      id)))
