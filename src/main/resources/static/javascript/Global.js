fetch("/HTML/Navigation.html")
    .then(response => response.text())
    .then(html => {
        document.getElementById("navigation").innerHTML = html;
    });