;; Copyright (c) 2022 Dominic Freeston

;; Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

;; The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(ns freeston.feed
  (:require [clojure.data.xml :as xml]))

(def domain "https://freeston.me/")

(defn abs-url [resource]
  (str domain (:uri resource)))

(defn pub-date [resource]
  (.format
   (:date resource)
   (java.time.format.DateTimeFormatter/ofPattern
    "E, dd MMM yyyy 00:00:00 +0000")))

(defn build-date []
  (.format
   (java.time.LocalDateTime/now)
   (java.time.format.DateTimeFormatter/ofPattern
    "E, dd MMM yyyy hh:mm:ss +0000")))

(defn html-body [post]
  (str 
   (if-let [img (:image post)]
     (str "<p><img src=\"" img  "\"></p>"))
   (:html-body post)))

(defn- rss-post [post]
  [:item
   [:pubDate (pub-date post)]
   [:guid (abs-url post)]
   [:link (abs-url post)]
   [:title (:title post)]
   [:description [:-cdata (html-body post)]]])

(defn- channel [data]
  [:channel
   [:atom:link {:href (str domain "feed.xml") :rel "self" :type "application/rss+xml"}]
   [:title "freeston.me"]
   [:link domain]
   [:description "Another tech blog, probably"]
   [:lastBuildDate (build-date)]
   (map rss-post (:posts data))
   ])

(defn rss [data]
  (xml/indent-str
   (xml/sexp-as-element
    [:rss {:version "2.0"
           :xmlns:atom "http://www.w3.org/2005/Atom"
           :xmlns:content"http://purl.org/rss/1.0/modules/content/"}
     (channel data)])))
