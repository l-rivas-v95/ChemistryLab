export function formatPropertyShort(value, maxLength = 48) {
    const clean = cleanText(value);

    if (!clean) {
        return "N/A";
    }

    const first = getFirstUsefulPart(clean);

    if (first.length <= maxLength) {
        return first;
    }

    return `${first.slice(0, maxLength).trim()}...`;
}

export function formatDescription(value, maxLength = 520) {
    const clean = cleanText(value);

    if (!clean) {
        return "Sin descripción disponible.";
    }

    if (clean.length <= maxLength) {
        return clean;
    }

    return `${clean.slice(0, maxLength).trim()}...`;
}

export function hasValue(value) {
    return value !== null && value !== undefined && String(value).trim() !== "";
}

export function cleanText(value) {
    return String(value || "")
        .replace(/\s+/g, " ")
        .replace(/\s+\|\s+/g, " | ")
        .trim();
}

function getFirstUsefulPart(text) {
    const parts = String(text || "")
        .split("|")
        .map((item) => item.trim())
        .filter(Boolean);

    const first = parts[0] || text;

    return removeExtraParenthetical(first);
}

function removeExtraParenthetical(text) {
    let clean = String(text || "").trim();

    // Mantiene cosas cortas tipo "(NIOSH, 2024)", pero corta explicaciones enormes.
    clean = clean.replace(/\(([^)]{45,})\)/g, "").trim();

    // Si aun así queda demasiado largo con " - " o "; ", se queda con el primer dato.
    clean = clean.split(" - ")[0].trim();
    clean = clean.split(";")[0].trim();

    return clean;
}