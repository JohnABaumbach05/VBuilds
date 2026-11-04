
document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("create-build-form");
    const submitButton = form.querySelector('button[type="submit"]');
    const ratings = ["happiness", "reliability", "loudness"];

    ratings.forEach((rating) => {

        const slider = document.getElementById(rating);
        const display = document.getElementById(rating + "Value");

        slider.addEventListener("input", () => {
            display.textContent = slider.value;
        });

    });

    form.addEventListener("submit", async (event) => {

        event.preventDefault();
        if (submitButton.disabled) {
            return;
        }

        const buildData = {

            title: document.getElementById("buildTitle").value.trim(),

            buildYear: Number(
                document.getElementById("year").value
            ),

            trim: document.getElementById("trim").value.trim()
                || "Not Specified",

            horsepower: Number(
                document.getElementById("horsepower").value
            ) || 220,

            tuner: document.getElementById("tuner").value.trim()
                || "Not Specified",

            description: document.getElementById("caption").value.trim()
                || "No Description Yet",

            happiness: Number(
                document.getElementById("happiness").value
            ),

            reliability: Number(
                document.getElementById("reliability").value
            ),

            loudness: Number(
                document.getElementById("loudness").value
            ),

            cost: Number(
                document.getElementById("buildCost").value
            ) || 0

        };

        if (!buildData.title || !buildData.buildYear) {
            alert("Please enter a build title and year.");
            return;
        }

        if (buildData.buildYear < 2022) {
            alert("Build year must be 2022 or newer.");
            return;
        }

        submitButton.disabled = true;
        submitButton.textContent = "Creating Build...";

        let createdBuildId = null;

        try {
            const mainPhotoInput =
                document.getElementById("mainPhoto");

            const mainPhoto = mainPhotoInput.files[0];

            if (mainPhoto) {

                const formData = new FormData();
                formData.append("file", mainPhoto);

                const uploadResponse = await fetch(
                    "/api/builds/upload-photo",
                    {
                        method: "POST",
                        credentials: "include",
                        body: formData
                    }
                );

                if (!uploadResponse.ok) {
                    const errorText = await uploadResponse.text();
                    console.error("Main photo upload failed:", errorText);

                    throw new Error("Failed to upload main photo.");
                }

                const uploadResult = await uploadResponse.json();

                buildData.mainPhoto = uploadResult.url;

            } else {

                buildData.mainPhoto = null;

            }

            const response = await fetch("/api/builds", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                credentials: "include",

                body: JSON.stringify(buildData)

            });

            if (!response.ok) {

                if (response.status === 401 ||
                    response.status === 403) {

                    throw new Error(
                        "You must be logged in to create a build."
                    );

                }

                const errorText = await response.text();

                console.error("Build creation failed:", errorText);

                throw new Error(
                    "Failed to create build. Check the console."
                );
            }

            const result = await response.json();

            createdBuildId = result.buildId;

            if (!createdBuildId) {
                throw new Error(
                    "Build was created, but no build ID was returned."
                );
            }

            console.log("Created build ID:", createdBuildId);

            const additionalPhotos =
                document.getElementById("additionalPhotos").files;

            for (const photo of additionalPhotos) {

                const formData = new FormData();

                formData.append("file", photo);

                const uploadResponse = await fetch(
                    "/api/builds/upload-photo",
                    {
                        method: "POST",
                        credentials: "include",
                        body: formData
                    }
                );

                if (!uploadResponse.ok) {

                    const errorText = await uploadResponse.text();

                    console.error(
                        "Additional photo upload failed:",
                        errorText
                    );

                    throw new Error(
                        `Failed to upload photo: ${photo.name}`
                    );
                }

                const uploadResult = await uploadResponse.json();

                console.log("Photo upload result:", uploadResult);
                console.log("Photo URL:", uploadResult.url);
                const saveResponse = await fetch(
                    `/api/builds/${createdBuildId}/photos`,
                    {
                        method: "POST",

                        credentials: "include",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify({
                            photoId: 0,
                            buildId: createdBuildId,
                            filePath: uploadResult.url,
                            caption: ""
                        })
                    }
                );

                if (!saveResponse.ok) {

                    const errorText = await saveResponse.text();

                    console.error(
                        "Failed to save photo record:",
                        errorText
                    );

                    throw new Error(
                        `Failed to save photo: ${photo.name}`
                    );
                }

                console.log(
                    "Additional photo saved:",
                    uploadResult.url
                );
            }
            console.log("Build created successfully:", createdBuildId);

            window.location.href = `/HTML/build.html?id=${createdBuildId}`;
            ratings.forEach((rating) => {

                const slider = document.getElementById(rating);
                const display = document.getElementById(
                    rating + "Value"
                );

                display.textContent = slider.value;

            });

            console.log(
                "Build and gallery created successfully:",
                createdBuildId
            );

        } catch (error) {

            console.error("Error creating build:", error);

            if (createdBuildId) {

                alert(
                    "Your build was created, but some gallery photos " +
                    "may not have saved. Build ID: " +
                    createdBuildId +
                    ". Do not submit again or you may create a duplicate."
                );

            } else {

                alert(error.message);

            }
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = "Create Build";

        }

    });

});
