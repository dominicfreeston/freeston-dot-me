{:title “The Wallslider has left Heroku”
 :layout :post
 :tags [“clojure” “web” “api” “database” “games” “the wallslider”]}

Like a lot of people recently, I've moved away from Heroku. Not because they're [removing the free tier](https://blog.heroku.com/next-chapter), but because I was tired of spending $16 a month just to run the leaderboard of [my seldom played game](https://thewallslider.com).

The old stack: a [Clojure](https://clojure.org/) app that connects to a [Postgres](https://www.postgresql.org/) database, all hosted on Heroku. It was very easy and allowed me to ship it quickly at the time, but since the game hasn't exactly sold in huge numbers, I was making an ever increasing loss[^loss] on keeping the leaderboard running for the handful of people that still play it. 

[^loss]: I'm still spending money on the domain and my Apple developer account. The latter is a fixed cost no matter how many apps I have, but currently The Wallslider is the only app I have, and I'm considering releasing a new version that points to app.freeston.me instead so I can eventually drop the domain altogether.

But I still love my little game and I love those few players! So my main goals were:

- free, or at least no additional costs
- very little extra maintenance burden once the migration was done

After exploring a few options[^flyio], I settled on the cutting edge approach of just using an existing server!

[^flyio]: Including [fly.io](https://fly.io), which seems very cool and employs [Ben Johnson](https://twitter.com/benbjohnson) who created Litestream.

I'm already running a small VPS on [Digital Ocean](https://www.digitalocean.com/) that serves another Clojure app intended to host various small personal projects. That project's codebase is set up using [polylith](https://polylith.gitbook.io/polylith/), which lends itself quite well to my needs here. I created a new [component](https://polylith.gitbook.io/polylith/architecture/2.3.-component) for all The Wallslider's server code and then added the few required endpoint to my app's [base](https://polylith.gitbook.io/polylith/architecture/2.2.-base). That took care of the code.

While I've gotten much more comfortable with databases the last few years, it doesn't mean I want the hassle of managing a Postgres cluster. But I also don't want to pay someone to manage one for me. 

Enter [SQLite](https://www.sqlite.org/)! As I'm running a single instance of my app on a dedicate (virtual) server, SQLite is more than enough for my needs and the data is now just a file that sits on my server. There was a little bit of SQL tweaking required but it mostly "just worked". The main downside of SQLite is the risk of data loss if something happens to that file or the server it's hosted on.

Enter [Litestream](https://litestream.io/)! This is designed exactly for my kind of scenario, small to medium apps that want to safeguard their data but keep the simplicity of using SQLite. It does so by streaming changes to an S3 compatible bucket, which is generally pretty cheap. But better than cheap is free.

Enter [Cloudflare R2](https://www.cloudflare.com/products/r2/)! They have a generous free tier which is more than enough for my tiny app and its tiny database.

Now I have all the tools I need and a new stack: A Digitial Ocean droplet running my Clojure app (fronted by nginx, which redirects various domains to the right endpoints), SQLite and Litestream and an R2 bucket to safeguard the data.

The total cost is about 7$/month for the droplet, which I was already spending and can continue adding other small projects to it. If any project ever takes off and needs to scale, polylith should make it straightforward to package the right components into a different app and ship it separately. Overall I'm pretty happy with this new setup.

