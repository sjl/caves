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


(defn make-fnmap [fns]
  (into {} (for [[label fntail] (map (juxt first rest) fns)]
             [(keyword label)
              `(fn ~@fntail)])))

(defn make-fnheads [fns]
  (map #(take 2 %) fns))


(defmacro defaspect [label & fns]
  (let [fnmap (make-fnmap fns)
        fnheads (make-fnheads fns)]
    `(do
       (defprotocol ~label
         ~@fnheads)
       (def ~label
         (with-meta ~label {:defaults ~fnmap})))))

(defmacro add-aspect [entity aspect & fns]
  (let [fnmap (make-fnmap fns)]
    `(extend ~entity ~aspect (merge (:defaults (meta ~aspect))
                                    ~fnmap))))


(comment

  (macroexpand-1
    '(defaspect Mobile
                (move [world target]
                      (when (can-move? world target)
                        (println world)))
                (can-move? [world target]
                           true)))

  (macroexpand-1
    '(add-aspect Player Mobile))

  (macroexpand-1
    '(add-aspect EE Mobile
                 (can-move? [world target]
                            true)))


  )
