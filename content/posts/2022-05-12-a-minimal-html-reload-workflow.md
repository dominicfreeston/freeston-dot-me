{
:title "A minimal HTML reload workflow"
:layout :post
:tags ["clojure" "web" "automation" "meta"]
}

I've been brushing up on [modern css layouts](https://every-layout.dev/) and I wanted something that would reload my changes automatically. Since I'm on Ubuntu with X11, I can use [xdotool](https://manpages.ubuntu.com/manpages/focal/man1/xdotool.1.html) to send keyboard commands to my browser.

I was working with a [babashka](https://babashka.org/) file which spits out an HTML file when I evaluate it and since babashka can also easily run shell commands,  I figured I could just add these two lines at the bottom of my [nrepl-connected script](https://book.babashka.org/#_nrepl):

``` clojure
(defonce browser-window
  (:out (shell/sh "xdotool" "selectwindow")))
(shell/sh "xdotool" "key" "--window" browser-window "F5")
```

The first time it's evaluated, I can use the mouse to select the desired window and from then on that window will be reloaded every time I evaluate the file. There's a bit of a limitation in that it doesn't care what the current browser tab is, it just triggers whatever is currently selected, but it's good enough for my purposes.

Job done! Well, not quite.

Of course this doesn't automate the reload when tweaking the CSS file. And it would be nice to have that functionality when working on this site. Really, I need a file watcher, and happily there's a [babashka pod](https://github.com/babashka/pods) [for file watching](https://github.com/babashka/pod-babashka-fswatcher)[^pods]. Hooking it up is straightforward, but when there's dozens or even hundreds of file changes, it gets a bit manic, so I hacked up together a debouncer using a cancellable future in an atom [^clojurians]:


``` clojure
(def reload-op (atom nil))
(defn trigger-reload []
  (let [[old _] (reset-vals! reload-op (future (Thread/sleep 20) (reload)))]
    (when old (future-cancel old))))

```

I also used the same mechanism to implement a [`watch`](https://github.com/dominicfreeston/freeston-dot-me/blob/main/src/freeston/watch.clj) command for this site which triggers a re-render whenever a file changes. Combined with the browser reload script watching the output folder, it makes for a nice workflow.

The full script is [available as a gist](https://gist.github.com/dominicfreeston/76d4adc6b3594a345f8d2cf683c51a88) and requires babashka and xdotool to be installed. I ended up also making a [macOS version](https://gist.github.com/dominicfreeston/b2b58236752b419fd19cd7d982e114bb) that makes use of AppleScript to trigger the reload instead. If you're reading this, I hope you find it useful.


[^pods]: Well, there's [two](https://github.com/babashka/pod-babashka-filewatcher).

[^clojurians]: I got some advice on how to improve it from the [clojurians slack](https://clojurians.slack.com/), some of which I even applied, though I can't apply it all now because of a current [limitation in babashka](https://github.com/babashka/babashka/issues/1264). I did learn about [`reset-vals!`](https://clojuredocs.org/clojure.core/reset-vals!) which is better than accessing the atom to cancel it and then calling `reset!`. Some also suggested an approach using core.async which sounds cool but maybe overkill for this particular hack.
