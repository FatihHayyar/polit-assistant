const PAGE_SIZE = 10;
const UPCOMING_AGENDAS_LIMIT = 5;
const LATEST_AFFAIRS_LIMIT = 5;

let currentOffset = 0;
let currentManagementToken = null;
let currentSubscription = null;


// ============================================================
// DOM
// ============================================================

const searchForm =
    document.getElementById("search-form");

const searchInput =
    document.getElementById("search-input");

const topicSelect =
    document.getElementById("topic-select");

const affairsList =
    document.getElementById("affairs-list");

const affairsStatus =
    document.getElementById("affairs-status");

const previousPageButton =
    document.getElementById("previous-page");

const nextPageButton =
    document.getElementById("next-page");

const pageInfo =
    document.getElementById("page-info");

const agendasList =
    document.getElementById("agendas-list");

const agendasStatus =
    document.getElementById("agendas-status");

const latestAffairsList =
    document.getElementById("latest-affairs-list");

const latestAffairsStatus =
    document.getElementById("latest-affairs-status");

const subscriptionStart =
    document.getElementById("subscription-start");

const subscriptionCreate =
    document.getElementById("subscription-create");

const subscriptionManageRequest =
    document.getElementById(
        "subscription-manage-request"
    );

const subscriptionMailSent =
    document.getElementById(
        "subscription-mail-sent"
    );

const subscriptionLoading =
    document.getElementById(
        "subscription-loading"
    );

const subscriptionActive =
    document.getElementById(
        "subscription-active"
    );

const subscriptionEdit =
    document.getElementById(
        "subscription-edit"
    );

const subscriptionResult =
    document.getElementById(
        "subscription-result"
    );

const subscriptionStatus =
    document.getElementById(
        "subscription-status"
    );

const subscriptionForm =
    document.getElementById(
        "subscription-form"
    );

const managementRequestForm =
    document.getElementById(
        "management-request-form"
    );

const subscriptionEditForm =
    document.getElementById(
        "subscription-edit-form"
    );

const subscriptionViews = [
    subscriptionStart,
    subscriptionCreate,
    subscriptionManageRequest,
    subscriptionMailSent,
    subscriptionLoading,
    subscriptionActive,
    subscriptionEdit,
    subscriptionResult
];


// ============================================================
// INITIALIZATION
// ============================================================

async function initializeApplication() {

    applyApplicationLanguage();

    loadAffairs();
    loadAgendas();
    loadLatestAffairs();

    const params =
        new URLSearchParams(
            window.location.search
        );

    const verificationToken =
        params.get("verifyToken");

    const managementToken =
        params.get("manageToken");

    if (verificationToken) {

        await handleVerificationToken(
            verificationToken
        );

        return;
    }

    if (managementToken) {

        await handleManagementToken(
            managementToken
        );

        return;
    }

    showSubscriptionView(
        subscriptionStart
    );
}


// ============================================================
// LANGUAGE
// ============================================================

function initializeLanguageSwitcher() {

    const buttons =
        document.querySelectorAll(
            ".language-button"
        );

    buttons.forEach(button => {

        button.addEventListener(
            "click",
            () => {

                const language =
                    button.dataset.language;

                if (
                    !language ||
                    language ===
                    PolitI18n.getLanguage()
                ) {
                    return;
                }

                PolitI18n.setLanguage(
                    language
                );
            }
        );
    });


    window.addEventListener(
        "polit-language-change",
        () => {

            applyApplicationLanguage();

            /*
             * Dynamic content must be rendered again because
             * dates, topic names and status messages depend
             * on the selected language.
             */
            loadAffairs();
            loadAgendas();
            loadLatestAffairs();

            if (
                currentSubscription &&
                !subscriptionActive
                    ?.classList
                    .contains("hidden")
            ) {
                renderActiveSubscription(
                    currentSubscription
                );
            }
        }
    );
}


