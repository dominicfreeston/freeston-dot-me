{
:title "Vector Fields Exploration"
:layout :post
:tags ["clojure" "art"]
}

<div align=center>
<img src="/img/vectorfields/orange.png" width=70% />
</div>

A budding generative artist will, sooner or later, play with [vector fields](https://en.wikipedia.org/wiki/Vector_field)[^colors].

[Tyler Hobbs](https://tylerxhobbs.com/), one of my favourite generative artists, recently wrote a [good introduction](https://tylerxhobbs.com/essays/2020/flow-fields) that prompted me to make that time be now. All the images in this post are created using roughly the same program based on a field populated with perlin/simplex noise and
tweaking a dozen or so parameters.

[^colors]: Also, apparently start with black and white images and eventually "discover colors" then talk or write about how hard it is to use well. Which, considering some of the choices made here, is a fair point.

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/fur-1.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/fur-2.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/fur-3.png" />
  </div>
</div>

By restricting the starting point of the flow lines and letting them run long in a chaotic flow field we get structured yet dynamic images:

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/smoke-1.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/smoke-2.png" />
  </div>
</div>


<div class="row">
  <div class="item">
	<img src="/img/vectorfields/bw-2.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/bw-3.png" />
  </div>
</div>

Maintaining the high step-size through the noise field but ensuring the field still flows in roughly one direction leads to long lines quickly converging into the main paths through the field. Combine with highly saturated colors for some 80s sci-fi vibes:

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/neon-1.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/neon-2.png" />
  </div>
</div>

Swap for filled shapes defined by short lines for some winding vine-like effects:

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/plant-1.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/plant-2.png" />
  </div>
  <div class="item">
  <img src="/img/vectorfields/plant-3.png" />
  </div>
</div>
<div class="row">
  <div class="item">
  <img src="/img/vectorfields/wp-red.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/wp-green.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/wp-purple.png" />
  </div>
</div>

Extend the length of the lines defining some heavily layered shapes for some dynamic dreamscapes:

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/dream-1.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/dream-2.png" />
  </div>
</div>
<div class="row">
  <div class="item">
	<img src="/img/vectorfields/dream-3.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/dream-4.png" />
  </div>
</div>

Pairing flow lines at random and layering resulting shapes can create some interesting texture with a sense of light peering through, almost like stained glass, though here using random short lines and pairing them may achieve a similar effect more simply:

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/blue-0.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/blue-1.png" />
  </div>
</div>
<div class="row">
  <div class="item">
	<img src="/img/vectorfields/blue-2.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/blue-3.png" />
  </div>
</div>


Different combinations of layered semi-transparent shapes from grouping multiple flow-lines create interesting watercolor paint-like effects:

<div class="row">
  <div class="item">
  <img src="/img/vectorfields/paint-1.png" />
  </div>
  <div class="item">
	<img src="/img/vectorfields/paint-2.png" />
  </div>
</div>

Lots more to explore, but some exciting and varied initial results from a fairly straightforward system.
