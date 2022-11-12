#!/usr/bin/env bb

;; Copyright (c) 2022 Dominic Freeston

;; Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

;; The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