function applyApplicationLanguage() {

    document.documentElement.lang =
        PolitI18n.getLanguage();

    PolitI18n.applyStaticTranslations(
        document
    );

    PolitI18n.updateLanguageButtons(
        document
    );

    translateTopicSelectOptions();
}


function translateTopicSelectOptions() {

    if (!topicSelect) {
        return;
    }

    topicSelect
        .querySelectorAll(
            "option[value]"
        )
        .forEach(option => {

            if (!option.value) {
                option.textContent =
                    PolitI18n.t(
                        "allTopics"
                    );

                return;
            }

            option.textContent =
                PolitI18n.topic(
                    option.value
                );
        });
}


// ============================================================
// SUBSCRIPTION VIEW
// ============================================================

function showSubscriptionView(view) {

    for (
        const element
        of subscriptionViews
        ) {

        if (element) {
            element.classList.add(
                "hidden"
            );
        }
    }

    if (view) {
        view.classList.remove(
            "hidden"
        );
    }

    clearSubscriptionStatus();
}


function showSubscriptionError(message) {

    if (!subscriptionStatus) {
        return;
    }

    subscriptionStatus.className =
        "status error";

    subscriptionStatus.textContent =
        message ||
        PolitI18n.t(
            "genericError"
        );

    subscriptionStatus.classList.remove(
        "hidden"
    );
}


function clearSubscriptionStatus() {

    if (!subscriptionStatus) {
        return;
    }

    subscriptionStatus.textContent =
        "";

    subscriptionStatus.className =
        "status hidden";
}


// ============================================================
// VERIFICATION
// ============================================================

async function handleVerificationToken(
    token
) {

    showSubscriptionView(
        subscriptionLoading
    );

    const loadingTitle =
        document.getElementById(
            "subscription-loading-title"
        );

    if (loadingTitle) {

        loadingTitle.textContent =
            PolitI18n.t(
                "subscriptionActivating"
            );
    }

    try {

        const response =
            await fetch(
                `/api/v1/subscriptions/verify?token=${encodeURIComponent(token)}`
            );

        const body =
            await readJsonSafely(
                response
            );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "verificationFailed"
                )
            );
        }

        currentSubscription =
            body;

        removeQueryParameter(
            "verifyToken"
        );

        renderActiveSubscription(
            body
        );

    } catch (error) {

        removeQueryParameter(
            "verifyToken"
        );

        showSubscriptionView(
            subscriptionStart
        );

        showSubscriptionError(
            error.message
        );
    }
}


// ============================================================
// MANAGEMENT TOKEN
// ============================================================

async function handleManagementToken(
    token
) {

    currentManagementToken =
        token;

    showSubscriptionView(
        subscriptionLoading
    );

    const loadingTitle =
        document.getElementById(
            "subscription-loading-title"
        );

    if (loadingTitle) {

        loadingTitle.textContent =
            PolitI18n.t(
                "subscriptionLoading"
            );
    }

    try {

        const response =
            await fetch(
                `/api/v1/subscriptions/manage/${encodeURIComponent(token)}`
            );

        const body =
            await readJsonSafely(
                response
            );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "subscriptionLoadFailed"
                )
            );
        }

        currentSubscription =
            body;

        /*
         * Remove token from URL after it has been read.
         * The token remains only in JavaScript memory.
         */
        removeQueryParameter(
            "manageToken"
        );

        renderActiveSubscription(
            body
        );

    } catch (error) {

        currentManagementToken =
            null;

        removeQueryParameter(
            "manageToken"
        );

        showSubscriptionView(
            subscriptionStart
        );

        showSubscriptionError(
            error.message
        );
    }
}


// ============================================================
// ACTIVE SUBSCRIPTION
// ============================================================

