Ideas
=====

Remove the distinction between items and creatures.  ALL THING ARE ENTITY.

    (defprocotol Entity
      (tick [this game])
      (get-location [this game?])
      (get-id [this]))

    (def Entity
      (with-meta Entity :default-fns
        {:tick         (fn [this game] game)
         :get-location (fn [this game] (:location this))
         :get-id       (fn [this] (:id this))}))


Individual things will implement the protocol:

    (defrecord Pixie [id location])

    (extend-entity Pixie
      (tick [this game]
         (do-stuff game)))

    (extend Pixie Entity
      (merge {:tick (fn [this game] game)
              :get-location (fn [this game] (:location this))
              :get-id (fn [this] (:id this))}
             {:tick (fn [this game]
                      (do-stuff game))}))

    (defmacro extend-entity [cls & fns]
      (let [fnmap (into {}
                    (for [[fn-name & fn-tail] fns]
                      [(keyword fn-name) fn-tail]))]
        `(extend ~cls Entity
          (merge default-entity-fns
                 ~fnmap))))

The Entity protocol should be very sparse.  Most functionality will come from
other protocols that can be "added in" on a case-by-case basis.  Like mixins,
but without the monkeypatching clusterfuck.

Sample Entities
---------------

A Fungus might look like:

    (extend' Fungus Destructible ...)
    (extend' Fungus Metabolizer ...)

Funguses don't do much.  They do draw nutrition from the soil though, so their
nutritional value should grow over time until they split.

Goblins:

    (extend' Goblin Mobile ...)
    (extend' Goblin Destructible ...)
    (extend' Goblin Attacker ...)
    (extend' Goblin AI ...)
    (extend' Goblin Container ...)
    (extend' Goblin Wearer ...)
    (extend' Goblin Wielder ...)
    (extend' Goblin Eater ...)
    (extend' Goblin Metabolizer ...)

Goblines are mobs that can walk around, be killed, kill things, have an
inventory, wear armor and wield weapons, eat food, and burn their eaten food.

Pixies:

    (extend' Pixie Mobile ...)
    (extend' Pixie Destructible ...)
    (extend' Pixie Attacker ...)
    (extend' Pixie AI ...)

    (extend' Pixie Item ...)
    (extend' Pixie Applicable ...)
    (extend' Pixie Edible ...)

Here Pixies are entities that move around, can be destroyed (i.e. killed), can
attack other entities, have an AI to decide actions (put this in tick?).  Sounds
like a normal monster, but they can also function as an item if you pick them
up.  They can then be applied or eaten.

Ideas for Entity Protocols
--------------------------

### Entity

Base protocol.  All entities must implement this.

Basically it's implementation-related stuff that's not gameplay-related.

Exception: materials?

### Mobile

Can move autonomously.

### Item

Can be placed in a container.

### Armor

Can be worn as armor by wearers.

### Weapon

Can be used as a weapon by wielders.

### Container

Can hold things that implement Item.  Creatures can be Containers -- this
effectively means they have an inventory.

### Food

Can be eaten by Eaters (maybe).

### Quaffable

Can be drunk by drinkers.

### Eater

Can eat Food (maybe).

### Drinker

Can drink Quaffables (maybe).

### Metabolizer

Has a notion of "energy" that either decreases over time (hunger) or increases
over time (photosynthesis).

### Destructible

Can be destroyed by physical (or magical) trauma.  Usually handled through HP
counts.

### Decaying

Can transform to something else (or nothing) over a period of time.

### Attacker

Can physically attack other creatures.

### Wielder

Can use other entities as weapons if they implement Weapon.

### Wearer

Can use other entities as armor if they implement Armor.

### Sentient

Has an alignment.  Not sure about this one.
