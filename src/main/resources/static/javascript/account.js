
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
        window.location.href = "/login.html";
        return;
    }

    const user = await response.json();

    if (!user) {
        window.location.href = "/login.html";
        return;
    }

    document.getElementById("username").textContent = user.username;
    document.getElementById("email").textContent = user.email;
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

