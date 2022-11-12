
(ns freeston.server
  (:require [freeston.core :refer [public-dir]]
            [babashka.fs :as fs]
            [hiccup.core :refer [html]]
            [org.httpkit.server :as s]
            [clojure.string :as str]))

(defn content-type [path]
  (if (fs/directory? path)
    "text/html"
    (let [ext (fs/extension path)]
      (condp some [ext]
        #{"html"} "text/html"
        #{"js"} "text/javascript"
        #{"css"} "text/css"
        #{"jpg" "jpeg" "png"} (str "image/" ext)
        #{"woff" "woff2" "ttf" "otf"} (str "font/" ext)
        #{"xml"} "application/xml"
        "text/html"))))

(defn dir-to-uri [dir]
  (str/replace-first (str dir) public-dir ""))

(defn dir-link [dir]
  (let [uri (dir-to-uri dir)]
    [:li [:a {:href uri} (fs/file-name uri)]]))

(defn dir-page [dir]
  (html
   [:html
    [:h1 (dir-to-uri dir)]
    (into [:ul] (mapv dir-link (sort (fs/list-dir dir))))]
   ))

(defn app [req]
  (println "serving: "(:uri req))
  (let [uri (fs/path public-dir (str/replace-first (:uri req) "/" ""))
        uri (if (fs/exists? (fs/path uri "index.html"))
              (fs/path uri "index.html")
              uri)
        body (cond
               (fs/directory? uri)
               (dir-page uri)

               (fs/exists? uri)
               (fs/read-all-bytes uri)
               
               :else
               (fs/read-all-bytes (fs/path public-dir "404.html")))]
    
    {:status  200
     :headers {"Content-Type" (content-type uri)}
     :body body}))

(defonce server (atom  nil))

(defn start! [& [port & args]]
  (let [port (or port 8080)]
    (println (str "starting server on http://localhost:" port))
    (reset! server (s/run-server #'app {:port port})))
  @(promise))

