const loginModal = document.getElementById("login-modal");
const accountModal = document.getElementById("account-modal");

const openLogin = document.getElementById("open-login");
const openAccount = document.getElementById("open-account");

const closeLogin = document.getElementById("close-login");
const closeAccount = document.getElementById("close-account");

// Open login modal
openLogin.addEventListener("click", function() {
    loginModal.style.display = "flex";
});

// Open account creation modal
openAccount.addEventListener("click", function() {
    accountModal.style.display = "flex";
});

// Close login modal
closeLogin.addEventListener("click", function() {
    loginModal.style.display = "none";
});

// Close account creation modal
closeAccount.addEventListener("click", function() {
    accountModal.style.display = "none";
});


// ==========================================
// ACCOUNT CREATION VALIDATION
// ==========================================

const accountForm = document.getElementById("account-form");

const usernameInput = document.getElementById("account-username");
const emailInput = document.getElementById("account-email");
const passwordInput = document.getElementById("account-password");

// Create a message element underneath the signup form
const accountMessage = document.createElement("p");

accountMessage.id = "account-message";
accountMessage.setAttribute("role", "alert");

accountMessage.style.marginTop = "10px";
accountMessage.style.fontSize = "14px";
accountMessage.style.textAlign = "center";
accountMessage.style.display = "none";

accountForm.appendChild(accountMessage);

// Display signup messages
function showAccountMessage(message, isError = true) {
    accountMessage.textContent = message;
    accountMessage.style.color = isError ? "#ff5555" : "#4caf50";
    accountMessage.style.display = "block";
}

// Clear signup messages
function clearAccountMessage() {
    accountMessage.textContent = "";
    accountMessage.style.display = "none";
}

// Add visible registration requirements
const accountRequirements = document.createElement("p");

accountRequirements.textContent =
    "Username and password must each contain at least 8 characters.";

accountRequirements.style.fontSize = "12px";
accountRequirements.style.color = "#aaa";
accountRequirements.style.textAlign = "center";
accountRequirements.style.marginTop = "8px";
accountRequirements.style.marginBottom = "8px";

// Insert requirements before the submit button
const accountSubmitButton =
    accountForm.querySelector('button[type="submit"]') ||
    accountForm.querySelector("button");

if (accountSubmitButton) {
    accountForm.insertBefore(
        accountRequirements,
        accountSubmitButton
    );
} else {
    accountForm.appendChild(accountRequirements);
}

// Clear errors when users edit their information
[usernameInput, emailInput, passwordInput].forEach(function(input) {
    input.addEventListener("input", clearAccountMessage);
});


// ==========================================
// ACCOUNT CREATION
// ==========================================

accountForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    clearAccountMessage();

    const username = usernameInput.value.trim();
    const email = emailInput.value.trim();
    const password = passwordInput.value;

    // Check required fields
    if (!username || !email || !password) {
        showAccountMessage("Please fill out all fields.");
        return;
    }

    // Username validation
    if (username.length < 3) {
        showAccountMessage(
            "Username must contain at least 3 characters."
        );
        usernameInput.focus();
        return;
    }

    // Password validation
    if (password.length < 8) {
        showAccountMessage(
            "Password must contain at least 8 characters."
        );
        passwordInput.focus();
        return;
    }

    const account = {
        username: username,
        email: email,
        password: password
    };

    // Prevent duplicate submissions
    const submitButton = accountSubmitButton;

    if (submitButton) {
        submitButton.disabled = true;
    }

    try {
        const response = await fetch("/api/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(account)
        });

        // Read the backend error response safely
        if (!response.ok) {
            let errorMessage = "";

            const responseText = await response.text();

            if (responseText) {
                try {
                    const errorData = JSON.parse(responseText);

                    if (typeof errorData === "string") {
                        errorMessage = errorData;
                    } else if (errorData && typeof errorData === "object") {
                        errorMessage =
                            errorData.message ||
                            errorData.error ||
                            errorData.detail ||
                            "";
                    }
                } catch (error) {
                    // Plain text response
                    errorMessage = responseText;
                }
            }

            // Handle conflicts
            if (response.status === 409) {
                showAccountMessage(
                    errorMessage ||
                    "Account information conflicts with an existing account or registration requirement."
                );
                return;
            }

            // Handle invalid registration information
            if (response.status === 400) {
                showAccountMessage(
                    errorMessage ||
                    "Invalid registration information. Please check your details."
                );
                return;
            }

            // Handle all other errors
            showAccountMessage(
                errorMessage ||
                "Account creation failed. Please try again."
            );
            return;
        }

        console.log("Account created successfully!");

        showAccountMessage(
            "Account created! Logging you in...",
            false
        );

        // Automatically log in after registration
        const loginResponse = await fetch("/api/users/login", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (loginResponse.status === 429) {
            showAccountMessage(
                "Account created, but too many login attempts were made. Please try logging in later."
            );
            return;
        }

        if (!loginResponse.ok) {
            showAccountMessage(
                "Account created successfully! Please log in manually."
            );
            return;
        }

        console.log("Automatically logged in!");

        accountModal.style.display = "none";
        accountForm.reset();
        clearAccountMessage();

        await checkSession();

    } catch (error) {
        console.error("Registration/login error:", error);

        showAccountMessage(
            "Something went wrong. If your account was created, try logging in manually."
        );

    } finally {
        if (submitButton) {
            submitButton.disabled = false;
        }
    }
});


// ==========================================
// LOGIN
// ==========================================

const loginForm = document.getElementById("login-form");

loginForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    const login = {
        username: username,
        password: password
    };

    try {
        const response = await fetch("/api/users/login", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(login)
        });

        if (response.status === 429) {
            alert(
                "Too many failed login attempts. Please try again in 15 minutes."
            );
            return;
        }

        if (response.status === 401) {
            alert("Incorrect username or password.");
            return;
        }

        if (!response.ok) {
            throw new Error(
                "Login failed (" + response.status + ")"
            );
        }

        const user = await response.json();

        if (!user) {
            alert("Invalid username or password.");
            return;
        }

        console.log("Logged in:", user.username);

        loginModal.style.display = "none";
        loginForm.reset();

        await checkSession();

    } catch (error) {
        console.error("Login error:", error);
        alert("Something went wrong. Please try again.");
    }
});