function renderActiveSubscription(
    subscription
) {

    if (!subscription) {
        return;
    }

    currentSubscription =
        subscription;

    const displayName =
        document.getElementById(
            "active-display-name"
        );

    const email =
        document.getElementById(
            "active-email"
        );

    if (displayName) {

        displayName.textContent =
            subscription.displayName ||
            subscription.email ||
            "";
    }

    if (email) {

        email.textContent =
            subscription.email ||
            "";
    }

    const topicsContainer =
        document.getElementById(
            "active-topics"
        );

    if (topicsContainer) {

        topicsContainer.innerHTML =
            "";

        const topics =
            Array.isArray(
                subscription.topics
            )
                ? subscription.topics
                : [];

        for (const topicName of topics) {

            const badge =
                document.createElement(
                    "span"
                );

            badge.className =
                "active-topic";

            badge.textContent =
                PolitI18n.topic(
                    topicName
                );

            topicsContainer.appendChild(
                badge
            );
        }
    }

    showSubscriptionView(
        subscriptionActive
    );
}


// ============================================================
// NEW SUBSCRIPTION
// ============================================================

async function subscribe(event) {

    event.preventDefault();

    clearSubscriptionStatus();

    const email =
        document
            .getElementById(
                "subscription-email"
            )
            .value
            .trim();

    const displayName =
        document
            .getElementById(
                "subscription-name"
            )
            .value
            .trim();

    const topics =
        getCheckedValues(
            "subscription-topic"
        );

    if (topics.length === 0) {

        showSubscriptionError(
            PolitI18n.t(
                "selectAtLeastOne"
            )
        );

        return;
    }

    setFormDisabled(
        subscriptionForm,
        true
    );

    try {

        const response =
            await fetch(
                "/api/v1/subscriptions",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email,
                        displayName:
                            displayName ||
                            null,
                        topics
                    })
                }
            );

        await readJsonSafely(
            response
        );

        if (!response.ok) {

            /*
             * We deliberately do not expose backend details here.
             * This also prevents German backend validation messages
             * from leaking into another UI language.
             */
            throw new Error(
                PolitI18n.t(
                    "subscriptionRequestFailed"
                )
            );
        }

        subscriptionForm.reset();

        const mailText =
            document.getElementById(
                "subscription-mail-sent-text"
            );

        if (mailText) {

            mailText.textContent =
                PolitI18n.t(
                    "subscriptionMailSent"
                );
        }

        showSubscriptionView(
            subscriptionMailSent
        );

    } catch (error) {

        showSubscriptionError(
            error.message
        );

    } finally {

        setFormDisabled(
            subscriptionForm,
            false
        );
    }
}


// ============================================================
// REQUEST MANAGEMENT LINK
// ============================================================

async function requestManagementLink(
    event
) {

    event.preventDefault();

    clearSubscriptionStatus();

    const email =
        document
            .getElementById(
                "management-email"
            )
            .value
            .trim();

    setFormDisabled(
        managementRequestForm,
        true
    );

    try {

        const response =
            await fetch(
                "/api/v1/subscriptions/manage",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email
                    })
                }
            );

        await readJsonSafely(
            response
        );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "requestFailed"
                )
            );
        }

        managementRequestForm.reset();

        const mailText =
            document.getElementById(
                "subscription-mail-sent-text"
            );

        if (mailText) {

            mailText.textContent =
                PolitI18n.t(
                    "managementMailSent"
                );
        }

        showSubscriptionView(
            subscriptionMailSent
        );

    } catch (error) {

        showSubscriptionError(
            error.message
        );

    } finally {

        setFormDisabled(
            managementRequestForm,
            false
        );
    }
}


// ============================================================
// EDIT SUBSCRIPTION
// ============================================================

function openSubscriptionEdit() {

    if (!currentManagementToken) {

        requestManagementLinkForCurrentSubscription();

        return;
    }

    const editEmail =
        document.getElementById(
            "edit-email"
        );

    if (editEmail) {

        editEmail.textContent =
            currentSubscription
                ?.email ||
            "";
    }

    const activeTopics =
        new Set(
            currentSubscription
                ?.topics ||
            []
        );

    document
        .querySelectorAll(
            'input[name="edit-topic"]'
        )
        .forEach(input => {

            input.checked =
                activeTopics.has(
                    input.value
                );
        });

    showSubscriptionView(
        subscriptionEdit
    );
}


