{:title "Triggering a download from an HTML form (using hyperscript)"
 :layout :post
 :tags ["html" "web" "hyperscript"]}
 
## *Before you head down the rabbit hole*

The approach described here is entirely unnecessary if you control the API: add the [`Content-Disposition: attachment` header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition) to the response and the browser should download the file rather than display it.
 
## The Problem

I want the user of a web page to be able select some parameters, then click a button that triggers the download of a document (in my case an SVG file) based on those selected parameters. The API being used is designed as a GET request with query parameters - this last bit is important for the approach described here to be viable.

A [`<form>`](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form) is a nearly-perfect HTML-only solution, except that the [`download`](https://developer.mozilla.org/en-US/docs/Web/API/HTMLAnchorElement/download) property is only available on anchor (`<a>`) elements. The best I can do is set the [`target` attribute to `_blank`](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/form#attr-target) and have it open in a new tab or window.


It looks simply like this:
``` html
<form action="some-url" method="get" target="_blank">
  ... form content ...
  <button "type="submit">Download</button>
</form>
```

## The Approach

I decided to use scripting to replace the `<button>` with an `<a>` tag that points to the same URL the form would and keep that URL up to date whenever the content of the form changes.

For this I decided to use [hyperscript](https://hyperscript.org/)[^hyperscript]. Firstly, I defined a function to build up the equivalent GET url for a form and put it in a [`<script>` tag](https://hyperscript.org/docs/#install):

[^hyperscript]: For no other reason than I've been meaning to give it and its companion [htmx](https://htmx.org/) a go, though it does seem particularly well suited to this kind of DOM manipulation.

``` text hljs
def formURL(form)
  set vs to form as Values
  set params to Object.keys(vs).map( \ k -> k + '=' + vs[k]).join('&')
  return (@action of form) + '?' + params
end
```

Then when the page loads, I replace the button with an anchor using the following script on the submit button:

``` text hljs
init make <a#download-link/>
put '<filename>' into its @download
put formURL(closest <form/>) into its @href
put my.innerHTML into it
put it after me
remove me
```
It's a bit wordy, but should be fairly self-explanatory (hyperscript is good like that). The reason I do this part with a script rather than simply use an anchor to begin with is to embrace the idea of [progressive enhancement](https://developer.mozilla.org/en-US/docs/Glossary/Progressive_Enhancement). If JavaScript is disabled or the HyperScript dependency can't be loaded, we're still left with our default working behaviour of opening the file into a new tab.

With a bit of CSS I can make the button and the anchor look the same, making the experience virtually identical in both scenarios (apart from the download enhancement) and avoid any visible flash as one element replaces the other.

Finally, the key part is to keep the URL up to date as the form's inputs change, which can now be done by attaching this one-liner to the form element[^form-change]:

``` text hljs
on change put formURL(me) into @href of #download-link
```


## The Result
The resulting code is sufficiently generic that it should be possible to drop this into any HTML page and have it mostly "just work"[^just-work]:

``` html
<script src="https://unpkg.com/hyperscript.org@0.9.5"></script>

<script type="text/hyperscript">
  def formURL(form)
    set vs to form as Values
    set params to Object.keys(vs).map( \ k -> k + '=' + vs[k]).join('&')
    return (@action of form) + '?' + params
  end
</script>

<form action="some-url" method="get" target="_blank"
      _="on change put formURL(me) into @href of #download-link">

  ... form content ...
  
  <button type="submit"
          _= "init make <a#download-link/>
              put '<filename>' into its @download
              put formURL(closest <form/>) into its @href
              put my.innerHTML into it
              put it after me
              remove me">
    Download
  </button>
</form>
```

[^form-change]: At first I thought I needed to attach this script to every field in the form, but putting it on the form element itself seems to do the trick.

[^just-work]: Of course, the filename will need to be updated to something suitable and the form content needs to be filled in!
