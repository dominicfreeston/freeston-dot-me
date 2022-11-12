;; Copyright (c) 2022 Dominic Freeston

(ns freeston.templates)

(defn display-date
  ([date pattern]
   (let [p (case pattern
             :default "MMMM d, yyyy"
             :year-month "yyyy MMMM"
             :month-day "MMM dd")
         fmt (java.time.format.DateTimeFormatter/ofPattern p)]
     (.format date fmt)))
  ([date]
   (display-date date :default)))


(defn site-link
  "Output hiccup element that links to a resource on this site.
  Assumes :uri should be treated as path relative from root.
  content can be either a keyword or a hiccup element"
  ([meta resource content]
   [:a
    (into meta {:href (str "/" (:uri resource))})
    (if (keyword? content)
      (get resource content)
      content)])
  ([resource content]
   (site-link {} resource content)))

(defn head [h-seq]
  (into
   [:head 
    [:meta {:charset "utf-8"}] 
    [:link {:rel "icon" :href "/img/favicon.ico"}] 
    [:title "freeston.me"] 
    [:link {:rel "canonical," :href "https://freeston.me/"}] 
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}] 
    [:link {:rel "preload" :as "font" :href "/fonts/FiraSansCondensed-Light.woff2" :type "font/woff2" :crossorigin ""}]
    [:link {:rel "preload" :as "font" :href "/fonts/FiraSansCondensed-Regular.woff2" :type "font/woff2" :crossorigin ""}]
    [:link {:rel "preload" :as "font" :href "/fonts/FiraSansCondensed-SemiBold.woff2" :type "font/woff2" :crossorigin ""}]
    [:link {:href "/css/style.css" :rel "stylesheet" :type "text/css"}]]
   h-seq))

(def top-nav
  [:nav 
   [:ul 
    [:li [:a {:href "/"} "Home"]]
    [:li [:a {:href "/posts"} "Posts"]]
    [:li [:a {:href "/tags"} "Tags"]]
    [:li [:a {:href "/feed.xml"} "RSS"]]]
   
   [:ul
    [:li [:a {:href "https://github.com/dominicfreeston" :rel "me"} "GitHub"]]
    [:li [:a {:href "https://mastodon.gamedev.place/@dominic" :rel "me"} "Mastodon"]]
    [:li [:a {:href "https://twitter.com/dominicfreeston" :rel "me"} "Twitter"]]]])

(defn base [h-seq body]
  [:html
   {:lang "en"} 
   (head h-seq)
   [:body
    [:div.header
     [:div.freeston-group
      [:a {:href "/" :class "freeston-title title-link"} "freeston"]
      [:button#theme-toggle {:type "button"} ""]
      [:a {:href "/" :class "freeston-title title-link"} "me"]]
     top-nav]

    body
    
    [:footer "Copyright © 2022 Dominic Freeston (" [:a {:href "https://github.com/dominicfreeston/freeston-dot-me"} "View Source" ] ")"]
    [:script {:src "/js/theme-toggle.js" :type "application/javascript"}]
    [:script {:src "/js/highlight.pack.js" :type "application/javascript"}] 
    [:script "hljs.initHighlightingOnLoad();"]]])

(def not-found
  (base
   []
   [:h1 [:a.title-link {:href "https://http.cat/404"} "404 - Page not found"]]))

(defn md-page [data]
  (base
   []
   [:div
    (select-keys data [:class])
    (:html-body data)]))

(defn home [data]
  (base
   []
   [:div
    [:h1 "Creations"]
    [:div.projects

     [:a.project {:href "https://app.freeston.me/plottables"}
      [:h2 "Plottables"]
      [:p "Downloadable generative artwork for you to plot at home."]]
     
     [:a.project {:href "/thirty-five"}
      [:h2 "Thirty-Five"]
      [:p "A thirty-five day challenge to produce one piece of generative art a day."]]
     
     [:a.project {:href "https://www.thewallslider.com/"}
      [:h2 "The Wallslider"]
      [:p "An endless retro wall-jumping game for iOS and Android."]]]
    
    [:h1 "Recent Posts"]
    [:ul
     (map (fn [post]
            [:li (site-link post :title)])
          (take 5 (remove #(some #{"thirty-five"} (map :name (:tags %))) (:posts data))))]
    (site-link {:uri "posts"} "More posts")

    [:h1 "About"]
    [:p "Welcome to my little corner of the internet. I'm Dominic, an app developer, generative-artist, indie-game-dev and general code tinkerer. You can find out about my recent personal work on this page and about my professional experience from " [:a {:href "/cv"} "my CV"] "."]
    ]))

(defn tags [data]
  (base
   []
   [:div
    [:div#page-header
     [:h1 "Tags"]]
    [:ul
     (map (fn [tag]
            [:li (site-link tag :name) (str " (" (count (:posts tag)) ")")])
          (:tags data))]]))

(defn tag [tag]
  (base
   []
   [:div
    [:h1 (str "Posts tagged " (:name tag))]
    [:ul
     (map (fn [post]
            [:li
             (display-date (:date post))
             " - "
             (site-link post :title)])
          (:posts tag))]]))

(defn archives [data]
  (base
   []
   [:div
    [:div#page-header
     [:h1 "Posts"]]
    (map (fn [group]
           (list
            [:h4 (display-date (first group) :year-month)]
            [:ul
             (map (fn [post]
                    [:li
                     (display-date (:date post) :month-day)
                     " - "
                     (site-link post :title)])
                  (second group))]))
         (:archives data))]))

;; Thirty-Five

(defn thirty-five-post? [post]
  (some #{"thirty-five"} (map :name (:tags post))))

(defn add-thirty-five-link [post]
  (if (thirty-five-post? post)
    (assoc post :thing [:a {:href "/thirty-five"} "thirty-five"])
    post))

(defn thirty-five [data]
  (base
   []
   (list
    [:h1 "Thirty-Five"]
    [:div.image-grid
     (for [[i post] (map-indexed vector
                                 (->> (:posts data)
                                      (filter thirty-five-post?)
                                      reverse ))]
       (site-link
        post
        [:div
         [:p (str (inc i))]
         [:img {:src (:image post)}]]))]
    
    [:p "I set myself the challenge of creating a piece of generative art a day for 35 days and share them online. The goal was to tackle two of the things I find hold me back in my personal creative endevours: lack of consistency and a reluctance to share things in public."])))

;; Post

(defn post [post]
  (base
   (list
    [:meta {:property "og:title"
            :content (:title post)}]
    (when-let [img (:image post)]
      [:meta {:property "og:image"
              :content (str "https://freeston.me" img)}]))
   [:div
    [:article
     [:div#post
      [:div.post-header
       [:div#post-meta
        [:h1
         (site-link {:class "title-link"} post :title)]
        [:div.post-meta
         [:span.date (display-date (:date post))]
         (when (thirty-five-post? post)
           [:a {:href "/thirty-five"} "Thirty-Five"])]]]
      
      [:div 
       (when-let [img (:image post)]
         [:p [:img {:src img}]])]
      
      (:html-body post)]]

    [:div.post-tags
     [:h2 "Read More"]
     [:ul
      (when-let [prev (:prev post)]
        [:li [:b "Prev: "](site-link prev :title)])
      (when-let [next (:next post)]
        [:li [:b "Next: "] (site-link next :title)])
      [:li [:b "Tags: "]
       [:ul.hlist
        (map (fn [tag] [:li (site-link tag :name)]) (:tags post))]]]]]))


