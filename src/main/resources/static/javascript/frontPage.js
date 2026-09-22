
// featured builds on front page
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
            buildImage.src = build.mainPhoto;

            buildLink.appendChild(buildImage);
            featuredBuilds.appendChild(buildLink);
        });
    });


// highest horsepower builds on front page
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
            buildImage.src = build.mainPhoto;

            buildLink.appendChild(buildImage);
            highestHorsepowerBuilds.appendChild(buildLink);
        });
    });

// random builds on front page
fetch("api/builds/randomlist")
    .then(response => response.json())
    .then(builds => {
        console.log(builds);

        const randomBuilds = document.getElementById("random-builds");

        builds.forEach(build => {
            const buildLink = document.createElement("a");
            buildLink.href = `/build.html?id=${build.buildId}`;

            const buildImage = document.createElement("img");

            buildImage.className = "build-image";
            buildImage.alt = build.title;
            buildImage.src = build.mainPhoto;

            buildLink.appendChild(buildImage);
            randomBuilds.appendChild(buildLink);
        });
    });


// newest builds on front page
fetch("api/builds/newest")
    .then(response => response.json())
    .then(builds => {
        console.log(builds);

        const newBuilds = document.getElementById("new-builds");

        builds.forEach(build => {
            const buildLink = document.createElement("a");
            buildLink.href = `/build.html?id=${build.buildId}`;

            const buildImage = document.createElement("img");

            buildImage.className = "build-image";
            buildImage.alt = build.title;
            buildImage.src = build.mainPhoto;

            buildLink.appendChild(buildImage);
            newBuilds.appendChild(buildLink);
        });
    });