fetch("/api/builds/featured")
    .then(response => response.json())
    .then(builds => {
        console.log(builds);

        const featuredBuilds = document.getElementById("featured-builds");

        builds.forEach(build => {
            const buildLink = document.createElement("a");
            buildLink.href = `/build.html?id=${build.buildId}`;

            const buildImage = document.createElement("img");

            buildImage.className = "build-image";
            buildImage.alt = build.title;

            fetch(`/api/builds/${build.buildId}/photos`)
                .then(response => response.json())
                .then(photos => {
                    console.log(photos[0].filePath);

                    buildImage.src = photos[0].filePath;

                    buildLink.appendChild(buildImage);
                    featuredBuilds.appendChild(buildLink);
                });
        });
    });

fetch("/api/builds/top")
    .then(response => response.json())
    .then(builds => {
        console.log(builds);

        const highestHorsepowerBuilds = document.getElementById("highest-horsepower");

        builds.forEach(build => {
            const buildLink = document.createElement("a");
            buildLink.href = `/build.html?id=${build.buildId}`;

            const buildImage = document.createElement("img");

            buildImage.className = "build-image";
            buildImage.alt = build.title;

            fetch(`/api/builds/${build.buildId}/photos`)
                .then(response => response.json())
                .then(photos => {
                    console.log(photos[0].filePath);

                    buildImage.src = photos[0].filePath;

                    buildLink.appendChild(buildImage);
                    highestHorsepowerBuilds.appendChild(buildLink);
                });
        });
    });