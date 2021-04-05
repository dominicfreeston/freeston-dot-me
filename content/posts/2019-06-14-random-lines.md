{
:title "Random Lines"
:layout :post
:tags ["clojure", "drawing"]
}

I've been toying with Clojure and Quil to generate drawings that I can then draw with a pen plotter. The most successful thing I've managed to do so far is a little program that:

1. defines a shape by generating a random set of lines within a small grid
2. repeats that shape in a larger grid, randomly varying its scale and rotation

The grids and repetition create a pleasing sense of regularity whilst the randomness leaves just enough room for some surprises. I find myself looking for extra patterns, even in the aspects I know to be random.

Every time you load this page you'll get a different image below:
![](https://protected-brook-19426.herokuapp.com/)

(It may take a while to load because it's on a free heroku instance and spins down after 30min of inactivity)
