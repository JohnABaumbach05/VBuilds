fetch("/api/builds/allbuilds")
    .then(response => response.json())
    .then(builds => {
        console.log(builds);

        const allBuilds = document.getElementById("all-builds");

        builds.forEach(build => {
            const buildLink = document.createElement("a");
            buildLink.href = `/build.html?id=${build.buildId}`;

            const buildImage = document.createElement("img");

            buildImage.className = "build-image";
            buildImage.alt = build.title;
            buildImage.src = build.mainPhoto;

            buildLink.appendChild(buildImage);
            allBuilds.appendChild(buildLink);
        });
    });