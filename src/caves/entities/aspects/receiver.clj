(ns caves.entities.aspects.receiver
  (:use [caves.entities.core :only [defaspect]]
        [caves.world.core :only [get-entities-around]]))


(defaspect Receiver
  (receive-message [this message world]
    (update-in world [:entities (:id this) :messages] conj message)))


(defn send-message [entity message args world]
  (if (satisfies? Receiver entity)
    (receive-message entity (apply format message args) world)
    world))

(defn send-message-nearby [coord message world]
  (let [entities (get-entities-around world coord 7)
        sm (fn [world entity]
             (send-message entity message [] world))]
    (reduce sm world entities)))


