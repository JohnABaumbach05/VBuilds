
fetch("/api/builds/allbuilds")
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to load builds");
        }

        return response.json();
    })
    .then(builds => {

        console.log("Loaded builds:", builds);

        const allBuilds = document.getElementById("all-builds");

        builds.forEach(build => {
            const buildLink = document.createElement("a");
            buildLink.href = `/HTML/build.html?id=${build.buildId}`;
            const buildImage = document.createElement("img");
            buildImage.className = "build-image";
            buildImage.alt = build.title;
            if (build.mainPhoto) {
                buildImage.src = build.mainPhoto;
            } else {
                buildImage.style.display = "none";
            }
            buildLink.appendChild(buildImage);
            allBuilds.appendChild(buildLink);

        });

    })
    .catch(error => {
        console.error("Error loading builds:", error);
    });
