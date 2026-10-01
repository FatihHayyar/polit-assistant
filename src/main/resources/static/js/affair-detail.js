const CONTENT_PREVIEW_LENGTH = 900;

let currentAffair = null;
let contentExpanded = false;


// ============================================================
// DOM
// ============================================================

const affairDetailStatus =
    document.getElementById(
        "affair-detail-status"
    );

const affairDetail =
    document.getElementById(
        "affair-detail"
    );

const affairTitle =
    document.getElementById(
        "affair-title"
    );

const affairTitleLong =
    document.getElementById(
        "affair-title-long"
    );

const affairMeta =
    document.getElementById(
        "affair-meta"
    );

const affairContent =
    document.getElementById(
        "affair-content"
    );

const toggleContentButton =
    document.getElementById(
        "toggle-content-button"
    );

const affairTopics =
    document.getElementById(
        "affair-topics"
    );

const affairDocuments =
    document.getElementById(
        "affair-documents"
    );

const affairContentSection =
    document.getElementById(
        "affair-content-section"
    );

const affairTopicsSection =
    document.getElementById(
        "affair-topics-section"
    );

const affairDocumentsSection =
    document.getElementById(
        "affair-documents-section"
    );

const affairSourceLink =
    document.getElementById(
        "affair-source-link"
    );

const affairSourceLinkBottom =
    document.getElementById(
        "affair-source-link-bottom"
    );


// ============================================================
// INITIALIZATION
// ============================================================

async function initializeAffairDetail() {

    initializeLanguageSwitcher();

    applyApplicationLanguage();

    const affairId =
        getAffairId();

    if (!affairId) {

        showError(
            PolitI18n.t(
                "affairNotFound"
            )
        );

        return;
    }

    await loadAffair(
        affairId
    );
}


// ============================================================
// LANGUAGE
// ============================================================

