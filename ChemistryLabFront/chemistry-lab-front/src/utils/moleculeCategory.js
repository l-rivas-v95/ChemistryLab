export function getMoleculeCategoryClass(molecula) {
    const tipo = normalizeText(
        molecula?.tipoCompuesto ||
        molecula?.tipo_compuesto ||
        molecula?.tipo ||
        ""
    );

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

    return "unknown";
}

export function getTipoVisible(molecula) {
    return (
        molecula?.tipoCompuesto ||
        molecula?.tipo_compuesto ||
        molecula?.tipo ||
        "Sin clasificar"
    );
}

export function normalizeText(value) {
    return String(value || "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}
