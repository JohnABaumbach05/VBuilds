
document.addEventListener("DOMContentLoaded", async () => {


    const params = new URLSearchParams(window.location.search);
    const buildId = params.get("id");

    if (!buildId || !/^\d+$/.test(buildId)) {
        document.getElementById("build-title").textContent =
            "Invalid Build ID";
        return;
    }

    try {

        const adminPreview = params.get("admin") === "true";


        const backToAdmin = document.getElementById("back-to-admin");

        if (backToAdmin && adminPreview) {
            backToAdmin.hidden = false;
        }
        const buildUrl = adminPreview
            ? `/api/admin/builds/${buildId}`
            : `/api/builds/${buildId}`;

        const response = await fetch(buildUrl, {
            credentials: "include"
        });

        console.log("Admin preview:", adminPreview);
        console.log("Fetching build from:", buildUrl);
        console.log("Response status:", response.status);
        console.log("Response URL:", response.url);

        if (!response.ok) {
            throw new Error(`Failed to load build: ${response.status}`);
        }

        const build = await response.json();
        console.log("Loaded build:", build);

        const ownerLink = document.getElementById("build-owner");
        if (Number.isInteger(build.ownerID) && build.ownerID > 0) {
            try {
                const ownerResponse = await fetch(`/api/users/${build.ownerID}/profile`);
                if (ownerResponse.ok) {
                    const owner = await ownerResponse.json();
                    ownerLink.href = `/HTML/profile.html?id=${owner.id}`;
                    document.getElementById("build-owner-name").textContent = owner.username;
                    const avatar = document.getElementById("build-owner-avatar");
                    if (owner.profilePicture) {
                        const img = document.createElement("img");
                        img.src = owner.profilePicture;
                        img.alt = "";
                        img.onerror = () => { img.remove(); avatar.textContent = "VB"; };
                        avatar.replaceChildren(img);
                    }
                    ownerLink.hidden = false;
                }
            } catch (ownerError) {
                console.warn("Unable to load build owner", ownerError);
            }
        }

        const photoResponse = await fetch(
            `/api/builds/${buildId}/photos`
        );

        let additionalPhotos = [];

        if (photoResponse.ok) {
            additionalPhotos = await photoResponse.json();
        } else {
            console.warn(
                "Unable to load additional build photos:",
                photoResponse.status
            );
        }

        const photos = [];

        if (build.mainPhoto) {
            photos.push({
                filePath: build.mainPhoto,
                caption: "Main Build Photo"
            });
        }

        additionalPhotos.forEach(photo => {

            if (
                photo.filePath &&
                !photos.some(
                    existing => existing.filePath === photo.filePath
                )
            ) {
                photos.push(photo);
            }

        });

        console.log("Build gallery:", photos);

        document.getElementById("build-title").textContent =
            build.title;

        document.getElementById("build-subtitle").textContent =
            `${build.buildYear} Subaru WRX`;

        const mainPhoto = document.getElementById(
            "build-main-photo"
        );

        if (photos.length > 0) {

            mainPhoto.src = photos[0].filePath;
            mainPhoto.alt = photos[0].caption || build.title;
            mainPhoto.hidden = false;

            mainPhoto.onerror = () => {
                mainPhoto.hidden = true;
            };

        }

        document.getElementById("build-horsepower").textContent =
            build.horsepower != null
                ? `${build.horsepower} WHP`
                : "N/A";

        document.getElementById("build-cost").textContent =
            build.cost === 0
                ? "Not Specified"
                : `$${build.cost.toLocaleString()}`;

        document.getElementById("build-tuner").textContent =
            build.tuner || "N/A";

        document.getElementById("build-trim").textContent =
            build.trim || "N/A";

        document.getElementById("build-description").textContent =
            build.description || "No description provided";

        document.getElementById("build-happiness").textContent =
            build.happiness != null
                ? `${build.happiness}/10`
                : "N/A";

        document.getElementById("build-reliability").textContent =
            build.reliability != null
                ? `${build.reliability}/10`
                : "N/A";

        document.getElementById("build-loudness").textContent =
            build.loudness != null
                ? `${build.loudness}/10`
                : "N/A";


        // Load the parts associated with this build.
        const partsList = document.getElementById("build-parts-list");
        if (partsList) {
            try {
                const partsResponse = await fetch(`/api/builds/${buildId}/parts`, {
                    credentials: "include"
                });
                if (!partsResponse.ok) {
                    throw new Error(`Failed to load parts: ${partsResponse.status}`);
                }
                const parts = await partsResponse.json();
                partsList.replaceChildren();
                if (!Array.isArray(parts) || parts.length === 0) {
                    const empty = document.createElement("p");
                    empty.className = "empty-parts";
                    empty.textContent = "No parts added to this build yet.";
                    partsList.appendChild(empty);
                } else {
                    parts.forEach(part => {
                        const card = document.createElement("article");
                        card.className = "installed-part-card";
                        const title = document.createElement("h3");
                        title.textContent = part.partName || "Unnamed Part";
                        const category = document.createElement("p");
                        category.textContent = [part.category, part.manufacturer].filter(Boolean).join(" · ");
                        const detail = document.createElement("p");
                        const cost = Number(part.partCost);
                        detail.textContent = [
                            Number.isFinite(cost) ? `Cost: ${cost.toLocaleString()}` : null,
                            part.estPowerAdded ? `Power gain: +${part.estPowerAdded} HP` : null,
                            part.partReliability != null ? `Reliability: ${part.partReliability}/10` : null,
                            part.makesLouder ? "Increases loudness" : null
                        ].filter(Boolean).join(" · ");
                        card.append(title, category, detail);
                        if (part.partDescription) {
                            const description = document.createElement("p");
                            description.textContent = part.partDescription;
                            card.appendChild(description);
                        }
                        if (part.partLink && /^https?:\/\//i.test(part.partLink)) {
                            const link = document.createElement("a");
                            link.href = part.partLink;
                            link.textContent = "View part";
                            link.target = "_blank";
                            link.rel = "noopener noreferrer";
                            card.appendChild(link);
                        }
                        partsList.appendChild(card);
                    });
                }
            } catch (partsError) {
                console.error("Error loading build parts:", partsError);
                partsList.replaceChildren();
                const message = document.createElement("p");
                message.className = "empty-parts";
                message.textContent = "Unable to load installed parts.";
                partsList.appendChild(message);
            }
        }

        let currentPhotoIndex = 0;

        const previousButton = document.getElementById("photo-prev");
        const nextButton = document.getElementById("photo-next");
        const photoCounter = document.getElementById("photo-counter");
        const photoCaption = document.getElementById("photo-caption");

        function displayPhoto() {

            if (photos.length === 0) {
                mainPhoto.hidden = true;

                if (photoCounter) {
                    photoCounter.textContent = "No photos";
                }

                return;
            }

            const photo = photos[currentPhotoIndex];

            mainPhoto.hidden = false;
            mainPhoto.src = photo.filePath;
            mainPhoto.alt = photo.caption || build.title;

            if (photoCounter) {
                photoCounter.textContent =
                    `${currentPhotoIndex + 1} / ${photos.length}`;
            }

            if (photoCaption) {
                photoCaption.textContent = photo.caption || "";
            }

        }


        if (previousButton) {

            previousButton.addEventListener("click", () => {

                currentPhotoIndex =
                    (currentPhotoIndex - 1 + photos.length)
                    % photos.length;

                displayPhoto();

            });

        }


        if (nextButton) {

            nextButton.addEventListener("click", () => {

                currentPhotoIndex =
                    (currentPhotoIndex + 1) % photos.length;

                displayPhoto();

            });

        }

        if (photos.length <= 1) {

            if (previousButton) {
                previousButton.hidden = true;
            }

            if (nextButton) {
                nextButton.hidden = true;
            }

        }

        displayPhoto();

    } catch (error) {

        console.error("Error loading build:", error);

        document.getElementById("build-title").textContent =
            "Unable to Load Build";

    }

});
