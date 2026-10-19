// ---------- Guards ----------
// Not logged in? Go to login.
if (!localStorage.getItem("token")) {
    window.location.href = "login.html";
}

// Logged in but not an admin? Go back to the feed.
// (This is only for convenience. The real protection is on the server.)
if (localStorage.getItem("role") !== "ADMIN") {
    window.location.href = "index.html";
}


// ---------- Things we need from the page ----------
const tabRow = document.getElementById("tabRow");
const adminList = document.getElementById("adminList");
const emptyMessage = document.getElementById("emptyMessage");

const STATUSES = ["OPEN", "SEEN", "RESOLVED"];
let currentTab = "active"; // "active" or "hidden"


function buildTabs() {
    const tabs = [
        { key: "active", label: "📋 Active issues" },
        { key: "hidden", label: "🚩 Hidden issues" }
    ];


    tabRow.textContent = "";
    for (const tab of tabs) {
        const button = makeElement("button", "pill", tab.label);
        if (tab.key === currentTab) {
            button.classList.add("active");
        }
        button.addEventListener("click", function () {
            currentTab = tab.key;
            buildTabs();
            loadList();
        });
        tabRow.appendChild(button);
    }
}


// ---------- Loading the list ----------
async function loadList() {
    try {
        let issues;
        if (currentTab === "active") {
            issues = await api("/api/issues", "GET");
        } else {
            issues = await api("/api/admin/issues/hidden", "GET");
        }
        showList(issues);
    } catch (error) {
        showToast(error.message, "error");
    }
}


function showList(issues) {
    adminList.textContent = "";

    if (issues.length === 0) {
        emptyMessage.textContent = currentTab === "active"
            ? "No active issues yet."
            : "No hidden issues. Nothing has been auto-hidden by reports.";
        emptyMessage.classList.remove("d-none");
        return;
    }
    emptyMessage.classList.add("d-none");

    for (const issue of issues) {
        adminList.appendChild(createAdminCard(issue));
    }
}


// ---------- One card ----------
function createAdminCard(issue) {
    const card = makeElement("article", "issue-card status-" + issue.status);
    const body = makeElement("div", "issue-body");

    // Top line: category, votes, date
    const meta = makeElement("div", "d-flex flex-wrap gap-2 align-items-center");
    meta.appendChild(makeElement("span", "chip", categoryLabel(issue.category)));
    meta.appendChild(makeElement("span", "chip", "▲ " + issue.upvoteCount));
    meta.appendChild(makeElement("span", "small text-body-secondary",
        new Date(issue.createdAt).toLocaleDateString()));

    const title = makeElement("h5", "issue-title", issue.title);
    const text = makeElement("p", "issue-text", issue.description);

    body.appendChild(meta);
    body.appendChild(title);
    body.appendChild(text);


    // Bottom line: different buttons depending on the tab
    if (currentTab === "active") {
        body.appendChild(createStatusButtons(issue));
    } else {
        body.appendChild(createRestoreButton(issue));
    }

    card.appendChild(body);
    return card;
}


// ---------- Status buttons (Active tab) ----------
function createStatusButtons(issue) {
    const row = makeElement("div", "status-btns");

    for (const status of STATUSES) {
        const isCurrent = issue.status === status;
        // The current status is filled in. The others are outlined.
        const style = isCurrent ? "btn btn-sm btn-brand" : "btn btn-sm btn-outline-secondary rounded-pill";
        const button = makeElement("button", style, status);

        button.addEventListener("click", async function () {
            if (isCurrent) {
                return; // already in this status, nothing to do
            }
            try {
                await api("/api/admin/issues/" + issue.id + "/status", "PATCH", { status: status });
                showToast("Marked as " + status, "ok");
                loadList(); // reload so the card shows the new status
            } catch (error) {
                showToast(error.message, "error");
            }
        });

        row.appendChild(button);
    }
    return row;
}

// ---------- Restore button (Hidden tab) ----------
function createRestoreButton(issue) {
    const row = makeElement("div", "status-btns");
    const button = makeElement("button", "btn btn-sm btn-brand", "↩ Restore to feed");

    button.addEventListener("click", async function () {
        try {
            await api("/api/admin/issues/" + issue.id + "/restore", "POST");
            showToast("Issue restored", "ok");
            loadList();
        } catch (error) {
            showToast(error.message, "error");
        }
    });

    row.appendChild(button);
    return row;
}

// ---------- Start ----------
buildTabs();
loadList();