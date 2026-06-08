export function getMoleculeCategoryClass(molecula) {
    const formulaCategory = getCategoryFromFormula(molecula?.formula);
    if (formulaCategory !== "unknown") {
        return formulaCategory;
    }

    const tipo = normalizeText(
        molecula?.tipoCompuesto ||
        molecula?.tipo_compuesto ||
        molecula?.tipo ||
        ""
    );

    return getCategoryFromType(tipo) || "unknown";
}

export function getTipoVisible(molecula) {
    const formulaCategory = getCategoryFromFormula(molecula?.formula);
    if (formulaCategory !== "unknown") {
        return getVisibleTypeFromCategory(formulaCategory);
    }

    const tipo =
        molecula?.tipoCompuesto ||
        molecula?.tipo_compuesto ||
        molecula?.tipo;

    if (tipo) {
        return tipo;
    }

    return "Sin clasificar";
}

export function normalizeText(value) {
    return String(value || "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}

function getCategoryFromType(tipo) {
    if (!tipo) {
        return null;
    }

    if (tipo.includes("acido")) {
        return "acid";
    }

    if (tipo.includes("base") || tipo.includes("hidroxido")) {
        return "base";
    }

    if (tipo.includes("oxido")) {
        return "oxide";
    }

    if (tipo.includes("sal")) {
        return "salt";
    }

    if (tipo.includes("inorganica") || tipo.includes("inorganico")) {
        return "inorganic";
    }

    if (tipo.includes("organica") || tipo.includes("organico")) {
        return "organic";
    }

    return null;
}

function getCategoryFromFormula(formula) {
    const cleanFormula = normalizeFormula(formula);

    if (!cleanFormula) {
        return "unknown";
    }

    if (isHydroxide(cleanFormula)) {
        return "base";
    }

    if (isOxide(cleanFormula)) {
        return "oxide";
    }

    if (isInorganicCarbonFormula(cleanFormula)) {
        return "inorganic";
    }

    if (containsElement(cleanFormula, "C")) {
        return "organic";
    }

    return "inorganic";
}

function getVisibleTypeFromCategory(category) {
    switch (category) {
        case "organic":
            return "Orgánica";
        case "inorganic":
            return "Inorgánica";
        case "acid":
            return "Ácido";
        case "base":
            return "Base";
        case "oxide":
            return "Óxido";
        case "salt":
            return "Sal";
        default:
            return "Sin clasificar";
    }
}

function normalizeFormula(formula) {
    return String(formula || "")
        .replace(/[·.].*$/g, "")
        .replace(/\s+/g, "")
        .trim();
}

function containsElement(formula, symbol) {
    const regex = new RegExp(`(^|[^a-zA-Z])${symbol}(?![a-z])`);
    return regex.test(formula);
}

function isHydroxide(formula) {
    return formula.includes("OH") && !containsElement(formula, "C");
}

function isOxide(formula) {
    return containsElement(formula, "O") && !containsElement(formula, "C") && /O\d*$/.test(formula.replace(/[()]/g, ""));
}

function isInorganicCarbonFormula(formula) {
    const withoutParentheses = formula.replace(/[()]/g, "");

    return /^CO2?$/.test(withoutParentheses)
        || /^CS2$/.test(withoutParentheses)
        || /^H?CO3\d*$/.test(withoutParentheses)
        || /CO3/.test(withoutParentheses)
        || /HCO3/.test(withoutParentheses)
        || /CN/.test(withoutParentheses)
        || /SCN/.test(withoutParentheses);
}
