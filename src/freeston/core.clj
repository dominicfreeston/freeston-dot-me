#!/usr/bin/env bb

;; Copyright (c) 2022 Dominic Freeston

;; Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

;; The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(ns freeston.core
  (:require [freeston.templates :as template]
            [freeston.feed :as feed]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [babashka.fs :as fs] ;; Can use fs on the jvm
            [hiccup.core :as hiccup]
            [markdown.core :refer [md-to-html-string]]))

(def content-dir "content")
(def public-dir "public")

;; Parsing
;;;;;;;;;;

(def posts-dir "posts")
(def posts-src-path (fs/path content-dir posts-dir))

(defn thin-post [post]
  (select-keys post [:title :date :uri :tags]))

;; Posts

(defn parse-post
  "Parses the post into a map.
  The map contains the metadata included at the top of the post,
  a html :body, and other useful bits of extracted data
  
  file can be a string, a path or a file "
  
  [file]
  (when-let [[_ year month day name] (re-matches #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md" (fs/file-name file))]
    (with-open [rdr (java.io.PushbackReader. (io/reader (fs/file file)))]
      (let [[_ uri] (re-matches #"(.*)\.md" (fs/file-name file))
            meta (edn/read rdr)
            raw-body (slurp rdr)
            html-body (md-to-html-string raw-body :footnotes? true)]
        (into {:uri (fs/path posts-dir uri)
               :date (java.time.LocalDate/of
                      (Integer/parseInt year)
                      (Integer/parseInt month)
                      (Integer/parseInt day))
               :name name
               :raw-body raw-body
               :html-body html-body
               :tags []}
              meta)))))

(defn add-prev-next
  "Adds a :prev and :next key to post data containing the metadata of the prev/next post if it exists"
  [pages]
  (map (fn [[prev target next]]
         (assoc target
                :prev (when prev (thin-post prev))
                :next (when next (thin-post next))))
       (partition 3 1 (flatten [nil pages nil]))))

(defn parse-posts [dir]
  (->> (fs/list-dir dir)
                   sort
                   (map parse-post)
                   add-prev-next
                   reverse))

(defn group-by-month [posts]
  (sort
   #(compare %2 %1)
   (group-by (fn [post]
               (java.time.YearMonth/from (:date post)))
             posts)))

;; Tags

(defn build-tag [tag posts]
  {:name tag
   :uri (fs/path "tags" (str/replace tag #"\s" "-"))
   :posts (filter #(some #{tag} (:tags %)) posts)})

(defn get-all-tags [posts]
  (reduce (fn [tags post]
            (into tags (:tags post)))
          #{}
          posts))

(defn build-tags [posts]
  (map
   #(build-tag % (map thin-post posts))
   (sort (get-all-tags posts))))

(defn replace-tags
  "Replace string tags with map tags inside posts"
  [posts tags]
  (let [tags (into {} (map (fn [t] [(:name t) t]) tags))]
    (map (fn [p]
           (update p :tags (fn [ts]
                             (map #(get tags %) ts))))
         posts)))

;; CV
(defn parse-md-page [props]
  (with-open [rdr (-> props
                      :source
                      fs/file
                      io/reader
                      java.io.PushbackReader.)]
    (let [raw-body (slurp rdr)
          html-body (md-to-html-string raw-body :footnotes? true)]
      (assoc props
             :raw-body raw-body
             :html-body html-body))))

;; Combine it all

(def pages
  [{:source "content/cv.md"
    :uri "cv"
    :name "CV"
    :class "cv"}
   {:source "content/svg.md"
    :uri "svg"
    :name "SVG"}])

(defn gather-data [dir]
  (let [posts (parse-posts dir)
        tags (build-tags posts)]
    {:posts (replace-tags posts tags)
     :tags tags
     :archives (group-by-month posts)
     :pages (map parse-md-page pages)}))

;; Static Assets
;;;;;;;;;;;;;;;;

(defn copy-dir [dir]
  (println "Copying directory:" (fs/file-name dir))
  (fs/copy-tree dir
                (fs/path public-dir (fs/file-name dir))
                {:replace-existing true}))

(defn copy-asset-dirs []
  (copy-dir (fs/path content-dir "img"))
  (doseq [dir (fs/list-dir "static")]
    (copy-dir dir)))

;; Rendering
;;;;;;;;;;;;

(defn html [h]
  (hiccup/html {:mode :html}
               "<!DOCTYPE html>"
               h))

(defn render-resource [resource template]
  (let [file (fs/file (fs/path public-dir (:uri resource) "index.html"))]
    (println "rendering:" (or (:name resource) (:uri resource)) " -> " (str file))
    (io/make-parents file)
    (spit file (html (template resource)))))

(defn render-resources [resources template]
  (run! #(render-resource % template) resources))

(defn -main [& _args]
  (println "Generating site...")
  (let [data (gather-data posts-src-path)]
    (when (not (fs/exists? public-dir))
      (fs/create-dir public-dir))
    (copy-asset-dirs)

    (spit (fs/file (fs/path public-dir "404.html")) (html template/not-found))

    (println "\nRendering Feed")
    (spit (fs/file (fs/path public-dir "feed.xml")) (feed/rss data))

    (println "\nRendering Top Level")
    (render-resource (assoc data :name "home" :uri "")
                     template/home)
    (render-resource (assoc data :uri "posts")
                     template/archives)
    (render-resource (assoc data :uri "tags")
                     template/tags)

    (println "\nRendering Custom Pages")
    (render-resource (assoc data :uri "thirty-five")
                     template/thirty-five)
    
    (println "\nRendering Pages")
    (render-resources (:pages data) template/md-page)
    (println "\nRendering Posts")
    (render-resources (:posts data) template/post)
    (println "\nRendering Tags")
    (render-resources (:tags data) template/tag)
    ))
