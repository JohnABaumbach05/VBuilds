fetch("/HTML/Navigation.html")
    .then(response => response.text())
    .then(html => {
        document.getElementById("navigation").innerHTML = html;
        document.dispatchEvent(new Event("navigationLoaded"));
    })
    .catch(error => {
        console.error("Failed to load navigation:", error);
    });