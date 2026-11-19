async function loadBuildEngagement(buildId) {
    const endpoint = `/api/builds/${buildId}/engagement`;
    const section = document.getElementById("build-engagement");
    const likeButton = document.getElementById("build-like");
    const photoLikes = document.getElementById("build-photo-likes");
    const guestIcon = document.getElementById("build-like-guest-icon");
    const likeStatus = document.getElementById("build-like-status");
    const count = document.getElementById("build-like-count");
    const signIn = document.getElementById("build-engagement-login");
    const form = document.getElementById("build-comment-form");
    const input = document.getElementById("build-comment");
    const postButton = form.querySelector("button");
    const status = document.getElementById("build-engagement-status");
    const empty = document.getElementById("build-comments-empty");
    const comments = document.getElementById("build-comments");
    const formatDate = new Intl.DateTimeFormat(undefined, { dateStyle: "medium", timeStyle: "short" });
    let liked = false;

    function showLikes(likes) {
        liked = likes.liked;
        likeButton.setAttribute("aria-pressed", String(liked));
        count.textContent = `${likes.count} ${likes.count === 1 ? "like" : "likes"}`;
    }

    function showSignedIn(signedIn) {
        likeButton.hidden = !signedIn;
        guestIcon.hidden = signedIn;
        form.hidden = !signedIn;
        signIn.hidden = signedIn;
    }

    function renderComment(entry) {
        const item = document.createElement("li");
        const author = document.createElement("strong");
        author.textContent = entry.username || "Deleted account";
        const date = new Date(entry.createdAt);
        const time = document.createElement("time");
        time.dateTime = date.toISOString();
        time.textContent = formatDate.format(date);
        const body = document.createElement("p");
        body.textContent = entry.comment;
        item.append(author, time, body);
        return item;
    }

    section.hidden = false;

    try {
        const response = await fetch(endpoint, { cache: "no-store" });
        if (!response.ok) throw new Error();
        const data = await response.json();
        showLikes(data.likes);
        comments.replaceChildren(...data.comments.map(renderComment));
        empty.hidden = data.comments.length > 0;
        showSignedIn(data.signedIn);
        photoLikes.hidden = false;
        if (window.location.hash === '#build-engagement') section.scrollIntoView();
    } catch {
        status.textContent = "Unable to load likes and comments. Refresh to try again.";
        return;
    }

    likeButton.addEventListener("click", async () => {
        likeButton.disabled = true;
        likeStatus.hidden = true;
        try {
            const response = await fetch(`${endpoint}/like`, { method: liked ? "DELETE" : "PUT" });
            if (response.status === 401) {
                showSignedIn(false);
                return;
            }
            if (!response.ok) throw new Error();
            showLikes(await response.json());
        } catch {
            likeStatus.textContent = "Unable to update your like. Try again.";
            likeStatus.hidden = false;
        } finally {
            likeButton.disabled = false;
        }
    });

    form.addEventListener("submit", async event => {
        event.preventDefault();
        if (postButton.disabled) return;
        const comment = input.value.trim();
        if (!comment || comment.length > 1000) {
            status.textContent = "Enter a comment of 1 to 1000 characters.";
            return;
        }

        postButton.disabled = true;
        input.readOnly = true;
        status.textContent = "";
        try {
            const response = await fetch(`${endpoint}/comments`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ comment })
            });
            if (response.status === 401) {
                showSignedIn(false);
                return;
            }
            if (!response.ok) throw new Error();
            comments.prepend(renderComment(await response.json()));
            empty.hidden = true;
            input.value = "";
            status.textContent = "Comment posted.";
        } catch {
            status.textContent = "Unable to post your comment. Try again.";
        } finally {
            postButton.disabled = false;
            input.readOnly = false;
        }
    });
}
