
async function checkSession() {
    try {
        const response = await fetch("/api/users/me", {
            credentials: "include"
        });

        if (!response.ok) {
            return null;
        }


        const responseText = await response.text();


        if (!responseText.trim()) {
            return null;
        }


        const user = JSON.parse(responseText);

        if (user) {
            console.log("Logged in as:", user.username);

            const adminLink = document.getElementById("admin-link");

            if (adminLink) {
                adminLink.style.display = user.admin ? "inline-block" : "none";
            }


            const loginButton =
                document.getElementById("open-login");

            const accountButton =
                document.getElementById("open-account");

            const loggedInUser =
                document.getElementById("logged-in-user");

            const usernameDisplay =
                document.getElementById("display-username");

            if (loginButton) {
                loginButton.style.display = "none";
            }

            if (accountButton) {
                accountButton.style.display = "none";
            }

            if (loggedInUser && usernameDisplay) {
                loggedInUser.style.display = "block";
                usernameDisplay.textContent = user.username;
            }
        }

        return user;

    } catch (error) {
        console.error("Session check failed:", error);
        return null;
    }
}



document.addEventListener("navigationLoaded", checkSession);

document.addEventListener("DOMContentLoaded", function() {
    if (document.getElementById("admin-link")) {
        checkSession();
    }
});


const logoutButton = document.getElementById("logout-button");

if (logoutButton) {
    logoutButton.addEventListener("click", async function() {
        const response = await fetch("/api/users/logout", {
            method: "POST",
            credentials: "include"
        });

        if (response.ok) {
            window.location.reload();
        }
    });
}

