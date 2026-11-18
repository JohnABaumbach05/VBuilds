const feedbackForm = document.getElementById("feedback-form");
const feedbackInput = document.getElementById("feedback-comment");
const feedbackButton = feedbackForm.querySelector("button");
const feedbackStatus = document.getElementById("feedback-status");
const feedbackListStatus = document.getElementById("feedback-list-status");
const feedbackList = document.getElementById("feedback-comments");
const feedbackDateFormat = new Intl.DateTimeFormat(undefined, { dateStyle: "medium", timeStyle: "short" });

function renderFeedbackComment(entry) {
    const item = document.createElement("li");
    const comment = document.createElement("p");
    comment.textContent = entry.comment;
    const time = document.createElement("time");
    const date = new Date(entry.createdAt);
    time.dateTime = date.toISOString();
    time.textContent = feedbackDateFormat.format(date);
    item.append(comment, time);
    return item;
}

async function loadFeedback() {
    try {
        const response = await fetch("/api/feedback", { cache: "no-store" });
        if (!response.ok) throw new Error("Unable to load comments.");
        const comments = await response.json();
        const items = document.createDocumentFragment();
        for (const comment of comments) {
            items.append(renderFeedbackComment(comment));
        }
        feedbackList.replaceChildren(items);
        feedbackListStatus.textContent = comments.length ? "" : "No comments yet.";
        return true;
    } catch {
        feedbackListStatus.textContent = "Unable to load all comments. Refresh to try again.";
        return false;
    }
}

const initialFeedbackLoad = loadFeedback();

feedbackForm.addEventListener("submit", async event => {
    event.preventDefault();
    if (feedbackButton.disabled) return;

    const comment = feedbackInput.value.trim();
    if (!comment || comment.length > 1000) {
        feedbackStatus.textContent = "Enter a comment of 1 to 1000 characters.";
        return;
    }

    feedbackButton.disabled = true;
    feedbackInput.readOnly = true;
    feedbackStatus.textContent = "Posting…";
    try {
        await initialFeedbackLoad;
        const response = await fetch("/api/feedback", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ comment })
        });
        if (!response.ok) throw new Error("Unable to post comment.");

        const saved = await response.json();
        feedbackList.prepend(renderFeedbackComment(saved));
        feedbackInput.value = "";
        feedbackStatus.textContent = "Comment posted.";
        await loadFeedback();
    } catch {
        feedbackStatus.textContent = "Unable to post your comment. Try again.";
    } finally {
        feedbackButton.disabled = false;
        feedbackInput.readOnly = false;
    }
});
