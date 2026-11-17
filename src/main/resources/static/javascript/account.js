
let userBuilds = [];

document.addEventListener("DOMContentLoaded", async () => {
    await loadAccount();
    await loadBuilds();

    document.querySelectorAll("[data-filter]").forEach(button => {
        button.addEventListener("click", () => {
            displayBuilds(button.dataset.filter);
        });
    });
});

async function loadAccount() {
    const response = await fetch("/api/users/me");

    if (!response.ok) {
        window.location.href = "/index.html";
        return;
    }

    const user = await response.json();

    if (!user) {
        window.location.href = "/login.html";
        return;
    }

    document.getElementById("username").textContent = user.username;
    document.getElementById("email").textContent = user.email;
    showProfilePicture(user.profilePicture);
}

async function loadBuilds() {
    const response = await fetch("/api/builds/mine");

    if (!response.ok) {
        console.error("Failed to load builds");
        return;
    }

    userBuilds = await response.json();
    displayBuilds("all");
}


function displayBuilds(filter) {
    const container = document.getElementById("build-container");
    container.innerHTML = "";

    const filtered = userBuilds.filter(build => {
        if (filter === "approved") return build.approved;
        if (filter === "pending") return build.inProcess;
        if (filter === "rejected") {
            return !build.approved && !build.inProcess;
        }
        return true;
    });

    if (filtered.length === 0) {
        container.textContent = "No builds found.";
        return;
    }

    filtered.forEach(build => {
        const card = document.createElement("div");
        card.className = "build-card";


        const buildLink = document.createElement("a");
        buildLink.href = `/HTML/build.html?id=${build.buildId}`;
        buildLink.className = "account-build-link";


        if (build.mainPhoto) {
            const image = document.createElement("img");
            image.src = build.mainPhoto;
            image.alt = build.title;
            image.className = "account-build-image";
            buildLink.appendChild(image);
        }


        const title = document.createElement("h3");
        title.textContent = build.title;
        buildLink.appendChild(title);


        const status = document.createElement("p");
        status.className = "build-status";

        status.textContent = build.approved
            ? "Approved"
            : build.inProcess
                ? "In Process"
                : "Rejected";


        const editButton = document.createElement("button");
        editButton.textContent = "Edit Build";
        editButton.disabled = true;
        editButton.title = "Build editing coming soon";

        card.append(buildLink, status, editButton);
        container.appendChild(card);
    });
}


let currentAccount = null;
let picturePreviewUrl = null;

function showProfilePicture(url) {
    const avatar = document.querySelector(".profile-avatar");
    if (!avatar) return;
    avatar.replaceChildren();
    if (url) {
        const img = document.createElement("img");
        img.src = url;
        img.alt = "Profile picture";
        img.className = "avatar-image";
        img.onerror = () => { avatar.textContent = "VB"; };
        avatar.appendChild(img);
    } else {
        avatar.textContent = "VB";
    }
}

function clearPreviewUrl() {
    if (picturePreviewUrl) URL.revokeObjectURL(picturePreviewUrl);
    picturePreviewUrl = null;
}

async function openAccountSettings() {
    const response = await fetch("/api/users/me");
    if (!response.ok) {
        window.location.href = "/login.html";
        return;
    }
    currentAccount = await response.json();
    document.getElementById("settings-username").value = currentAccount.username ?? "";
    document.getElementById("settings-email").value = currentAccount.email ?? "";
    document.getElementById("settings-bio").value = currentAccount.bio ?? "";
    document.getElementById("current-password").value = "";
    document.getElementById("new-password").value = "";
    document.getElementById("profile-picture").value = "";
    document.getElementById("settings-message").textContent = "";
    clearPreviewUrl();
    const preview = document.getElementById("picture-preview");
    preview.hidden = !currentAccount.profilePicture;
    if (currentAccount.profilePicture) preview.src = currentAccount.profilePicture;
    document.getElementById("account-settings").hidden = false;
}

document.addEventListener("DOMContentLoaded", () => {
    const pictureInput = document.getElementById("profile-picture");
    const preview = document.getElementById("picture-preview");

    document.getElementById("settings-btn").addEventListener("click", openAccountSettings);
    document.getElementById("cancel-settings").addEventListener("click", () => {
        clearPreviewUrl();
        document.getElementById("account-settings").hidden = true;
    });

    pictureInput.addEventListener("change", () => {
        clearPreviewUrl();
        const file = pictureInput.files[0];
        if (!file) return;
        if (!["image/jpeg", "image/png", "image/webp"].includes(file.type) ||
                file.size > 5 * 1024 * 1024) {
            document.getElementById("settings-message").textContent =
                "Select a JPG, PNG, or WebP image under 5 MB.";
            pictureInput.value = "";
            return;
        }
        picturePreviewUrl = URL.createObjectURL(file);
        preview.src = picturePreviewUrl;
        preview.hidden = false;
    });

    document.getElementById("settings-form").addEventListener("submit", async event => {
        event.preventDefault();
        const message = document.getElementById("settings-message");
        const saveButton = document.getElementById("save-settings");
        saveButton.disabled = true;
        message.textContent = "Saving...";
        const payload = {
            username: document.getElementById("settings-username").value,
            email: document.getElementById("settings-email").value,
            bio: document.getElementById("settings-bio").value,
            currentPassword: document.getElementById("current-password").value,
            newPassword: document.getElementById("new-password").value
        };
        try {
            const response = await fetch("/api/users/me", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            if (!response.ok) {
                message.textContent = await response.text();
                return;
            }
            const updated = await response.json();
            document.getElementById("username").textContent = updated.username;
            document.getElementById("email").textContent = updated.email;
            document.getElementById("current-password").value = "";
            document.getElementById("new-password").value = "";
            currentAccount = updated;

            const file = pictureInput.files[0];
            if (file) {
                const form = new FormData();
                form.append("file", file);
                const upload = await fetch("/api/users/me/profile-picture", {
                    method: "POST", body: form
                });
                if (!upload.ok) {
                    message.textContent = "Profile saved, but photo upload failed: " +
                        await upload.text();
                    return;
                }
                const result = await upload.json();
                currentAccount.profilePicture = result.profilePicture;
                showProfilePicture(result.profilePicture);
                pictureInput.value = "";
                clearPreviewUrl();
            }
            message.textContent = "Account updated successfully!";
        } catch (error) {
            console.error(error);
            message.textContent = "Unable to save changes.";
        } finally {
            saveButton.disabled = false;
        }
    });
});
