
document.addEventListener("DOMContentLoaded", async () => {

    // ================================
    // GET BUILD ID FROM URL
    // ================================

    const params = new URLSearchParams(window.location.search);
    const buildId = params.get("id");

    if (!buildId || !/^\d+$/.test(buildId)) {
        document.getElementById("build-title").textContent =
            "Invalid Build ID";
        return;
    }

    try {

        // ================================
        // FETCH BUILD FROM SPRING BOOT
        // ================================

        const response = await fetch(`/api/builds/${buildId}`);

        if (!response.ok) {
            throw new Error(
                `Failed to load build: ${response.status}`
            );
        }

        const build = await response.json();

        console.log("Loaded build:", build);

        // ================================
        // FETCH ADDITIONAL BUILD PHOTOS
        // ================================

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

        // ================================
        // COMBINE ALL BUILD PHOTOS
        // ================================

        const photos = [];

        // Main photo always comes first
        if (build.mainPhoto) {
            photos.push({
                filePath: build.mainPhoto,
                caption: "Main Build Photo"
            });
        }

        // Add additional photos without duplicates
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

        // ================================
        // BUILD HEADER
        // ================================

        document.getElementById("build-title").textContent =
            build.title;

        document.getElementById("build-subtitle").textContent =
            `${build.buildYear} Subaru WRX`;

        // ================================
        // MAIN BUILD PHOTO
        // ================================

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

        // ================================
        // BUILD SPECIFICATIONS
        // ================================

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

        // ================================
        // BUILD RATINGS
        // ================================

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

        // ================================
        // PHOTO CAROUSEL
        // ================================

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

        // Previous photo
        if (previousButton) {

            previousButton.addEventListener("click", () => {

                currentPhotoIndex =
                    (currentPhotoIndex - 1 + photos.length)
                    % photos.length;

                displayPhoto();

            });

        }

        // Next photo
        if (nextButton) {

            nextButton.addEventListener("click", () => {

                currentPhotoIndex =
                    (currentPhotoIndex + 1) % photos.length;

                displayPhoto();

            });

        }

        // Hide arrows when there is only one photo
        if (photos.length <= 1) {

            if (previousButton) {
                previousButton.hidden = true;
            }

            if (nextButton) {
                nextButton.hidden = true;
            }

        }

        // Display initial photo and counter
        displayPhoto();

    } catch (error) {

        console.error("Error loading build:", error);

        document.getElementById("build-title").textContent =
            "Unable to Load Build";

    }

});
