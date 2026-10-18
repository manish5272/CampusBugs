// Already logged in? Go straight to the feed.
if (localStorage.getItem("token")) {
    window.location.href = "index.html";
}

const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");
const messageBox = document.getElementById("message");

// The ONLY place that decides which form is visible.
// classList.toggle("d-none", true) hides an element, toggle("d-none", false) shows it.
function showForm(which) {
    const isLogin = which === "login";
    loginForm.classList.toggle("d-none", !isLogin);
    registerForm.classList.toggle("d-none", isLogin);
    messageBox.className = "alert d-none";
}

function showMessage(text, isError) {
    messageBox.textContent = text;
    messageBox.className = isError ? "alert alert-danger" : "alert alert-success";
}

// Navbar buttons and the links inside the panel
document.getElementById("navLogin").addEventListener("click", function () { showForm("login"); });
document.getElementById("navSignup").addEventListener("click", function () { showForm("register"); });

document.getElementById("showRegister").addEventListener("click", function (event) {
    event.preventDefault();
    showForm("register");
});

document.getElementById("showLogin").addEventListener("click", function (event) {
    event.preventDefault();
    showForm("login");
});

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    try {
        const data = await api("/api/auth/login", "POST", {
            rollNumber: document.getElementById("loginRoll").value,
            password: document.getElementById("loginPassword").value
        });

        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        window.location.href = "index.html";
    } catch (error) {
        showMessage(error.message, true);
    }
});

registerForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    try {
        await api("/api/auth/register", "POST", {
            rollNumber: document.getElementById("registerRoll").value,
            password: document.getElementById("registerPassword").value
        });

        showForm("login");
        showMessage("Account created! Please log in.", false);
    } catch (error) {
        showMessage(error.message, true);
    }
});

// On page load, JavaScript decides what to show. A link ending in #signup opens the sign-up form.
showForm(window.location.hash === "#signup" ? "register" : "login");