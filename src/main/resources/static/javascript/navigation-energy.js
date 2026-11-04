
document.addEventListener("navigationLoaded", () => {

    const svg = document.getElementById("navigation-energy");
    const glow = document.getElementById("energy-glow-path");
    const core = document.getElementById("energy-core-path");

    const sidebar = document.querySelector("aside");
    const header = document.querySelector("header");

    if (!svg || !glow || !core || !sidebar || !header) return;

    if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
        return;
    }

    const duration = 40000;
    const pause = 4500;
    const streakLength = 350;
    const segmentCount = 12;

    let pathLength = 0;
    let startTime = null;
    let animationId = null;

    core.style.opacity = "0";
    glow.style.opacity = "0";

    const namespace = "http://www.w3.org/2000/svg";
    const trailGroup = document.createElementNS(namespace, "g");
    trailGroup.setAttribute("id", "energy-trail");

    const segments = [];

    for (let i = 0; i < segmentCount; i++) {

        const segmentGlow = document.createElementNS(namespace, "path");
        const segmentCore = document.createElementNS(namespace, "path");

        segmentGlow.setAttribute("fill", "none");
        segmentGlow.setAttribute("stroke", "#007bff");
        segmentGlow.setAttribute("stroke-width", "20");
        segmentGlow.setAttribute("stroke-linecap", "butt");
        segmentGlow.setAttribute("stroke", "#007bff");
        segmentGlow.setAttribute("stroke-width", "14");
        segmentGlow.setAttribute("opacity", "0.15");

        segmentCore.setAttribute("fill", "none");
        segmentCore.setAttribute("stroke", "#80dfff");
        segmentCore.setAttribute("stroke-width", "3");
        segmentCore.setAttribute("stroke-linecap", "butt");

        trailGroup.appendChild(segmentGlow);
        trailGroup.appendChild(segmentCore);

        segments.push({
            glow: segmentGlow,
            core: segmentCore
        });
    }

    svg.appendChild(trailGroup);

    function createPath() {

        const width = window.innerWidth;
        const height = window.innerHeight;

        const sidebarWidth = sidebar.getBoundingClientRect().width;
        const headerHeight = header.getBoundingClientRect().height;

        const centerX = sidebarWidth / 2;
        const points = [];
        const sidebarLoops = 3;
        const sidebarSteps = 240;

        for (let i = 0; i <= sidebarSteps; i++) {

            const t = i / sidebarSteps;

            const y = height - t * (height - headerHeight / 2);

            const x =
                centerX +
                Math.sin(t * Math.PI * 2 * sidebarLoops) *
                (sidebarWidth * 0.40);

            points.push([x, y]);
        }
        const headerSteps = 300;
        const headerLoops = 4;

        const startX = centerX;
        const endX = width - 40;

        for (let i = 0; i <= headerSteps; i++) {

            const t = i / headerSteps;

            const x = startX + t * (endX - startX);

            const y =
                headerHeight / 2 +
                Math.sin(t * Math.PI * 2 * headerLoops) *
                (headerHeight * 0.40);

            points.push([x, y]);
        }

        const pathData = points
            .map((point, index) =>
                `${index === 0 ? "M" : "L"} ${point[0]} ${point[1]}`
            )
            .join(" ");

        core.setAttribute("d", pathData);
        glow.setAttribute("d", pathData);

        pathLength = core.getTotalLength();
    }

    createPath();

    function hideTrail() {
        segments.forEach(segment => {
            segment.glow.style.opacity = "0";
            segment.core.style.opacity = "0";
        });
    }

    function animate(timestamp) {

        if (animate.lastFrame && timestamp - animate.lastFrame < 33) {
            animationId = requestAnimationFrame(animate);
            return;
        }

        animate.lastFrame = timestamp;

        if (startTime === null) {
            startTime = timestamp;
        }

        const elapsed =
            (timestamp - startTime) % (duration + pause);

        if (elapsed >= duration || pathLength === 0) {
            hideTrail();
            animationId = requestAnimationFrame(animate);
            return;
        }

        const progress = elapsed / duration;
        const movingForward = progress <= 0.5;

        const position = movingForward
            ? progress * 2
            : (1 - progress) * 2;

        const headDistance = position * pathLength;
        const segmentLength = streakLength / segmentCount;

        segments.forEach((segment, i) => {
            const distanceBehind = i * segmentLength;

            const distance = movingForward
                ? headDistance - distanceBehind
                : headDistance + distanceBehind;

            if (distance < 0 || distance > pathLength) {
                segment.glow.style.opacity = "0";
                segment.core.style.opacity = "0";
                return;
            }

            const nextDistance = movingForward
                ? Math.max(0, distance - segmentLength)
                : Math.min(pathLength, distance + segmentLength);

            const pointA = core.getPointAtLength(distance);
            const pointB = core.getPointAtLength(nextDistance);

            const pathData =
                `M ${pointA.x} ${pointA.y} L ${pointB.x} ${pointB.y}`;

            segment.glow.setAttribute("d", pathData);
            segment.core.setAttribute("d", pathData);
            const fade = Math.pow(
                1 - i / segmentCount,
                1.8
            );

            segment.glow.style.opacity = String(fade * 0.35);
            segment.core.style.opacity = String(fade * 0.45);
        });

        animationId = requestAnimationFrame(animate);
    }

    animationId = requestAnimationFrame(animate);

    window.addEventListener("resize", () => {
        createPath();
    });

});
