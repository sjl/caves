(ns caves.ui.drawing
  (:use [caves.utils :only (map2d shear)])
  (:require [lanterna.screen :as s]))


; Definitions -----------------------------------------------------------------
(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))


; Start -----------------------------------------------------------------------
(defmethod draw-ui :start [ui game screen]
  (s/put-sheet screen 0 0
               ["Welcome to the Caves of Clojure!"
                ""
                "Press any key to continue."]))


; Win -------------------------------------------------------------------------
(defmethod draw-ui :win [ui game screen]
  (s/put-sheet screen 0 0
               ["Congratulations, you win!"
                "Press escape to exit, anything else to restart."]))


; Lose ------------------------------------------------------------------------
(defmethod draw-ui :lose [ui game screen]
  (s/put-sheet screen 0 0
               ["Sorry, better luck next time."
                "Press escape to exit, anything else to restart."]))


; Play ------------------------------------------------------------------------
(defn get-viewport-coords
  "Find the top-left coordinates of the viewport in the overall map, centering on the player."
  [game player-location vcols vrows]
  (let [[center-x center-y] player-location

        tiles (:tiles (:world game))

        map-rows (count tiles)
        map-cols (count (first tiles))

        start-x (- center-x (int (/ vcols 2)))
        start-x (max 0 start-x)

        start-y (- center-y (int (/ vrows 2)))
        start-y (max 0 start-y)

        end-x (+ start-x vcols)
        end-x (min end-x map-cols)

        end-y (+ start-y vrows)
        end-y (min end-y map-rows)

        start-x (- end-x vcols)
        start-y (- end-y vrows)]
    [start-x start-y]))

(defn get-viewport-coords-of
  "Get the viewport coordiates for the given real coords, given the viewport start coords."
  [start-x start-y coords]
  (let [[cx cy] coords]
    [(- cx start-x) (- cy start-y)]))


(defn draw-hud [screen game start-x start-y]
  (let [hud-row (dec (second (s/get-size screen)))
        [x y] (get-in game [:world :entities :player :location])
        info (str "loc: [" x "-" y "]")
        info (str info " start: [" start-x "-" start-y "]")]
    (s/put-string screen 0 hud-row info)))


(defn draw-entity [screen start-x start-y {:keys [location glyph color]}]
  (let [[x y] (get-viewport-coords-of start-x start-y location)]
    (s/put-string screen x y glyph {:fg color})))


(defn draw-world [screen vrows vcols start-x start-y tiles]
  (letfn [(render-tile [tile]
            [(:glyph tile) {:fg (:color tile)}])]
    (let [tiles (shear tiles start-x start-y vcols vrows)
          sheet (map2d render-tile tiles)]
      (s/put-sheet screen 0 0 sheet))))


(defn highlight-player [screen start-x start-y player]
  (let [[x y] (get-viewport-coords-of start-x start-y (:location player))]
    (s/move-cursor screen x y)))


(defmethod draw-ui :play [ui game screen]
  (let [world (:world game)
        {:keys [tiles entities]} world
        player (:player entities)
        [cols rows] (s/get-size screen)
        vcols cols
        vrows (dec rows)
        [start-x start-y] (get-viewport-coords game (:location player) vcols vrows)]
    (draw-world screen vrows vcols start-x start-y tiles)
    (doseq [entity (vals entities)]
      (draw-entity screen start-x start-y entity))
    (draw-hud screen game start-x start-y)
    (highlight-player screen start-x start-y player)))


; Entire Game -----------------------------------------------------------------
(defn draw-game [game screen]
  (s/clear screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen))
