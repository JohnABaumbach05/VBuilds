


let pendingBuilds = [];
let allBuilds = [];
let currentBuildFilter = "all";
let allUsers = [];



async function loadAllUsers() {
    try {
        const response = await fetch("/api/admin/users", {
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        allUsers = await response.json();
        renderAllUsers();

    } catch (error) {
        console.error("Failed to load users:", error);
        alert("Could not load users.");
    }
}

function renderAllUsers() {
    const container = document.getElementById("users-list");
    container.replaceChildren();

    if (allUsers.length === 0) {
        container.textContent = "No registered users found.";
        return;
    }

    allUsers.forEach(user => {
        const card = document.createElement("div");
        card.className = "build-card";

        const info = document.createElement("div");
        info.className = "build-info";

        const username = document.createElement("h3");
        username.textContent = user.username;

        const email = document.createElement("p");
        email.textContent = user.email;

        const joined = document.createElement("p");
        joined.textContent = `Joined: ${user.dateJoined ?? "Unknown"}`;

        const role = document.createElement("p");
        role.textContent = user.admin
            ? "Role: Administrator"
            : "Role: Member";

        info.append(username, email, joined, role);
        card.append(info);
        container.append(card);
    });
}



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


async function loadPendingBuilds() {
    try {
        const response = await fetch("/api/admin/builds/pending", {
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        pendingBuilds = await response.json();
        renderDashboard();

    } catch (error) {
        console.error("Failed to load pending builds:", error);
        alert("Could not load pending builds.");
    }
}


function getBuildStatus(build) {
    if (build.inProcess && !build.approved) {
        return "pending";
    }

    if (build.approved) {
        return "approved";
    }

    return "rejected";
}


function renderAllBuilds() {
    const container = document.getElementById("all-builds");
    container.replaceChildren();

    const filteredBuilds = allBuilds.filter(build => {
        if (currentBuildFilter === "all") {
            return true;
        }

        return getBuildStatus(build) === currentBuildFilter;
    });

    if (filteredBuilds.length === 0) {
        const message = document.createElement("p");
        message.textContent = "No builds found.";
        container.append(message);
        return;
    }

    filteredBuilds.forEach(build => {
        const card = document.createElement("div");
        card.className = "build-card";

        const info = document.createElement("div");
        info.className = "build-info";

        const title = document.createElement("h3");
        title.textContent = build.title;

        const viewButton = document.createElement("a");

        viewButton.href =
            `/HTML/build.html?id=${build.buildId}&admin=true`;

        viewButton.textContent = "View Full Build";
        viewButton.className = "view-build-btn";

        const owner = document.createElement("p");
        owner.textContent = `Owner ID: ${build.ownerID ?? build.ownerId}`;

        const horsepower = document.createElement("p");
        horsepower.textContent = `${build.horsepower} HP`;

        const status = document.createElement("p");
        status.textContent =
            `Status: ${getBuildStatus(build).toUpperCase()}`;


        if (build.approved) {
            const featureBtn = document.createElement("button");

            featureBtn.className = "feature-btn";

            featureBtn.textContent = build.featured
                ? "Unfeature"
                : "Feature";

            featureBtn.addEventListener("click", async () => {
                const action = build.featured
                    ? "unfeature"
                    : "feature";

                featureBtn.disabled = true;

                try {
                    const response = await fetch(
                        `/api/admin/builds/${build.buildId}/${action}`,
                        {
                            method: "PUT",
                            credentials: "include"
                        }
                    );

                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}`);
                    }

                    await loadAllBuilds();

                } catch (error) {
                    console.error("Feature update failed:", error);
                    alert("Could not update featured status.");
                    featureBtn.disabled = false;
                }
            });

            info.append(featureBtn);
        }


        info.append(title, owner, horsepower, status, viewButton);
        card.append(info);
        container.append(card);
    });
}


document.querySelectorAll(".filter-btn").forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll(".filter-btn").forEach(btn => {
            btn.classList.remove("active");
        });

        button.classList.add("active");

        currentBuildFilter = button.dataset.filter;

        renderAllBuilds();
    });
});





async function loadAllBuilds() {
    try {
        const response = await fetch("/api/admin/builds", {
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        allBuilds = await response.json();

        renderAllBuilds();

    } catch (error) {
        console.error("Failed to load all builds:", error);
        alert("Could not load all builds.");
    }
}



async function loadDashboardStats() {
    try {
        const response = await fetch("/api/admin/stats", {
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const stats = await response.json();

        document.getElementById("total-users").textContent =
            stats.totalUsers;

        document.getElementById("total-builds").textContent =
            stats.totalBuilds;

        document.getElementById("pending-count").textContent =
            stats.pendingBuilds;

        document.getElementById("approved-count").textContent =
            stats.approvedBuilds;

    } catch (error) {
        console.error("Failed to load dashboard stats:", error);
    }
}


function createBuildCard(build) {
    const card = document.createElement("div");
    card.className = "build-card";

    const info = document.createElement("div");
    info.className = "build-info";

    const title = document.createElement("h3");
    title.textContent = build.title;


    const viewButton = document.createElement("a");

    viewButton.href =
        `/HTML/build.html?id=${build.buildId}&admin=true`;

    viewButton.textContent = "View Full Build";
    viewButton.className = "view-build-btn";


    const owner = document.createElement("p");
    owner.textContent = `Owner ID: ${build.ownerID ?? build.ownerId}`;

    const horsepower = document.createElement("p");
    horsepower.textContent = `${build.horsepower} HP`;

    const status = document.createElement("p");
    status.textContent = "Status: PENDING";

    const actions = document.createElement("div");
    actions.className = "build-actions";

    const approve = document.createElement("button");
    approve.className = "approve-btn";
    approve.textContent = "Approve";

    const reject = document.createElement("button");
    reject.className = "reject-btn";
    reject.textContent = "Reject";

    approve.addEventListener("click", () => {
        reviewBuild(build.buildId, "approve", approve, reject);
    });

    reject.addEventListener("click", () => {
        reviewBuild(build.buildId, "reject", approve, reject);
    });

    actions.append(approve, reject);
    info.append(
        title,
        owner,
        horsepower,
        status,
        viewButton,
        actions
    );
    card.append(info);

    return card;
}


function renderBuilds(containerId, builds) {
    const container = document.getElementById(containerId);
    container.replaceChildren();

    if (builds.length === 0) {
        const message = document.createElement("p");
        message.textContent = "No pending builds.";
        container.append(message);
        return;
    }

    builds.forEach(build => {
        container.append(createBuildCard(build));
    });
}


async function reviewBuild(buildId, action, approveBtn, rejectBtn) {
    approveBtn.disabled = true;
    rejectBtn.disabled = true;

    try {
        const response = await fetch(
            `/api/admin/builds/${buildId}/${action}`,
            {
                method: "PUT",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        await Promise.all([
            loadPendingBuilds(),
            loadDashboardStats()
        ]);

    } catch (error) {
        console.error(`Failed to ${action} build:`, error);
        alert(`Failed to ${action} build.`);
    } finally {
        approveBtn.disabled = false;
        rejectBtn.disabled = false;
    }
}


function renderDashboard() {
    renderBuilds(
        "recent-pending",
        pendingBuilds.slice(0, 3)
    );

    renderBuilds(
        "pending-builds",
        pendingBuilds
    );
}


loadPendingBuilds();
loadDashboardStats();
loadAllBuilds();
loadAllUsers();
