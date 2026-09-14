fetch("/api/builds/featured")
    .then(response => response.json())
    .then(builds => {

        const buildList = document.getElementById("build-list");

        builds.forEach(build => {

            const buildCard = document.createElement("div");

            buildCard.innerHTML = `
                <h2>${build.title}</h2>
                <p>${build.buildYear} ${build.trim}</p>
            `;

            buildList.appendChild(buildCard);
        });
    })
    .catch(error => {
        console.error("Error loading builds:", error);
    });