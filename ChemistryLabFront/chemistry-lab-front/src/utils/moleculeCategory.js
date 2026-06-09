const FAMILY_CATEGORY_MAP = {
    ORGANIC: "organic",
    ACID: "acid",
    HYDROXIDE: "base",
    SALT: "salt",
    METALLIC_OXIDE: "oxide",
    COVALENT_OXIDE: "oxide",
    PEROXIDE: "oxide",
    COVALENT: "inorganic",
    UNKNOWN: "unknown"
};

const FAMILY_LABEL_MAP = {
    ORGANIC: "Orgánica",
    ACID: "Ácido",
    HYDROXIDE: "Base / hidróxido",
    SALT: "Sal",
    METALLIC_OXIDE: "Óxido metálico",
    COVALENT_OXIDE: "Óxido covalente",
    PEROXIDE: "Peróxido",
    COVALENT: "Inorgánica",
    UNKNOWN: "Sin clasificar"
};

export function getMoleculeCategoryClass(molecula) {
    const family = normalizeFamily(molecula?.compoundFamily);
    return FAMILY_CATEGORY_MAP[family] || "unknown";
}

export function getTipoVisible(molecula) {
    const family = normalizeFamily(molecula?.compoundFamily);
    return FAMILY_LABEL_MAP[family] || "Sin clasificar";
}

export function normalizeText(value) {
    return String(value || "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}

function normalizeFamily(value) {
    return String(value || "UNKNOWN")
        .trim()
        .toUpperCase();
}
