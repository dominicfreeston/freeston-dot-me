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
