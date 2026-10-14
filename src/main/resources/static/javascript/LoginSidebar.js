
const loginModal = document.getElementById("login-modal");
const accountModal = document.getElementById("account-modal");

const openLogin = document.getElementById("open-login");
const openAccount = document.getElementById("open-account");

const closeLogin = document.getElementById("close-login");
const closeAccount = document.getElementById("close-account");

// OPEN LOGIN MODAL
openLogin.addEventListener("click", function() {
    loginModal.style.display = "flex";
});

// OPEN ACCOUNT MODAL
openAccount.addEventListener("click", function() {
    accountModal.style.display = "flex";
});

// CLOSE LOGIN MODAL
closeLogin.addEventListener("click", function() {
    loginModal.style.display = "none";
});

// CLOSE ACCOUNT MODAL
closeAccount.addEventListener("click", function() {
    accountModal.style.display = "none";
});


// =============================
// CREATE ACCOUNT
// =============================

// =============================
// CREATE ACCOUNT
// =============================

const accountForm = document.getElementById("account-form");

accountForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const username = document.getElementById("account-username").value;
    const email = document.getElementById("account-email").value;
    const password = document.getElementById("account-password").value;

    const account = {
        username: username,
        email: email,
        password: password
    };

    try {
        // STEP 1: CREATE ACCOUNT
        const response = await fetch("/api/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(account)
        });

        if (response.status === 409) {
            alert("That username is already taken. Please choose another.");
            return;
        }

        if (!response.ok) {
            throw new Error("Account creation failed");
        }

        console.log("Account created successfully!");

        // STEP 2: AUTOMATICALLY LOG IN
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

        if (!loginResponse.ok) {
            throw new Error("Automatic login failed");
        }

        console.log("Automatically logged in!");

        // STEP 3: CLOSE MODAL
        accountModal.style.display = "none";

        // STEP 4: CLEAR REGISTRATION FORM
        accountForm.reset();

        // STEP 5: UPDATE USER INTERFACE
        await checkSession();

    } catch (error) {
        console.error("Registration/login error:", error);
        alert("Something went wrong. If your account was created, try logging in manually.");
    }
});


// =============================
// LOGIN
// =============================

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

        if (!response.ok) {
            throw new Error("Login failed");
        }

        const user = await response.json();

        if (!user) {
            alert("Invalid username or password.");
            return;
        }

        console.log("Logged in:", user.username);

        // CLOSE LOGIN MODAL
        loginModal.style.display = "none";

        // CLEAR LOGIN FORM
        loginForm.reset();

        // UPDATE USER INTERFACE
        await checkSession();

    } catch (error) {
        console.error("Login error:", error);
        alert("Unable to log in. Please check your credentials.");
    }
});
