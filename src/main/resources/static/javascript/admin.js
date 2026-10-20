
const builds = [
    {
        id: 1,
        title: "2022 Subaru WRX",
        username: "wrxdriver",
        horsepower: 350,
        status: "PENDING",
        submittedAt: "2026-10-20T10:30:00",
        photo: "/images/default-build.jpg"
    },
    {
        id: 2,
        title: "2020 Toyota Supra",
        username: "supraboy",
        horsepower: 500,
        status: "PENDING",
        submittedAt: "2026-10-19T15:00:00",
        photo: "/images/default-build.jpg"
    },
    {
        id: 3,
        title: "2018 Honda Civic Type R",
        username: "typeR",
        horsepower: 320,
        status: "APPROVED",
        submittedAt: "2026-10-18T12:00:00",
        photo: "/images/default-build.jpg"
    }
];

const users = [
    { username: "wrxdriver" },
    { username: "supraboy" },
    { username: "typeR" }
];

// Sidebar navigation
document.querySelectorAll(".nav-btn").forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll(".nav-btn").forEach(btn => {
            btn.classList.remove("active");
        });

        document.querySelectorAll(".admin-page").forEach(page => {
            page.classList.remove("active");
        });

        button.classList.add("active");

        document.getElementById(button.dataset.page)
            .classList.add("active");
    });
});

function createBuildCard(build) {
    const card = document.createElement("div");
    card.className = "build-card";

    const img = document.createElement("img");
    img.src = build.photo;
    img.alt = build.title;

    const info = document.createElement("div");
    info.className = "build-info";

    const title = document.createElement("h3");
    title.textContent = build.title;

    const owner = document.createElement("p");
    owner.textContent = `Submitted by @${build.username}`;

    const horsepower = document.createElement("p");
    horsepower.textContent = `${build.horsepower} HP`;

    const status = document.createElement("p");
    status.textContent = `Status: ${build.status}`;

    info.append(title, owner, horsepower, status);

    if (build.status === "PENDING") {
        const actions = document.createElement("div");
        actions.className = "build-actions";

        const approve = document.createElement("button");
        approve.className = "approve-btn";
        approve.textContent = "Approve";
        approve.addEventListener("click", () => {
            reviewBuild(build.id, "APPROVED");
        });

        const reject = document.createElement("button");
        reject.className = "reject-btn";
        reject.textContent = "Reject";
        reject.addEventListener("click", () => {
            reviewBuild(build.id, "REJECTED");
        });

        actions.append(approve, reject);
        info.append(actions);
    }

    card.append(img, info);
    return card;
}

function renderBuilds(containerId, items) {
    const container = document.getElementById(containerId);
    container.replaceChildren();

    if (items.length === 0) {
        const empty = document.createElement("p");
        empty.textContent = "No builds to display.";
        container.append(empty);
        return;
    }

    items.forEach(build => {
        container.append(createBuildCard(build));
    });
}

function reviewBuild(id, status) {
    const build = builds.find(b => b.id === id);
    if (!build || build.status !== "PENDING") return;

    // Frontend demo only. No database changes.
    build.status = status;
    renderDashboard();
}

function renderDashboard() {
    const pending = builds
        .filter(b => b.status === "PENDING")
        .sort((a, b) =>
            new Date(b.submittedAt) - new Date(a.submittedAt)
        );

    document.getElementById("total-users").textContent =
        users.length;

    document.getElementById("total-builds").textContent =
        builds.length;

    document.getElementById("pending-count").textContent =
        pending.length;

    document.getElementById("approved-count").textContent =
        builds.filter(b => b.status === "APPROVED").length;

    renderBuilds("recent-pending", pending.slice(0, 3));
    renderBuilds("pending-builds", pending);
    renderBuilds("all-builds", builds);

    const usersList = document.getElementById("users-list");
    usersList.replaceChildren();

    users.forEach(user => {
        const p = document.createElement("p");
        p.textContent = `@${user.username}`;
        usersList.append(p);
    });
}

renderDashboard();
