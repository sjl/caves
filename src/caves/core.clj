(ns caves.core
  (:require [lanterna.screen :as s]))


(defn main [screen-type]
  (let [screen (s/get-screen screen-type)]
    (s/in-screen screen
                 (s/put-string screen 0 0 "Welcome to the Caves of Clojure!")
                 (s/put-string screen 0 1 "Press any key to exit...")
                 (s/redraw screen)
                 (s/get-key-blocking screen))))


(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                      (args ":swing") :swing
                      (args ":text")  :text
                      :else           :auto)]
    (main screen-type)))

(comment
  (main :swing))
