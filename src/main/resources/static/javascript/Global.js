fetch("/HTML/Navigation.html")
    .then(response => {
        if (!response.ok) throw new Error(`Navigation failed (${response.status})`);
        return response.text();
    })
    .then(html => {
        const navigation = document.getElementById("navigation");
        if (!navigation) return;
        navigation.innerHTML = html;
        const toggle = document.getElementById("mobile-menu-toggle");
        const backdrop = document.getElementById("mobile-nav-backdrop");
        const sidebar = document.getElementById("mobile-sidebar");
        const closeMenu = () => {
            document.body.classList.remove("mobile-nav-open");
            toggle.setAttribute("aria-expanded", "false");
            toggle.setAttribute("aria-label", "Open navigation menu");
        };
        toggle.addEventListener("click", () => {
            const open = !document.body.classList.contains("mobile-nav-open");
            document.body.classList.toggle("mobile-nav-open", open);
            toggle.setAttribute("aria-expanded", String(open));
            toggle.setAttribute("aria-label", open ? "Close navigation menu" : "Open navigation menu");
        });
        backdrop.addEventListener("click", closeMenu);
        sidebar.addEventListener("click", event => {
            if (event.target.closest("a")) closeMenu();
        });
        document.addEventListener("keydown", event => {
            if (event.key === "Escape") closeMenu();
        });
        window.matchMedia("(min-width: 769px)").addEventListener("change", event => {
            if (event.matches) closeMenu();
        });
        document.dispatchEvent(new Event("navigationLoaded"));
    })
    .catch(error => console.error("Failed to load navigation:", error));
