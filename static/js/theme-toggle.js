const btn = document.querySelector("#theme-toggle");
const root = document.documentElement;

btn.addEventListener("click", function() {
    const prefersDarkScheme = window.matchMedia("(prefers-color-scheme: dark)");
    
    if (prefersDarkScheme.matches) {
	root.classList.toggle("light-theme");
	root.classList.remove("dark-theme");
  } else {
      root.classList.toggle("dark-theme");
      root.classList.remove("light-theme");
  }
});
