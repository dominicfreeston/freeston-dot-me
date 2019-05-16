{
:title "Procrastivity"
:layout :post
:tags ["clojure" "meta"]
}

Of course this site was going to become it's own source of procrastination. [Cryogen](http://cryogenweb.org/) works quite well as it is, but there is room for improvement (including a few welcoming 'help wanted' labels in the issues). One of the reasons for picking it was for me to use some more Clojure, plus I've been wanting to make some more open source contributions. Lucky for me then, there were some issues to distract me.

### XML Generation

Having deployed this site for the first time, I pointed my [RSS reader](https://feedwrangler.net/) to my new feed and then... saw an empty title. Looking at the raw feed revealed a potential cause: lots of new lines, including before the content of the tags. Digging through the code lead me to [clj-rss](https://github.com/yogthos/clj-rss), which in turn relies on Clojure's own [clojure.xml](https://clojure.github.io/clojure/clojure.xml-api.html). So why the issue?

The great thing about Clojure being open source, is you can look at it[^oss]. Looking at [`emit`](https://github.com/clojure/clojure/blob/b9b1a094499b69a94bd47fc94c4f082d80239fa9/src/clj/clojure/xml.clj#L111) reveals what can only have been intended as a debugging helper - it uses `print` rather than returning a string or writing to a file. It does none of what would be required for proper xml support like escaping strings. It also adds new lines everywhere (via `println`), including in tags containing text. There's our issue! It's changing the content of the tag: it now starts and ends with an extra new line.

[^oss]: As an iOS developer, being able to look at source code is still a relative novelty.

There's better alternatives, of course, most notably [data.xml](https://github.com/clojure/data.xml)[^data], but I'm confused as to why something like `emit` would ship in Clojure's core libraries. It might be that using access-control is less typical in Clojure code and doc strings are what's primarily used to communicate intent. Regardless, my proposed change to using data.xml in Cryogen was promptly accepted, deployed and bubbled up through to Cryogen itself.

[^data]: It is at version 0.2.0-alpha6 at the time of writing, but seems stable enough. I'm under the impression Clojure libs have a tendency to stay sub 1.0 for a long time.

### Markdown Parsing

Like with many static site generators, posts for this site are written in Markdown. You may have already noticed I enjoy peppering my writing with plenty of links and asides via footnotes[^footnotes]. A previous draft of a similar post quickly put [markdown-clj](https://github.com/yogthos/markdown-clj) through its paces and surfaced issues with multiple types of links in the same paragraph.

[^footnotes]: I've never had an idea I couldn't expand on or wander away from.

Not much to expand upon here, but it was a useful exercise in figuring out somebody else's Clojure code and another opportunity to contribute back. It has also raised some questions in my mind around [Clojure and testing](https://puredanger.github.io/tech.puredanger.com/2013/08/31/clojure-and-testing/). Part of the language's appeal for me has been its underlying philosophy and it's going to be interesting to see which parts continue to resonate.


### Future Contributions

My interactions with the [maintainers](https://github.com/orgs/cryogen-project/people) of Cryogen and its dependencies have been a delight so far, and so I've decided to [take on](https://github.com/cryogen-project/cryogen-core) one of the [previously raised issues](https://github.com/cryogen-project/cryogen-core/issues/98). Fixing and expanding a static site generator is not exactly what I'd planned to spend these couple of weeks on, but it's been both educational and rewarding.
