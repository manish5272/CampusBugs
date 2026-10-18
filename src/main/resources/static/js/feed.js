// If the user is not logged in, send them to the login page
if (!localStorage.getItem("token")) {
    window.location.href = "login.html";
}

const issueList = document.getElementById("issueList");
const emptyMessage = document.getElementById("emptyMessage");
const pillRow = document.getElementById("categoryPills");

let currentCategory = ""; // "" means all categories

const STATUS_STYLES = {
    OPEN: "bg-danger",
    SEEN: "bg-warning text-dark",
    RESOLVED: "bg-success"
};

// ---------- Category pills ----------

function buildPills() {
    // [...] copies the CATEGORIES list after our extra "All" option
    const options = [{ value: "", label: "🔥 All" }, ...CATEGORIES];

    pillRow.textContent = "";
    for (const option of options) {
        const pill = makeElement("button", "pill", option.label);
        if (option.value === currentCategory) {
            pill.classList.add("active");
        }
        pill.addEventListener("click", function () {
            currentCategory = option.value;
            buildPills();
            loadIssues();
        });
        pillRow.appendChild(pill);
    }
}

// ---------- Loading and showing issues ----------

async function loadIssues() {
    let url = "/api/issues";
    if (currentCategory !== "") {
        url = url + "?category=" + currentCategory;
    }

    try {
        const issues = await api(url, "GET");
        showIssues(issues);
    } catch (error) {
        showToast(error.message, "error");
    }
}

function showIssues(issues) {
    // The server sorts by votes. For equal votes the order is not guaranteed,
    // so we add "newest first" as a tie-breaker.
    issues.sort(function (a, b) {
        if (b.upvoteCount !== a.upvoteCount) {
            return b.upvoteCount - a.upvoteCount;
        }
        return new Date(b.createdAt) - new Date(a.createdAt);
    });

    issueList.textContent = ""; // remove the old cards
    emptyMessage.classList.toggle("d-none", issues.length > 0);

    for (const issue of issues) {
        issueList.appendChild(createIssueCard(issue));
    }
}

function createIssueCard(issue) {
    const card = makeElement("article", "issue-card status-" + issue.status);

    // ----- Left column: upvote button and count -----
    const voteCol = makeElement("div", "vote-col");
    const voteBtn = makeElement("button", "vote-btn", "▲");
    voteBtn.title = "Upvote";
    const voteCount = makeElement("div", "vote-count", issue.upvoteCount);

    voteBtn.addEventListener("click", async function () {
        try {
            const updated = await api("/api/issues/" + issue.id + "/upvote", "POST");
            voteCount.textContent = updated.upvoteCount;
            voteBtn.classList.add("voted");
        } catch (error) {
            if (error.status === 409) {
                voteBtn.classList.add("voted"); // you had already voted
            }
            showToast(error.message, "error");
        }
    });

    voteCol.appendChild(voteBtn);
    voteCol.appendChild(voteCount);

    // ----- Right side: details -----
    const body = makeElement("div", "issue-body");

    const meta = makeElement("div", "d-flex flex-wrap gap-2 align-items-center");
    meta.appendChild(makeElement("span", "chip", categoryLabel(issue.category)));
    meta.appendChild(makeElement("span", "badge rounded-pill " + STATUS_STYLES[issue.status], issue.status));
    meta.appendChild(makeElement("span", "small text-body-secondary",
        new Date(issue.createdAt).toLocaleDateString()));

    const title = makeElement("h5", "issue-title", issue.title);
    const text = makeElement("p", "issue-text", issue.description);

    // ----- Footer: report button -----
    const footer = makeElement("div", "issue-footer");
    const reportBtn = makeElement("button", "report-btn", "🚩 Report");

    reportBtn.addEventListener("click", async function () {
        if (!confirm("Report this issue as abusive or inappropriate?")) {
            return; // user pressed Cancel
        }
        try {
            await api("/api/issues/" + issue.id + "/report", "POST");
            reportBtn.textContent = "✔ Reported";
            reportBtn.disabled = true;
            showToast("Thanks, your report was received", "ok");
        } catch (error) {
            if (error.status === 409) {
                reportBtn.textContent = "✔ Reported"; // already reported before
                reportBtn.disabled = true;
            }
            showToast(error.message, "error");
        }
    });
    footer.appendChild(reportBtn);

    body.appendChild(meta);
    body.appendChild(title);
    body.appendChild(text);
    body.appendChild(footer);

    card.appendChild(voteCol);
    card.appendChild(body);
    return card;
}

// ---------- Start ----------

// The create page leaves a one-time message in sessionStorage
const flash = sessionStorage.getItem("flash");
if (flash) {
    showToast(flash, "ok");
    sessionStorage.removeItem("flash");
}

buildPills();
loadIssues();