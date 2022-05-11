#!/usr/bin/env bb

(ns freeston.watch
  (:require
   [freeston.server :as server]))

(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/fswatcher "0.0.3")

(require '[pod.babashka.fswatcher :as fw])

(def build-op (atom nil))
(defn trigger-reload [_]
  (let [[old _] (reset-vals! build-op
            (future
              (Thread/sleep 20)
              (use 'freeston.core :reload-all)
              (freeston.core/-main)))]
    (when old (future-cancel old))))

(defn start! []
  (freeston.core/-main)
  (fw/watch "src"
            trigger-reload
            {:delay-ms 0 :recursive true})
  (fw/watch "content"
            trigger-reload
            {:delay-ms 0 :recursive true})
  (fw/watch "static"
            trigger-reload
            {:delay-ms 0 :recursive true})
  (server/start!))
