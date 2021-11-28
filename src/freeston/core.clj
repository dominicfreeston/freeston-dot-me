#!/usr/bin/env bb

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
  (if-let [[_ year month day name] (re-matches #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md" (fs/file-name file))]
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

(defn build-tags [posts]
  (let [tags (reduce (fn [tags post]
                       (into tags (:tags post)))
                     #{}
                     posts)]
    (map
     #(build-tag % (map thin-post posts))
     (sort tags))))

(defn replace-tags
  "Replace string tags with map tags inside posts"
  [posts tags]
  (let [tags (into {} (map (fn [t] [(:name t) t]) tags))]
    (map (fn [p]
           (update p :tags (fn [ts]
                             (map #(get tags %) ts))))
         posts)))

;; Combine it all

(defn gather-data [dir]
  (let [posts (parse-posts dir)
        tags (build-tags posts)]
    {:posts (replace-tags posts tags)
     :tags tags
     :archives (group-by-month posts)}))


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
    (println "rendering:" (:name resource) " -> " (str file))
    (io/make-parents file)
    (spit file (html (template resource)))))

(defn render-resources [resources template]
  (run! #(render-resource % template) resources))

(defn -main [& _args]
  (comment (println (-> (parse-posts posts-src-path)
                   second
                   :body)))

  (comment (println
       (:tags (gather-data posts-src-path))))
  
  (do
    (println "Generating site...")
    (let [data (gather-data posts-src-path)]
      (when (not (fs/exists? public-dir))
        (fs/create-dir public-dir))
      (copy-asset-dirs)

      (println "\n Rendering Feed")
      (spit (fs/file (fs/path public-dir "feed.xml")) (feed/rss data))

      (println "\n Rendering Top Level")
      (render-resource (assoc data :name "home" :uri "")
                       template/home)
      (render-resource (assoc data :name "tags" :uri "tags")
                       template/tags)
      (render-resource (assoc data :name "archives" :uri "archives")
                       template/archives)
      (spit (fs/file (fs/path public-dir "404.html")) (html template/not-found))
      (println "\nRendering Posts")
      (render-resources (:posts data) template/post)
      (println "\nRendering Tags")
      (render-resources (:tags data) template/tag)
      )))
