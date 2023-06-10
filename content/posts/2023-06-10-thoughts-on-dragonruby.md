{
:title "Thoughts on DragonRuby (from a Clojure convert)"
:layout :post
:tags ["clojure" "ruby" "games"]
:image "/img/dragonruby/animatch-penguin.png"
}

Clojure is my favourite language at this point; I've been using it to make art and websites for a few years, and last year got my first job using it. I was all in and I'd never have to write in a lesser language again. And then I spent most of my spare time this year writing [small](https://animatch.freeston.me/) [games](https://dominicfreeston.itch.io/hey-thats-my-yarn) in [Ruby](https://github.com/dominicfreeston/kifass2023). What gives?

The reason is I wanted to make games again, and the reason is the [DragonRuby Game Toolkit (DR)](https://dragonruby.itch.io/dragonruby-gtk). Many people come to DR because of Ruby, but I'm using it despite of Ruby. Here's some things I think it has going for it.

## It's a 2D-first code-only engine

For some it's a limitation, for me it's a strength. I made The Wallslider in Godot and I think it's an amazing open source project (Godot, not my game); but it does a lot of things I just don't need and they're always _there_ in the editor. I have no interest in making 3D games. DragonRuby's focus on 2D means its APIs are designed and optimised for my use case, and I get to keep working in my beloved Emacs.

## Simple, low-level, data-oriented APIs

The creator of DR, Amir Rajan, has said this aspect of DR's design is inspired by Clojure which might explain why despite the languages' many differences I feel quite at home in this strange corner of Ruby-land (all the mutation still makes me feel icky though).

There's no complex machinery or class
hierarchy to hook into - just some simple basic abstractions to build on top of as you see fit.

There's a `tick` function (method, technically, but we can pretend), that gets called 60 times a second (or as close as it can get), and each tick you tell DR what to render, handle input, compute any changes to your game state, etc.

Rendering graphics and playing audio is just pushing hashes (maps/dictionaries) into arrays and letting the engine handle the rest. Handling input is just checking the current state.

That's about it. The rest is up to you. But actually there's lots of well considered helper functions for collision detection, easing and interpolation, animation, etc and dozens and dozens of examples provided for all kinds of game styles and features.

## Dogmatic dedication to cross-platform support and easy deployment.

This is one of the main reasons I'm investing time in DR - Amir created DR to solve his own problems as a solo indie game developer trying to ship multiple games on multiple platforms.

As someone's who's built mobile apps for a living and has seen the effort that can go in supporting even just two platforms, I really appreciate the complete focus on cross platform support.

This comes with some caveats or limitations, like currently no shaders because doing this truly cross platforms is hard. A lot of these are tied to what [SDL](https://en.wikipedia.org/wiki/Simple_DirectMedia_Layer) supports (one of the [developers of DR is also a core contributor and maintainer of SDL](https://en.wikipedia.org/wiki/Ryan_C._Gordon)), and so some shader support might be on its way with SDL3, but in the meantime it has some features that can fill in a lot of the use cases for shaders in 2D and I can trust that my code will run pretty much identically on all platforms without modification.

Building for Windows, Mac, Linux and Web with a single command is great - it can even deploy automatically to [itch.io](https://itch.io/) and has beta support for easier [Steam](https://store.steampowered.com/) deployment. I've not yet tried it's mobile support and I'm unlikely to ever ship to console, but I like to know they are available with a (quite reasonably priced) pro licence.

## Hot reload 

Good hot reload is a super power and DR's is excellent. When I make an error, the built-in console shows me the stack trace and some often helpful error message, then the game picks right back up where it left off with my new changes applied.

For the situations where your new code can't work with the current state, you can reset the game and you're back in at the start of your game within a second.

## It's actually pretty fast

"But Ruby is slow!" - a language can't be slow, silly! It's down to the implementation. DragonRuby is based on mruby, not the Ruby of Rails, which is designed to be embedded. Also a lot of the engine is actually C, and the developers are even working on a _compiler_ for the Ruby parts. Amir gave a good overview of how the engine works and their future plans at [rubyconf last year](https://youtu.be/s2rngApV1WU).

## A lovely supporting community on Discord

The docs are somewhat lacking in places, but the community (which includes the developers themselves) is always there to help and support.

## The End

And that's it. 

It has its quirks (when will DR get circle primitives is a running joke, "just use a sprite" is the official answer) and it lacks the ecosystem of bigger engines, but DragonRuby is pretty darn good and I plan to continue using it for my game making efforts in the foreseeable future (whilst I continue to encourage Amir to make a lisp version).