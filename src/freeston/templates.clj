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
    [:li [:a {:href "/archives"} "Archives"]]
    [:li [:a {:href "/tags"} "Tags"]]
    [:li [:a {:href "/feed.xml"} "RSS"]]]])

(defn base [h-seq body]
  [:html
   {:lang "en"} 
   (head h-seq)
   [:body 
    [:div [:a {:href "/" :class "freeston-title title-link"} "freeston.me"]]
    top-nav

    body
    
    [:footer "Copyright © 2021 Dominic Freeston"] 
    [:script {:src "/js/highlight.pack.js" :type "application/javascript"}] 
    [:script "hljs.initHighlightingOnLoad();"]]])

(def not-found
  (base
   []
   [:h1 [:a.title-link {:href "https://http.cat/404"} "404 - Page not found"]]))

(defn home [data]
  (base
   []
   [:div
    [:h1 "Projects"]
    [:div.projects
     
     [:div.project 
      [:h2 [:a (site-link {:uri "tags/thirty-five"} "Thirty-Five")]]
      [:p "A thirty-five day challenge to produce one piece of generative art a day."]]
     
     [:div.project
      [:h2 [:a {:href "https://www.thewallslider.com/"} "The Wallslider"]]
      [:p "An endless retro wall-jumping game for iOS and Android."]]]
    
    [:h1 (str "Recent Posts")]
    [:ul
     (map (fn [post]
            [:li (site-link post :title)])
          (take 5 (remove #(some #{"thirty-five"} (map :name (:tags %))) (:posts data))))]
    (site-link {:uri "archives"} "More posts")

    [:h1 "About"]
    [:p "Welcome to my little corner of the internet. I'm an iOS developer by day and a sometimes-generative-artist, sometimes-indie-game-dev and general code tinkerer and procrastinator by night. The main tools I toy around with these days are " [:a {:href "https://clojure.org/"} "Clojure"] " and the " [:a {:href "https://godotengine.org/"} "Godot engine."]]]))

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
     [:h1 "Archives"]]
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

(defn post [post]
  (base
   (list
    [:meta {:property "og:title"
            :content (:title post)}]
    (if-let [img (:image post)]
      [:meta {:property "og:image"
              :content (str "https://freeston.me" img)}]))
   [:article
    [:div#post
     [:div.post-header
      [:div#post-meta
       [:h1
        (site-link {:class "title-link"} post :title)]
       [:div.byline
        [:span.date (display-date (:date post))]]]]
     
     [:div 
      (if-let [img (:image post)]
        [:p [:img {:src img}]])]
     
     (:html-body post)
     
     [:div.post-tags
      [:ul.hlist
       (if-let [prev (:prev post)]
         [:li (site-link prev "previous")])
       (if-let [next (:next post)]
         [:li (site-link next "next")])]
      [:br]
      [:b "Tags: "]
      [:ul.hlist
       (map (fn [tag] [:li (site-link tag :name)]) (:tags post))]]]]))

