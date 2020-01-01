{
:title "Retrospective"
:layout :post
:tags ["clojure" "art"]
}

I started my Clojure journey with Advent of Code 2018, with the long term and nebulous goal of [learning every language](https://blog.bradfieldcs.com/in-2017-learn-every-language-59b11f68eee). Separately I also was getting more curious about computer and generative art as a potential new creative outlet and was inspired to use our newly acquired pen plotter to produce a physical output, so I found some [very helpful resources](https://mattdesl.svbtle.com/pen-plotter-1) and started trying things out. I didn't achieve much there but I made one thing that made me want to keep going:

![](/img/retrospective/lines.jpeg)

Unable or unwilling to commit to Javascript based tools, struggling to focus on learning Clojure without an obvious goal in mind, I eventually came across [Quil](http://www.quil.info)[^quil] and realised I could work towards two goals at once. It's turned out to be an incredibly rewarding and somewhat successful endeavour. Below are a few of the things I've made this year.

[^quil]: [Tyler Hobb's talk at Clojure/conj](https://tylerxhobbs.com/essays/2019/code-goes-in-art-comes-out) is worth a watch.

![](/img/retrospective/cards.jpeg)
I first started with laying out grids of shape and playing around with [scaling and rotating such repeated shapes](/posts/2019-06-14-random-lines/), which made for some quite attractive birthday cards.

![](/img/retrospective/inkblots.jpeg)
One of the first times I went from idea to execution in the space of an evening, I created this drawing inspired by the inkblot patterns on my wife's wedding dress.

![](/img/retrospective/curves.jpeg)
At one point, just throwing random curves at the canvas, I found myself creating images that I couldn't plot because I'd accidentally kept the fill color. I played around with converting the bitmap output to vectors and playing with Inkscape's tools and got some fairly interesting results quite far from the original, though I don't see myself going down this manual route again any time soon.

![](/img/retrospective/perp.jpeg)
I played around with growing a drawing using perpendicular offshoots off existing lines, an idea I might go back to once I can get over the fact it's [already been done to near perfection](http://www.complexification.net/gallery/machines/substrate/index.php).

![](/img/retrospective/map.jpeg)
I'd been wanting the option to fill shapes or crop a drawing to a shape and finally did it to produce this. I have begun growing a [small library](https://github.com/dominicfreeston/penny/) for those kinds of operations.

![](/img/retrospective/space_invader.jpeg)
Although not generative, it was fun to come up with an idea for my brother's Christmas gift and have the tools and the skills to be able to produce it.
