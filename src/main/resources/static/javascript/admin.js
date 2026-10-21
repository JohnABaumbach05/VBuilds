
// VBuilds Admin Dashboard

let pendingBuilds = [];

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

// Fetch pending builds from Spring Boot
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

// Fetch dashboard statistics
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

// Create a build card
function createBuildCard(build) {
    const card = document.createElement("div");
    card.className = "build-card";

    const info = document.createElement("div");
    info.className = "build-info";

    const title = document.createElement("h3");
    title.textContent = build.title;

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
    info.append(title, owner, horsepower, status, actions);
    card.append(info);

    return card;
}

// Render build cards
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

// Approve or reject builds
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

// Render pending builds
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

// Initialize dashboard
loadPendingBuilds();
loadDashboardStats();
