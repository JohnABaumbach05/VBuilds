const accountModal = document.getElementById("account-modal");

const openAccount = document.getElementById("open-account");

const closeAccount = document.getElementById("close-account");


openAccount.addEventListener("click", function() {
    accountModal.style.display = "flex";
});


closeAccount.addEventListener("click", function() {
    accountModal.style.display = "none";
});