const btn = document.querySelector("#theme-toggle");
const root = document.documentElement;

const currentTheme = localStorage.getItem("theme");
if (currentTheme) {
    root.classList.add(currentTheme);
}

btn.addEventListener("click", function() {
    const prefersDarkScheme = window.matchMedia("(prefers-color-scheme: dark)");
    
    if (prefersDarkScheme.matches) {
	root.classList.toggle("light-theme");
	root.classList.remove("dark-theme");
    } else {
	root.classList.toggle("dark-theme");
	root.classList.remove("light-theme");
    }

    const newTheme = root.classList[0];
    if (newTheme) {
	localStorage.setItem("theme", newTheme);
    } else {
	localStorage.removeItem("theme");
    }

    btn.blur();
});
