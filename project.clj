(defproject caves "0.1.0-SNAPSHOT"
  :description "The Caves of Clojure"
  :url "http://stevelosh.com/blog/2012/07/caves-of-clojure-01/"
  :license {:name "MIT/X11"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clojure-lanterna "0.9.2"]
                ; [com.googlecode.lanterna/lanterna "2.0.1-SNAPSHOT"]
                 ]

  ; :repositories {"sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"}
  :main ^{:skip-aot true} caves.core

  )
