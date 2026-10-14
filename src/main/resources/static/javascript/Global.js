fetch("/HTML/Navigation.html")
    .then(response => response.text())
    .then(html => {
        document.getElementById("navigation").innerHTML = html;

        // Tell other scripts the navigation is ready
        document.dispatchEvent(new Event("navigationLoaded"));
    })
    .catch(error => {
        console.error("Failed to load navigation:", error);
    });