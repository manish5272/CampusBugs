// ---------- Shared helpers used by every page ----------

// One list of categories used by the feed, the cards and the create page.
// "value" must exactly match your Java Category enum.
const CATEGORIES = [
    { value: "MESS", label: "🍽️ Mess" },
    { value: "HOSTEL", label: "🛏️ Hostel" },
    { value: "WIFI", label: "📶 Wi-Fi" },
    { value: "LIBRARY", label: "📚 Library" },
    { value: "LAB", label: "🧪 Lab" },
    { value: "OTHER", label: "💬 Other" }
];

function categoryLabel(value) {
    // find() returns the first item where the arrow function gives true
    const found = CATEGORIES.find(c => c.value === value);
    return found ? found.label : value;
}

// ---------- Theme (light / dark) ----------

function applyTheme(theme) {
    document.documentElement.setAttribute("data-bs-theme", theme);
    localStorage.setItem("theme", theme);

    const button = document.getElementById("themeBtn");
    if (button) {
        button.textContent = theme === "dark" ? "☀️" : "🌙";
    }
}

function toggleTheme() {
    const current = document.documentElement.getAttribute("data-bs-theme");
    applyTheme(current === "dark" ? "light" : "dark");
}

// ---------- Login / logout ----------

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    window.location.href = "login.html";
}

// ---------- Talking to our Spring Boot API ----------

// Returns the JSON reply, or throws an Error with a readable message.
// The error also carries the HTTP status (error.status), e.g. 409.
async function api(path, method, body) {
    const headers = { "Content-Type": "application/json" };

    const token = localStorage.getItem("token");
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    const response = await fetch(path, {
        method: method,
        headers: headers,
        body: body ? JSON.stringify(body) : undefined
    });

    let data = null;
    try {
        data = await response.json();
    } catch (e) {
        // Some replies have no body. That is fine.
    }

    if (!response.ok) {
        // Token expired or invalid: send the user back to the login page
        if (response.status === 401 && !path.startsWith("/api/auth")) {
            logout();
        }
        const error = new Error(readErrorMessage(data));
        error.status = response.status;
        throw error;
    }
    return data;
}

function readErrorMessage(data) {
    if (data && data.errors && data.errors.length > 0) {
        return data.errors[0].defaultMessage;
    }
    if (data && data.message) {
        return data.message;
    }
    return "Something went wrong. Please try again.";
}

// ---------- Small UI helpers ----------

// Builds an HTML element safely (textContent, never innerHTML)
function makeElement(tag, className, text) {
    const element = document.createElement(tag);
    element.className = className;
    if (text !== undefined) {
        element.textContent = text;
    }
    return element;
}

// Small message that appears at the bottom of the screen for 3.5 seconds
function showToast(message, type) {
    let box = document.getElementById("toastBox");
    if (!box) {
        box = makeElement("div", "toast-box");
        box.id = "toastBox";
        document.body.appendChild(box);
    }
    const kind = type === "error" ? "toast-error" : "toast-ok";
    const toast = makeElement("div", "toast-msg " + kind, message);
    box.appendChild(toast);
    setTimeout(function () { toast.remove(); }, 3500);
}

// Runs on every page: connects the theme and logout buttons if the page has them
function setupNavbar() {
    const themeButton = document.getElementById("themeBtn");
    if (themeButton) {
        themeButton.addEventListener("click", toggleTheme);
    }
    const logoutButton = document.getElementById("logoutBtn");
    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
    }
    applyTheme(localStorage.getItem("theme") || "light");
}

setupNavbar();