{
:title "I wrote myself a static site generator"
:layout :post
:tags ["clojure" "meta"]
}

## The Story

I replaced a [perfectly fine static-site generator](http://cryogenweb.org/) with a custom one. I had been wanting to make some modifications to my site and whilst it was [absolutely possible](http://cryogenweb.org/docs/customizing-cryogen.html) to do so previously, I never felt particularly inclined to do it. I don't love writing HTML, even less so writing [django-style templates](https://github.com/yogthos/Selmer). I much prefer working with something like [hiccup](https://github.com/weavejester/hiccup). I guess I'm easily influenced because a [blog post by an author I've never come across before](https://blog.hamaluik.ca/posts/build-your-own-static-site-generator/) planted the seed.

I was always going to stick with Clojure and had been intending to look into [Babashka](https://babashka.org/), a subset of Clojure with precompiled built-in libraries intended for scripting. I recently saw that [Michiel Borkent](https://twitter.com/borkdude), the author of Babashka, had used it to [replace his previous Octopress blog](https://blog.michielborkent.nl/migrating-octopress-to-babashka.html). I thought I'd see if I could do something similar, keeping open the option of switching back to Clojure if things didn't work out.

Babashka comes with a bunch of useful libraries built-in, but it doesn't include any markdown parsing. It does provide a mechanism for other utilities to expose their functionality as Clojure namespaces. I initially attempted to use one called [bootleg](https://github.com/retrogradeorbit/bootleg). Whilst it uses the same [markdown parser](https://github.com/yogthos/markdown-clj) my site was using, it didn't expose the footnotes option that my existing posts rely on. I [opened a PR addressing the issue](https://github.com/retrogradeorbit/bootleg/pull/76), but the library isn't actively maintained and I'm still not sure if or when I'll get a reply [^oss]. 

[^oss]: This isn't a complaint or a criticism - I don't think we're owed anything from free software maintainers, especially so when they're small one-person projects they created to scratch their own itch.

Raising the issue, however, prompted [Michiel to wonder whether Babashka could run markdown-clj directly from source](https://blog.michielborkent.nl/markdown-clj-babashka-compatible.html). Amazingly, although there were a couple of minor issues stopping it from working initially, within less than day he had fixed the issues, got them merged in upstream and had written all about it! I now had all the tools I needed to recreate my site.

## The Outcome

At the time of writing, the code for the site now consists of:

- ~170 lines [parsing and rendering code](https://github.com/dominicfreeston/freeston-dot-me/blob/main/src/freeston/core.clj)
- ~170 lines [hiccup templates](https://github.com/dominicfreeston/freeston-dot-me/blob/main/src/freeston/templates.clj)
- ~50 lines [RSS feed template](https://github.com/dominicfreeston/freeston-dot-me/blob/main/src/freeston/feed.clj)
- ~70 lines [local development server to preview the site](https://github.com/dominicfreeston/freeston-dot-me/blob/main/src/freeston/server.clj)
- ~20 lines [babashka project and tasks definitions](https://github.com/dominicfreeston/freeston-dot-me/blob/main/bb.edn)
- 4 lines [bash script to get Netlify to install babashka and build the site](https://github.com/dominicfreeston/freeston-dot-me/blob/main/build.sh)

I replicated the layout and design of my site almost exactly. I kept the same [CSS files](https://github.com/dominicfreeston/freeston-dot-me/tree/main/static/css) and the hiccup templates are equivalent to the [original ones](https://github.com/dominicfreeston/freeston-dot-me/tree/545b6da58c3f21cca3b9d44b638e6d2392763c04/themes/freeston/html). I used my new found custom-code-freedom to make a [new home page](https://github.com/dominicfreeston/freeston-dot-me/blob/083b1355995d5be6096af798e6b090b25173e409/src/freeston/templates.clj#L69), rather than just display the latest post as it previously did. My intention is to now expand on this and find better ways to share some of my work than just via blog posts.

