if (!localStorage.getItem("token")) {
    window.location.href = "login.html";
}

const createForm = document.getElementById("createForm");
const categorySelect = document.getElementById("category");
const titleInput = document.getElementById("title");
const descInput = document.getElementById("description");
const postBtn = document.getElementById("postBtn");
const formError = document.getElementById("formError");

// Fill the dropdown from the shared CATEGORIES list in api.js
function addOption(label, value) {
    const option = makeElement("option", "", label);
    option.value = value;
    categorySelect.appendChild(option);
}

addOption("Select a category…", "");
for (const category of CATEGORIES) {
    addOption(category.label, category.value);
}

// Update the character counters and enable "Post" only when the form is complete
function updateForm() {
    document.getElementById("titleCount").textContent = titleInput.value.length;
    document.getElementById("descCount").textContent = descInput.value.length;

    const complete = categorySelect.value !== ""
        && titleInput.value.trim() !== ""
        && descInput.value.trim() !== "";
    postBtn.disabled = !complete;
}

categorySelect.addEventListener("change", updateForm);
titleInput.addEventListener("input", updateForm);
descInput.addEventListener("input", updateForm);

createForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    postBtn.disabled = true;
    postBtn.textContent = "Posting...";
    formError.classList.add("d-none");

    try {
        await api("/api/issues", "POST", {
            title: titleInput.value,
            description: descInput.value,
            category: categorySelect.value
        });

        // sessionStorage lasts until the tab closes. The feed shows this message once.
        sessionStorage.setItem("flash", "Your issue was posted anonymously 🎉");
        window.location.href = "index.html";
    } catch (error) {
        // Examples: respectful-language rule (400), 3-posts-per-day limit (429)
        formError.textContent = error.message;
        formError.classList.remove("d-none");
        postBtn.textContent = "Post";
        updateForm();
    }
});