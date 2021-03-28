{
:title "Reproduceable Generative Art with Quil and Clojure"
:layout :post
:tags ["art" "clojure" "quil"]
}

![](/img/workflow/ring.jpg)

## Context
I've been making generative artwork in [Clojure](https://clojure.org/) using [Quil](http://quil.info/) for just over 2 years now. Over time I've evolved a workflow that enables me to experiment quickly whilst reducing the risk of losing interesting results and avenues for exploration.

Most of the artwork I make is static images, usually with the intention of plotting them using an [AxiDraw](https://www.axidraw.com/). I usually export images as both `png` for previewing (and sometimes printing) and `svg` for plotting. I am also gradually developing [a small library](https://github.com/dominicfreeston/penny) with a focus on manipulating shapes and lines.

I work in [Emacs](https://www.gnu.org/software/emacs/) connected to a repl using [CIDER](https://cider.mx/), which means that evaluating the current buffer generates a new sketch. 

It's often useful to be able to [reproduce a particular output](https://www.kovach.me/Infrastructure_of_art.html), and the way I've been approaching this is to replace Clojure's random functions with an [implementation where the seed can be set](https://github.com/trystan/random-seed/blob/0acf96c82c32c8b442e79ad0caf0db49cebc6d29/src/random_seed/core.clj), as well as setting the seed for Quil's own [random](http://quil.info/api/math/random#random-seed) and [noise](http://quil.info/api/math/random#noise-seed) functions.

I'd been exporting images with the [random seed in the filename](https://tylerxhobbs.com/essays/2016/utilizing-random-number-generator-seeds) for a while, but as I tend to continually tweak the code, it often isn't enough to recreate that exact output. I wanted a simple way to save the state of the source code as well.

## Solution

So far I've settled on something like this[^fun-mode]:

[^fun-mode]: I initially worked with Quil's [functional-mode middleware](https://github.com/quil/quil/wiki/Functional-mode-(fun-mode) (because Clojure), but moved away from it as I do all the rendering in a single draw call anyway.

``` clojure
(ns sketch
  (:require
	[quil.core :as q]
	[clojure.java.io :as io]
	[random-seed.core :refer :all])
  (:refer-clojure :exclude
	[rand rand-int rand-nth shuffle]))
  
...

(defn export []
  (let [name (str sketch-name "-" seed)
        src (:file (meta #'export))
        dest (str "output/code/" name ".clj")
        png (str "output/png/" name ".png")
        svg (str "output/svg/" name ".svg")
        gr (q/create-graphics
		(q/width) (q/height) :svg svg)]
    ;; Save source
    (io/make-parents dest)
    (io/copy (io/file src) (io/file dest))
    ;; Save png
    (set-random-seed! seed)
    (draw)
    (q/save png)
    ;; Save svg
    (set-random-seed! seed)
    (q/with-graphics gr
      (draw)
      (.dispose gr)))
  (q/exit))
```

Exporting a sketch, including copying the source, now means replacing the [draw function in the sketch](http://quil.info/api/environment#defsketch) with this export function, without the need for extra infrastructure and setup. It does require that a) there is a single file we want to save a snapshot of and b) the export function is within that file (as it uses the metadata of the export function itself) - but that suits me well at this point.