async function requestManagementLinkForCurrentSubscription() {

    const email =
        currentSubscription
            ?.email;

    if (!email) {

        showSubscriptionView(
            subscriptionStart
        );

        return;
    }

    showSubscriptionView(
        subscriptionLoading
    );

    const loadingTitle =
        document.getElementById(
            "subscription-loading-title"
        );

    if (loadingTitle) {

        loadingTitle.textContent =
            PolitI18n.t(
                "managementLinkLoading"
            );
    }

    try {

        const response =
            await fetch(
                "/api/v1/subscriptions/manage",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email
                    })
                }
            );

        await readJsonSafely(
            response
        );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "managementLinkFailed"
                )
            );
        }

        const mailText =
            document.getElementById(
                "subscription-mail-sent-text"
            );

        if (mailText) {

            mailText.textContent =
                PolitI18n.t(
                    "secureManagementMailSent"
                );
        }

        showSubscriptionView(
            subscriptionMailSent
        );

    } catch (error) {

        renderActiveSubscription(
            currentSubscription
        );

        showSubscriptionError(
            error.message
        );
    }
}


async function updateSubscription(
    event
) {

    event.preventDefault();

    if (!currentManagementToken) {

        showSubscriptionError(
            PolitI18n.t(
                "managementLinkRequired"
            )
        );

        return;
    }

    const topics =
        getCheckedValues(
            "edit-topic"
        );

    if (topics.length === 0) {

        showSubscriptionError(
            PolitI18n.t(
                "selectAtLeastOne"
            )
        );

        return;
    }

    setFormDisabled(
        subscriptionEditForm,
        true
    );

    try {

        const response =
            await fetch(
                `/api/v1/subscriptions/manage/${encodeURIComponent(currentManagementToken)}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        topics
                    })
                }
            );

        const body =
            await readJsonSafely(
                response
            );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "updateFailed"
                )
            );
        }

        currentSubscription =
            body;

        const resultTitle =
            document.getElementById(
                "subscription-result-title"
            );

        const resultText =
            document.getElementById(
                "subscription-result-text"
            );

        if (resultTitle) {

            resultTitle.textContent =
                PolitI18n.t(
                    "subscriptionUpdated"
                );
        }

        if (resultText) {

            resultText.textContent =
                PolitI18n.t(
                    "subscriptionUpdatedText"
                );
        }

        showSubscriptionView(
            subscriptionResult
        );

    } catch (error) {

        showSubscriptionError(
            error.message
        );

    } finally {

        setFormDisabled(
            subscriptionEditForm,
            false
        );
    }
}


// ============================================================
// DELETE SUBSCRIPTION
// ============================================================

async function deleteSubscription() {

    if (!currentManagementToken) {

        await requestManagementLinkForCurrentSubscription();

        return;
    }

    const confirmed =
        window.confirm(
            PolitI18n.t(
                "deleteConfirmation"
            )
        );

    if (!confirmed) {
        return;
    }

    const deleteButton =
        document.getElementById(
            "delete-subscription-button"
        );

    if (deleteButton) {
        deleteButton.disabled =
            true;
    }

    try {

        const response =
            await fetch(
                `/api/v1/subscriptions/manage/${encodeURIComponent(currentManagementToken)}`,
                {
                    method: "DELETE"
                }
            );

        await readJsonSafely(
            response
        );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "deleteFailed"
                )
            );
        }

        currentManagementToken =
            null;

        currentSubscription =
            null;

        removeQueryParameter(
            "manageToken"
        );

        const resultTitle =
            document.getElementById(
                "subscription-result-title"
            );

        const resultText =
            document.getElementById(
                "subscription-result-text"
            );

        if (resultTitle) {

            resultTitle.textContent =
                PolitI18n.t(
                    "subscriptionEnded"
                );
        }

        if (resultText) {

            resultText.textContent =
                PolitI18n.t(
                    "subscriptionEndedText"
                );
        }

        showSubscriptionView(
            subscriptionResult
        );

    } catch (error) {

        showSubscriptionError(
            error.message
        );

    } finally {

        if (deleteButton) {

            deleteButton.disabled =
                false;
        }
    }
}


// ============================================================
// AFFAIRS
// ============================================================

async function loadAffairs() {

    if (
        !affairsStatus ||
        !affairsList
    ) {
        return;
    }

    affairsStatus.className =
        "status";

    affairsStatus.textContent =
        PolitI18n.t(
            "affairsLoading"
        );

    affairsList.innerHTML =
        "";

    const params =
        new URLSearchParams();

    const query =
        searchInput
            ?.value
            ?.trim() ||
        "";

    const topic =
        topicSelect
            ?.value ||
        "";

    if (query) {

        params.set(
            "q",
            query
        );
    }

    if (topic) {

        params.set(
            "topic",
            topic
        );
    }

    params.set(
        "limit",
        PAGE_SIZE
    );

    params.set(
        "offset",
        currentOffset
    );

    try {

        const response =
            await fetch(
                `/api/v1/affairs?${params.toString()}`
            );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "affairsLoadError"
                )
            );
        }

        const affairs =
            await response.json();

        renderAffairs(
            affairs
        );

        const currentPage =
            Math.floor(
                currentOffset /
                PAGE_SIZE
            ) + 1;

        if (pageInfo) {

            pageInfo.textContent =
                PolitI18n.t(
                    "page",
                    {
                        page:
                        currentPage
                    }
                );
        }

        if (previousPageButton) {

            previousPageButton.disabled =
                currentOffset === 0;
        }

        if (nextPageButton) {

            nextPageButton.disabled =
                affairs.length <
                PAGE_SIZE;
        }

        affairsStatus.textContent =
            affairs.length === 0
                ? PolitI18n.t(
                    "noAffairs"
                )
                : PolitI18n.t(
                    "affairsOnPage",
                    {
                        count:
                        affairs.length
                    }
                );

    } catch (error) {

        affairsStatus.className =
            "status error";

        affairsStatus.textContent =
            error.message;
    }
}


function renderAffairs(affairs) {

    if (!affairsList) {
        return;
    }

    affairsList.innerHTML =
        "";

    for (const affair of affairs) {

        const item =
            document.createElement(
                "article"
            );

        item.className =
            "result-item";

        makeAffairCardClickable(
            item,
            affair.id
        );


        // TITLE

        const title =
            document.createElement(
                "h3"
            );

        title.textContent =
            affair.title ||
            PolitI18n.t(
                "affairFallback",
                {
                    id:
                    affair.id
                }
            );

        item.appendChild(
            title
        );


        // META

        const meta =
            document.createElement(
                "div"
            );

        meta.className =
            "result-meta";

        if (affair.topic) {

            const topic =
                document.createElement(
                    "span"
                );

            topic.className =
                "topic-badge";

            topic.textContent =
                PolitI18n.topic(
                    affair.topic
                );

            meta.appendChild(
                topic
            );
        }

        if (affair.type) {

            appendMeta(
                meta,
                affair.type
            );
        }

        if (affair.state) {

            appendMeta(
                meta,
                affair.state
            );
        }

        if (affair.beginDate) {

            appendMeta(
                meta,
                formatDate(
                    affair.beginDate
                )
            );
        }

        item.appendChild(
            meta
        );


        // DETAIL HINT

        const hint =
            document.createElement(
                "span"
            );

        hint.className =
            "external-link";

        hint.textContent =
            PolitI18n.t(
                "details"
            );

        item.appendChild(
            hint
        );

        affairsList.appendChild(
            item
        );
    }
}


// ============================================================
// UPCOMING AGENDAS
// ============================================================

async function loadAgendas() {

    if (
        !agendasList ||
        !agendasStatus
    ) {
        return;
    }

    agendasStatus.className =
        "status";

    agendasStatus.textContent =
        PolitI18n.t(
            "agendasLoading"
        );

    agendasList.innerHTML =
        "";

    try {

        const response =
            await fetch(
                `/api/v1/agendas/relevant?limit=${UPCOMING_AGENDAS_LIMIT}&offset=0`
            );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "agendasLoadError"
                )
            );
        }

        const agendas =
            await response.json();

        renderAgendas(
            agendas
        );

        agendasStatus.textContent =
            agendas.length === 0
                ? PolitI18n.t(
                    "noAgendas"
                )
                : PolitI18n.t(
                    "upcomingAgendaCount",
                    {
                        count:
                        agendas.length
                    }
                );

    } catch (error) {

        agendasStatus.className =
            "status error";

        agendasStatus.textContent =
            error.message;
    }
}


function renderAgendas(agendas) {

    if (!agendasList) {
        return;
    }

    agendasList.innerHTML =
        "";

    for (const agenda of agendas) {

        const item =
            document.createElement(
                "article"
            );

        item.className =
            "result-item monitoring-item";

        if (agenda.affairId) {

            makeAffairCardClickable(
                item,
                agenda.affairId
            );
        }


        // DATE

        const dateValue =
            agenda.itemDate ||
            agenda.meetingBeginDate;

        if (dateValue) {

            const date =
                document.createElement(
                    "div"
                );

            date.className =
                "monitoring-date";

            date.textContent =
                formatDate(
                    dateValue
                );

            item.appendChild(
                date
            );
        }


        // TITLE

        const title =
            document.createElement(
                "h3"
            );

        title.textContent =
            agenda.affairTitle ||
            agenda.itemTitle ||
            PolitI18n.t(
                "agenda"
            );

        item.appendChild(
            title
        );


        // DESCRIPTION

        if (
            agenda.itemTitle &&
            agenda.affairTitle &&
            agenda.itemTitle !==
            agenda.affairTitle
        ) {

            const agendaTitle =
                document.createElement(
                    "p"
                );

            agendaTitle.className =
                "monitoring-description";

            agendaTitle.textContent =
                agenda.itemTitle;

            item.appendChild(
                agendaTitle
            );
        }


        // META

        const meta =
            document.createElement(
                "div"
            );

        meta.className =
            "result-meta";

        if (agenda.itemNumber) {

            appendMeta(
                meta,
                PolitI18n.t(
                    "agendaWithNumber",
                    {
                        number:
                        agenda.itemNumber
                    }
                )
            );
        }

        if (agenda.meetingName) {

            appendMeta(
                meta,
                agenda.meetingName
            );
        }

        if (
            Array.isArray(
                agenda.topics
            )
        ) {

            for (
                const topicName
                of agenda.topics
                ) {

                const topic =
                    document.createElement(
                        "span"
                    );

                topic.className =
                    "topic-badge";

                topic.textContent =
                    PolitI18n.topic(
                        topicName
                    );

                meta.appendChild(
                    topic
                );
            }
        }

        item.appendChild(
            meta
        );


        // DETAIL HINT

        if (agenda.affairId) {

            const hint =
                document.createElement(
                    "span"
                );

            hint.className =
                "external-link";

            hint.textContent =
                PolitI18n.t(
                    "viewAffair"
                );

            item.appendChild(
                hint
            );
        }

        agendasList.appendChild(
            item
        );
    }
}


// ============================================================
// LATEST AFFAIRS
// ============================================================

async function loadLatestAffairs() {

    if (
        !latestAffairsList ||
        !latestAffairsStatus
    ) {
        return;
    }

    latestAffairsStatus.className =
        "status";

    latestAffairsStatus.textContent =
        PolitI18n.t(
            "newLoading"
        );

    latestAffairsList.innerHTML =
        "";

    try {

        /*
         * More than five records are requested because
         * SONSTIGES is not displayed as a relevant WWF topic.
         */
        const response =
            await fetch(
                "/api/v1/affairs?limit=30&offset=0"
            );

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "newLoadError"
                )
            );
        }

        const affairs =
            await response.json();

        const relevantAffairs =
            affairs
                .filter(affair =>
                    affair.topic &&
                    affair.topic !==
                    "Sonstiges" &&
                    affair.topic !==
                    "SONSTIGES"
                )
                .slice(
                    0,
                    LATEST_AFFAIRS_LIMIT
                );

        renderLatestAffairs(
            relevantAffairs
        );

        latestAffairsStatus.textContent =
            relevantAffairs.length === 0
                ? PolitI18n.t(
                    "noNewAffairs"
                )
                : PolitI18n.t(
                    "newAffairCount",
                    {
                        count:
                        relevantAffairs.length
                    }
                );

    } catch (error) {

        latestAffairsStatus.className =
            "status error";

        latestAffairsStatus.textContent =
            error.message;
    }
}


function renderLatestAffairs(
    affairs
) {

    if (!latestAffairsList) {
        return;
    }

    latestAffairsList.innerHTML =
        "";

    for (const affair of affairs) {

        const item =
            document.createElement(
                "article"
            );

        item.className =
            "result-item monitoring-item";

        makeAffairCardClickable(
            item,
            affair.id
        );


        // DATE

        if (affair.beginDate) {

            const date =
                document.createElement(
                    "div"
                );

            date.className =
                "monitoring-date";

            date.textContent =
                formatDate(
                    affair.beginDate
                );

            item.appendChild(
                date
            );
        }


        // TITLE

        const title =
            document.createElement(
                "h3"
            );

        title.textContent =
            affair.title ||
            PolitI18n.t(
                "affairFallback",
                {
                    id:
                    affair.id
                }
            );

        item.appendChild(
            title
        );


        // META

        const meta =
            document.createElement(
                "div"
            );

        meta.className =
            "result-meta";

        if (affair.topic) {

            const topic =
                document.createElement(
                    "span"
                );

            topic.className =
                "topic-badge";

            topic.textContent =
                PolitI18n.topic(
                    affair.topic
                );

            meta.appendChild(
                topic
            );
        }

        if (affair.type) {

            appendMeta(
                meta,
                affair.type
            );
        }

        item.appendChild(
            meta
        );


        // DETAIL HINT

        const hint =
            document.createElement(
                "span"
            );

        hint.className =
            "external-link";

        hint.textContent =
            PolitI18n.t(
                "details"
            );

        item.appendChild(
            hint
        );

        latestAffairsList.appendChild(
            item
        );
    }
}


// ============================================================
// INTERNAL AFFAIR NAVIGATION
// ============================================================

function makeAffairCardClickable(
    element,
    affairId
) {

    if (
        !element ||
        affairId === null ||
        affairId === undefined
    ) {
        return;
    }

    const url =
        `/affair.html?id=${encodeURIComponent(affairId)}`;

    element.classList.add(
        "clickable-card"
    );

    element.setAttribute(
        "role",
        "link"
    );

    element.setAttribute(
        "tabindex",
        "0"
    );

    element.addEventListener(
        "click",
        () => {

            window.location.href =
                url;
        }
    );

    element.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Enter" ||
                event.key === " "
            ) {

                event.preventDefault();

                window.location.href =
                    url;
            }
        }
    );
}


// ============================================================
// HELPERS
// ============================================================

function getCheckedValues(name) {

    return Array.from(
        document.querySelectorAll(
            `input[name="${name}"]:checked`
        )
    ).map(
        input =>
            input.value
    );
}


function appendMeta(
    container,
    value
) {

    if (
        !container ||
        value === null ||
        value === undefined ||
        value === ""
    ) {
        return;
    }

    const element =
        document.createElement(
            "span"
        );

    element.textContent =
        value;

    container.appendChild(
        element
    );
}


function setFormDisabled(
    form,
    disabled
) {

    if (!form) {
        return;
    }

    form
        .querySelectorAll(
            "input, select, button"
        )
        .forEach(element => {

            element.disabled =
                disabled;
        });
}


async function readJsonSafely(
    response
) {

    const contentType =
        response.headers.get(
            "content-type"
        );

    if (
        contentType &&
        contentType.includes(
            "application/json"
        )
    ) {

        return await response.json();
    }

    return null;
}


function removeQueryParameter(
    parameterName
) {

    const url =
        new URL(
            window.location.href
        );

    url.searchParams.delete(
        parameterName
    );

    const query =
        url.searchParams.toString();

    const cleanUrl =
        url.pathname +
        (query ? `?${query}` : "") +
        url.hash;

    window.history.replaceState(
        {},
        document.title,
        cleanUrl
    );
}


function formatDate(value) {

    return PolitI18n.formatDate(
        value
    );
}


// ============================================================
// EVENTS
// ============================================================

document
    .getElementById(
        "show-subscribe-button"
    )
    ?.addEventListener(
        "click",
        () => {

            subscriptionForm
                ?.reset();

            showSubscriptionView(
                subscriptionCreate
            );
        }
    );


document
    .getElementById(
        "show-manage-button"
    )
    ?.addEventListener(
        "click",
        () => {

            managementRequestForm
                ?.reset();

            showSubscriptionView(
                subscriptionManageRequest
            );
        }
    );


document
    .getElementById(
        "cancel-subscribe-button"
    )
    ?.addEventListener(
        "click",
        () => {

            subscriptionForm
                ?.reset();

            showSubscriptionView(
                subscriptionStart
            );
        }
    );


document
    .getElementById(
        "cancel-manage-button"
    )
    ?.addEventListener(
        "click",
        () => {

            managementRequestForm
                ?.reset();

            showSubscriptionView(
                subscriptionStart
            );
        }
    );


document
    .getElementById(
        "mail-sent-home-button"
    )
    ?.addEventListener(
        "click",
        () => {

            showSubscriptionView(
                subscriptionStart
            );
        }
    );


document
    .getElementById(
        "edit-subscription-button"
    )
    ?.addEventListener(
        "click",
        openSubscriptionEdit
    );


document
    .getElementById(
        "delete-subscription-button"
    )
    ?.addEventListener(
        "click",
        deleteSubscription
    );


document
    .getElementById(
        "cancel-edit-button"
    )
    ?.addEventListener(
        "click",
        () => {

            renderActiveSubscription(
                currentSubscription
            );
        }
    );


document
    .getElementById(
        "subscription-result-home-button"
    )
    ?.addEventListener(
        "click",
        () => {

            removeQueryParameter(
                "manageToken"
            );

            currentManagementToken =
                null;

            currentSubscription =
                null;

            showSubscriptionView(
                subscriptionStart
            );
        }
    );


subscriptionForm
    ?.addEventListener(
        "submit",
        subscribe
    );


managementRequestForm
    ?.addEventListener(
        "submit",
        requestManagementLink
    );


subscriptionEditForm
    ?.addEventListener(
        "submit",
        updateSubscription
    );


searchForm
    ?.addEventListener(
        "submit",
        event => {

            event.preventDefault();

            currentOffset =
                0;

            loadAffairs();
        }
    );


previousPageButton
    ?.addEventListener(
        "click",
        () => {

            currentOffset =
                Math.max(
                    0,
                    currentOffset -
                    PAGE_SIZE
                );

            loadAffairs();
        }
    );


nextPageButton
    ?.addEventListener(
        "click",
        () => {

            currentOffset +=
                PAGE_SIZE;

            loadAffairs();
        }
    );


// ============================================================
// START
// ============================================================

initializeLanguageSwitcher();
initializeApplication();