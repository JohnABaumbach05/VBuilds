document.addEventListener("DOMContentLoaded", async () => {
    const id = new URLSearchParams(window.location.search).get("id");
    const username = document.getElementById("profile-username");
    const grid = document.getElementById("profile-builds");
    if (!id || !/^[1-9]\d*$/.test(id)) {
        username.textContent = "Invalid profile";
        return;
    }
    try {
        const [profileResponse, buildsResponse] = await Promise.all([
            fetch(`/api/users/${id}/profile`),
            fetch(`/api/builds/user/${id}`)
        ]);
        if (profileResponse.status === 404) {
            username.textContent = "Profile not found";
            return;
        }
        if (!profileResponse.ok || !buildsResponse.ok) throw new Error("Request failed");
        const profile = await profileResponse.json();
        const builds = await buildsResponse.json();
        document.title = `${profile.username} | VBuilds`;
        username.textContent = profile.username;
        document.getElementById("profile-bio").textContent = profile.bio || "No bio provided.";
        document.getElementById("profile-count").textContent = `${builds.length} approved build${builds.length === 1 ? "" : "s"}`;
        const socialContainer = document.getElementById("profile-socials");
        const platforms = [
            ["xUrl", "X", "x"],
            ["instagramUrl", "Instagram", "instagram"],
            ["tiktokUrl", "TikTok", "tiktok"],
            ["snapchatUrl", "Snapchat", "snapchat"]
        ];
        for (const [field, name, icon] of platforms) {
            if (!profile[field]) continue;
            const link = document.createElement("a");
            link.href = profile[field];
            link.target = "_blank";
            link.rel = "noopener noreferrer";
            link.title = name;
            link.setAttribute("aria-label", name);
            const image = document.createElement("img");
            image.src = `https://api.iconify.design/simple-icons:${icon}.svg?color=%23ffffff`;
            image.alt = name;
            image.width = 24;
            image.height = 24;
            link.appendChild(image);
            socialContainer.appendChild(link);
        }
        socialContainer.hidden = socialContainer.children.length === 0;
        const avatar = document.getElementById("profile-avatar");
        if (profile.profilePicture) {
            const image = document.createElement("img");
            image.src = profile.profilePicture;
            image.alt = `${profile.username}'s profile picture`;
            image.onerror = () => { avatar.textContent = "VB"; image.remove(); };
            avatar.replaceChildren(image);
        }
        if (!builds.length) {
            grid.textContent = "No approved builds yet.";
            return;
        }
        for (const build of builds) {
            const link = document.createElement("a");
            link.className = "profile-build-card";
            link.href = `/HTML/build.html?id=${build.buildId}`;
            if (build.mainPhoto) {
                const photo = document.createElement("img");
                photo.src = build.mainPhoto;
                photo.alt = build.title;
                photo.loading = "lazy";
                link.appendChild(photo);
            }
            const title = document.createElement("h3");
            title.textContent = build.title;
            const subtitle = document.createElement("p");
            subtitle.textContent = `${build.buildYear} Subaru WRX`;
            link.append(title, subtitle);
            grid.appendChild(link);
        }
    } catch (error) {
        console.error("Unable to load profile", error);
        username.textContent = "Unable to load profile";
    }
});