function initializeLanguageSwitcher() {

    document
        .querySelectorAll(
            ".language-button"
        )
        .forEach(button => {

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
             * At this stage parliamentary source content itself
             * remains unchanged. We only re-render UI labels,
             * dates and WWF topic names.
             *
             * Later this is where the translated backend response
             * can be requested.
             */
            if (currentAffair) {

                renderAffair(
                    currentAffair
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
}


// ============================================================
// LOAD AFFAIR
// ============================================================

async function loadAffair(
    affairId
) {

    showLoading();

    try {

        const response =
            await fetch(
                `/api/v1/affairs/${encodeURIComponent(affairId)}`
            );

        if (response.status === 404) {

            throw new Error(
                PolitI18n.t(
                    "affairNotFound"
                )
            );
        }

        if (!response.ok) {

            throw new Error(
                PolitI18n.t(
                    "affairDetailLoadError"
                )
            );
        }

        const affair =
            await response.json();

        currentAffair =
            affair;

        contentExpanded =
            false;

        renderAffair(
            affair
        );

        affairDetailStatus.classList.add(
            "hidden"
        );

        affairDetail.classList.remove(
            "hidden"
        );

    } catch (error) {

        showError(
            error.message ||
            PolitI18n.t(
                "affairDetailLoadError"
            )
        );
    }
}


// ============================================================
// RENDER AFFAIR
// ============================================================

function renderAffair(
    affair
) {

    if (!affair) {
        return;
    }

    renderTitle(
        affair
    );

    renderMeta(
        affair
    );

    renderSourceLinks(
        affair
    );

    renderTopics(
        affair.topics
    );

    renderContent(
        affair.documents
    );

    renderDocuments(
        affair.documents
    );
}


// ============================================================
// TITLE
// ============================================================

function renderTitle(
    affair
) {

    const title =
        cleanText(
            affair.title
        ) ||
        PolitI18n.t(
            "affairFallback",
            {
                id:
                affair.id
            }
        );

    affairTitle.textContent =
        title;


    const longTitle =
        cleanText(
            affair.titleLong
        );

    if (
        longTitle &&
        longTitle !== title
    ) {

        affairTitleLong.textContent =
            longTitle;

        affairTitleLong.classList.remove(
            "hidden"
        );

    } else {

        affairTitleLong.textContent =
            "";

        affairTitleLong.classList.add(
            "hidden"
        );
    }


    document.title =
        `${title} – WWF Polit-Assistant`;
}


// ============================================================
// META
// ============================================================

function renderMeta(
    affair
) {

    affairMeta.innerHTML =
        "";

    if (affair.number) {

        appendMetaItem(
            affairMeta,
            PolitI18n.t(
                "number"
            ),
            affair.number
        );
    }

    if (affair.type) {

        appendMetaItem(
            affairMeta,
            PolitI18n.t(
                "type"
            ),
            affair.type
        );
    }

    if (affair.state) {

        appendMetaItem(
            affairMeta,
            PolitI18n.t(
                "state"
            ),
            affair.state
        );
    }

    if (affair.beginDate) {

        appendMetaItem(
            affairMeta,
            PolitI18n.t(
                "beginDate"
            ),
            PolitI18n.formatDate(
                affair.beginDate
            )
        );
    }

    if (affair.endDate) {

        appendMetaItem(
            affairMeta,
            PolitI18n.t(
                "endDate"
            ),
            PolitI18n.formatDate(
                affair.endDate
            )
        );
    }
}


function appendMetaItem(
    container,
    label,
    value
) {

    if (
        value === null ||
        value === undefined ||
        value === ""
    ) {
        return;
    }

    const item =
        document.createElement(
            "span"
        );

    item.className =
        "detail-meta-item";


    const labelElement =
        document.createElement(
            "strong"
        );

    labelElement.textContent =
        `${label}: `;


    const valueElement =
        document.createElement(
            "span"
        );

    valueElement.textContent =
        value;


    item.appendChild(
        labelElement
    );

    item.appendChild(
        valueElement
    );

    container.appendChild(
        item
    );
}


// ============================================================
// SOURCE LINKS
// ============================================================

function renderSourceLinks(
    affair
) {

    const sourceUrl =
        cleanUrl(
            affair.urlExternal
        );

    configureExternalLink(
        affairSourceLink,
        sourceUrl
    );

    configureExternalLink(
        affairSourceLinkBottom,
        sourceUrl
    );
}


function configureExternalLink(
    element,
    url
) {

    if (!element) {
        return;
    }

    if (!url) {

        element.classList.add(
            "hidden"
        );

        element.removeAttribute(
            "href"
        );

        return;
    }

    element.href =
        url;

    element.classList.remove(
        "hidden"
    );
}


// ============================================================
// WWF TOPICS
// ============================================================

function renderTopics(
    topics
) {

    affairTopics.innerHTML =
        "";

    const relevantTopics =
        Array.isArray(topics)
            ? topics.filter(
                topic =>
                    getTopicName(topic) &&
                    !isOtherTopic(
                        getTopicName(topic)
                    )
            )
            : [];

    if (
        relevantTopics.length === 0
    ) {

        const empty =
            document.createElement(
                "p"
            );

        empty.className =
            "detail-empty";

        empty.textContent =
            PolitI18n.t(
                "noRelevantTopics"
            );

        affairTopics.appendChild(
            empty
        );

        return;
    }


    for (
        const topic
        of relevantTopics
        ) {

        const topicName =
            getTopicName(
                topic
            );

        const badge =
            document.createElement(
                "span"
            );

        badge.className =
            "topic-badge";

        badge.textContent =
            PolitI18n.topic(
                topicName
            );

        affairTopics.appendChild(
            badge
        );
    }
}


function getTopicName(
    topic
) {

    if (!topic) {
        return null;
    }

    if (
        typeof topic ===
        "string"
    ) {
        return topic;
    }

    return (
        topic.name ||
        topic.topic ||
        null
    );
}


function isOtherTopic(
    topicName
) {

    if (!topicName) {
        return false;
    }

    const normalized =
        String(topicName)
            .trim()
            .toUpperCase();

    return (
        normalized ===
        "SONSTIGES"
    );
}


// ============================================================
// AFFAIR CONTENT
// ============================================================

function renderContent(
    documents
) {

    affairContent.innerHTML =
        "";

    const content =
        findBestDocumentContent(
            documents
        );

    if (!content) {

        const empty =
            document.createElement(
                "p"
            );

        empty.className =
            "detail-empty";

        empty.textContent =
            PolitI18n.t(
                "noAffairContent"
            );

        affairContent.appendChild(
            empty
        );

        toggleContentButton.classList.add(
            "hidden"
        );

        return;
    }


    const textElement =
        document.createElement(
            "p"
        );

    textElement.className =
        "detail-content-body";


    if (
        content.length <=
        CONTENT_PREVIEW_LENGTH
    ) {

        textElement.textContent =
            content;

        toggleContentButton.classList.add(
            "hidden"
        );

    } else {

        textElement.textContent =
            contentExpanded
                ? content
                : createPreview(
                    content
                );

        toggleContentButton.textContent =
            contentExpanded
                ? PolitI18n.t(
                    "showLess"
                )
                : PolitI18n.t(
                    "readMore"
                );

        toggleContentButton.classList.remove(
            "hidden"
        );
    }

    affairContent.appendChild(
        textElement
    );
}


function findBestDocumentContent(
    documents
) {

    if (
        !Array.isArray(documents) ||
        documents.length === 0
    ) {
        return null;
    }

    /*
     * Prefer the longest available imported document text.
     * For the MVP this normally gives the user the most useful
     * parliamentary content instead of technical metadata.
     */
    const candidates =
        documents
            .map(document => ({
                document,
                text:
                    cleanText(
                        document.text
                    )
            }))
            .filter(
                candidate =>
                    candidate.text
            )
            .sort(
                (left, right) =>
                    right.text.length -
                    left.text.length
            );

    if (
        candidates.length === 0
    ) {
        return null;
    }

    return candidates[0].text;
}


function createPreview(
    content
) {

    if (
        content.length <=
        CONTENT_PREVIEW_LENGTH
    ) {
        return content;
    }

    let preview =
        content.substring(
            0,
            CONTENT_PREVIEW_LENGTH
        );

    /*
     * Avoid cutting the preview in the middle of a word.
     */
    const lastSpace =
        preview.lastIndexOf(
            " "
        );

    if (
        lastSpace >
        CONTENT_PREVIEW_LENGTH * 0.8
    ) {

        preview =
            preview.substring(
                0,
                lastSpace
            );
    }

    return `${preview.trim()}…`;
}


// ============================================================
// DOCUMENTS
// ============================================================

function renderDocuments(
    documents
) {

    affairDocuments.innerHTML =
        "";

    if (
        !Array.isArray(documents) ||
        documents.length === 0
    ) {

        const empty =
            document.createElement(
                "p"
            );

        empty.className =
            "detail-empty";

        empty.textContent =
            PolitI18n.t(
                "noDocuments"
            );

        affairDocuments.appendChild(
            empty
        );

        return;
    }


    for (
        const document
        of documents
        ) {

        affairDocuments.appendChild(
            createDocumentCard(
                document
            )
        );
    }
}


function createDocumentCard(
    affairDocument
) {

    const card =
        window.document.createElement(
            "article"
        );

    card.className =
        "document-card";


    // TITLE

    const title =
        documentElement(
            "h3",
            cleanText(
                affairDocument.name
            ) ||
            PolitI18n.t(
                "document"
            )
        );

    card.appendChild(
        title
    );


    // META

    const meta =
        window.document.createElement(
            "div"
        );

    meta.className =
        "result-meta";


    if (affairDocument.date) {

        appendMetaItem(
            meta,
            PolitI18n.t(
                "documentDate"
            ),
            PolitI18n.formatDate(
                affairDocument.date
            )
        );
    }


    if (affairDocument.language) {

        appendMetaItem(
            meta,
            PolitI18n.t(
                "documentLanguage"
            ),
            formatDocumentLanguage(
                affairDocument.language
            )
        );
    }


    if (affairDocument.format) {

        appendMetaItem(
            meta,
            PolitI18n.t(
                "documentFormat"
            ),
            String(
                affairDocument.format
            ).toUpperCase()
        );
    }


    if (
        meta.childElementCount >
        0
    ) {

        card.appendChild(
            meta
        );
    }


    // LINK

    const documentUrl =
        cleanUrl(
            affairDocument.url
        );

    if (documentUrl) {

        const link =
            window.document.createElement(
                "a"
            );

        link.className =
            "external-link";

        link.href =
            documentUrl;

        link.target =
            "_blank";

        link.rel =
            "noopener noreferrer";

        link.textContent =
            PolitI18n.t(
                "originalDocument"
            );

        card.appendChild(
            link
        );
    }


    return card;
}


function documentElement(
    tag,
    text
) {

    const element =
        window.document.createElement(
            tag
        );

    element.textContent =
        text;

    return element;
}


// ============================================================
// DOCUMENT LANGUAGE
// ============================================================

function formatDocumentLanguage(
    value
) {

    if (!value) {
        return "";
    }

    const normalized =
        String(value)
            .trim()
            .toLowerCase();

    const languageNames = {

        de: {
            de: "Deutsch",
            fr: "Französisch",
            it: "Italienisch",
            en: "Englisch"
        },

        fr: {
            de: "Allemand",
            fr: "Français",
            it: "Italien",
            en: "Anglais"
        },

        it: {
            de: "Tedesco",
            fr: "Francese",
            it: "Italiano",
            en: "Inglese"
        },

        en: {
            de: "German",
            fr: "French",
            it: "Italian",
            en: "English"
        }
    };


    const aliases = {

        de:
            "de",

        deu:
            "de",

        ger:
            "de",

        deutsch:
            "de",

        fr:
            "fr",

        fra:
            "fr",

        fre:
            "fr",

        français:
            "fr",

        francais:
            "fr",

        it:
            "it",

        ita:
            "it",

        italiano:
            "it",

        en:
            "en",

        eng:
            "en",

        english:
            "en"
    };


    const languageCode =
        aliases[normalized];

    if (!languageCode) {

        return value;
    }

    const uiLanguage =
        PolitI18n.getLanguage();

    return (
        languageNames[uiLanguage]
            ?.[languageCode]
        ||
        value
    );
}


// ============================================================
// CONTENT TOGGLE
// ============================================================

function toggleContent() {

    if (!currentAffair) {
        return;
    }

    contentExpanded =
        !contentExpanded;

    renderContent(
        currentAffair.documents
    );

    if (!contentExpanded) {

        affairContentSection
            ?.scrollIntoView({
                behavior:
                    "smooth",

                block:
                    "start"
            });
    }
}


// ============================================================
// STATUS
// ============================================================

function showLoading() {

    affairDetail.classList.add(
        "hidden"
    );

    affairDetailStatus.className =
        "status";

    affairDetailStatus.textContent =
        PolitI18n.t(
            "affairDetailLoading"
        );
}


function showError(
    message
) {

    affairDetail.classList.add(
        "hidden"
    );

    affairDetailStatus.className =
        "status error";

    affairDetailStatus.textContent =
        message ||
        PolitI18n.t(
            "affairDetailLoadError"
        );
}


// ============================================================
// URL / TEXT HELPERS
// ============================================================

function getAffairId() {

    const params =
        new URLSearchParams(
            window.location.search
        );

    const value =
        params.get(
            "id"
        );

    if (!value) {
        return null;
    }

    const id =
        Number(value);

    if (
        !Number.isInteger(id) ||
        id <= 0
    ) {
        return null;
    }

    return id;
}


function cleanText(
    value
) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)
        .replace(
            /\u0000/g,
            ""
        )
        .replace(
            /\r\n/g,
            "\n"
        )
        .replace(
            /\r/g,
            "\n"
        )
        .replace(
            /[ \t]+/g,
            " "
        )
        .replace(
            /\n[ \t]+/g,
            "\n"
        )
        .replace(
            /\n{3,}/g,
            "\n\n"
        )
        .trim();
}


function cleanUrl(
    value
) {

    if (!value) {
        return null;
    }

    try {

        const url =
            new URL(
                value,
                window.location.origin
            );

        if (
            url.protocol !== "http:" &&
            url.protocol !== "https:"
        ) {

            return null;
        }

        return url.href;

    } catch (error) {

        return null;
    }
}


// ============================================================
// EVENTS
// ============================================================

toggleContentButton
    ?.addEventListener(
        "click",
        toggleContent
    );


// ============================================================
// START
// ============================================================

initializeAffairDetail();