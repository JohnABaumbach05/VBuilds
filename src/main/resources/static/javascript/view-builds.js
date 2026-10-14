
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

            // Create clickable build link
            const buildLink = document.createElement("a");
            buildLink.href = `/HTML/build.html?id=${build.buildId}`;

            // Create build image
            const buildImage = document.createElement("img");

            buildImage.className = "build-image";
            buildImage.alt = build.title;

            // Use uploaded photo if available
            if (build.mainPhoto) {
                buildImage.src = build.mainPhoto;
            } else {
                // Hide image if build has no photo
                buildImage.style.display = "none";
            }

            // Add image to link
            buildLink.appendChild(buildImage);

            // Add link to page
            allBuilds.appendChild(buildLink);

        });

    })
    .catch(error => {
        console.error("Error loading builds:", error);
    });
