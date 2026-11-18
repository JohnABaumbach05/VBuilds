
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

    const pendingParts = [];
    const categorySelect = document.getElementById("partCategory");
    const customCategoryWrap = document.getElementById("customCategoryWrap");
    const customCategoryInput = document.getElementById("customCategory");
    const addedPartsList = document.getElementById("addedPartsList");
    categorySelect.addEventListener("change", () => {
        const custom = categorySelect.value === "__custom__";
        customCategoryWrap.hidden = !custom;
        customCategoryInput.required = custom;
        if (!custom) customCategoryInput.value = "";
    });

    function renderPendingParts() {
        addedPartsList.replaceChildren();
        pendingParts.forEach((part, index) => {
            const card = document.createElement("article");
            card.className = "added-part-card";
            const heading = document.createElement("h3");
            heading.textContent = part.partName;
            const details = document.createElement("p");
            details.textContent = `${part.category} · ${part.manufacturer} · ${part.partCost}`;
            const remove = document.createElement("button");
            remove.type = "button";
            remove.textContent = "Remove";
            remove.addEventListener("click", () => {
                pendingParts.splice(index, 1);
                renderPendingParts();
            });
            card.append(heading, details, remove);
            addedPartsList.appendChild(card);
        });
    }

    document.getElementById("addPartButton").addEventListener("click", () => {
        const partLink = document.getElementById("partLink");
        if (!partLink.reportValidity()) return;
        const custom = categorySelect.value === "__custom__";
        const category = (custom ? customCategoryInput.value : categorySelect.value).trim();
        const partName = document.getElementById("partName").value.trim();
        const manufacturer = document.getElementById("partManufacturer").value.trim();
        const costField = document.getElementById("partCost");
        const partCost = Number(costField.value || 0);
        const partReliability = Number(document.getElementById("partReliability").value);
        const estPowerAdded = Number(document.getElementById("partPower").value);
        if (!category || !partName || !manufacturer || category.length > 60 ||
            !Number.isInteger(partCost) || partCost < 0 ||
            !Number.isInteger(partReliability) || partReliability < 0 || partReliability > 10 ||
            !Number.isInteger(estPowerAdded) || estPowerAdded < 0) {
            alert("Enter a category, manufacturer, part name and valid part values.");
            return;
        }
        pendingParts.push({
            category, manufacturer, partName, partCost, partReliability,
            estPowerAdded,
            partLink: partLink.value.trim() || null,
            partDescription: document.getElementById("partDescription").value.trim(),
            makesLouder: document.getElementById("partLouder").checked
        });
        renderPendingParts();
        for (const id of ["partName", "partManufacturer", "partCost", "partDescription", "partPower", "partLink"]) {
            document.getElementById(id).value = "";
        }
        document.getElementById("partReliability").value = "5";
        document.getElementById("partLouder").checked = false;
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
            for (const part of pendingParts) {
                const partResponse = await fetch(`/api/builds/${createdBuildId}/parts`, {
                    method: "POST",
                    credentials: "include",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(part)
                });
                if (!partResponse.ok) {
                    throw new Error("Build created, but one or more parts could not be saved.");
                }
            }

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
                    "Your build was created, but some parts or gallery photos " +
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
