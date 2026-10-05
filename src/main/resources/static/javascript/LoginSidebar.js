const loginModal = document.getElementById("login-modal");
const accountModal = document.getElementById("account-modal");

const openLogin = document.getElementById("open-login");
const openAccount = document.getElementById("open-account");

const closeLogin = document.getElementById("close-login");
const closeAccount = document.getElementById("close-account");


openLogin.addEventListener("click", function() {
    loginModal.style.display = "flex";
});


openAccount.addEventListener("click", function() {
    accountModal.style.display = "flex";
});


closeLogin.addEventListener("click", function() {
    loginModal.style.display = "none";
});


closeAccount.addEventListener("click", function() {
    accountModal.style.display = "none";
});


const accountForm = document.getElementById("account-form");

accountForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const username = document.getElementById("account-username").value;
    const email = document.getElementById("account-email").value;
    const password = document.getElementById("account-password").value;

    const account = {
        username: username,
        email: email,
        password: password,
    };
    fetch("/api/users", {
        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(account)
    });
});


const loginForm = document.getElementById("login-form");

loginForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    const login = {
        username: username,
        password: password
    };

    fetch("/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(login)
    })
        .then(response => response.json())
        .then(user => {
            console.log(user);
        });
});


