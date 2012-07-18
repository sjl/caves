(ns caves.entities.aspects.receiver
  (:use [caves.entities.core :only [defaspect]]))


(defaspect Receiver
  (receive-message [this message world]
    (update-in world [:entities (:id this) :messages] conj message)))


(defn send-message [entity message args world]
  (if (satisfies? Receiver entity)
    (receive-message entity (apply format message args) world)
    world))

