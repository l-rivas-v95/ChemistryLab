export function getSafetySummary(riesgos) {
    const text = String(riesgos || "").trim();

    if (!text) {
        return {
            level: "none",
            label: "Sin riesgos registrados",
            codes: [],
            items: []
        };
    }

    const codes = extractHazardCodes(text);
    const items = codes.map(getHazardInfo).filter(Boolean);

    if (items.length === 0) {
        return {
            level: "info",
            label: cleanLongText(text, 160),
            codes: [],
            items: [
                {
                    code: null,
                    icon: "☣️",
                    title: "Información de seguridad",
                    description: cleanLongText(text, 160),
                    severity: "info"
                }
            ]
        };
    }

    const level = getMaxSeverity(items);

    return {
        level,
        label: `${items.length} indicación(es) de peligro`,
        codes,
        items
    };
}

export function getUsesSummary(usos) {
    const text = String(usos || "").trim();

    if (!text) {
        return ["Sin usos registrados"];
    }

    return splitUsefulParts(text)
        .map((item) => cleanUseText(item))
        .filter(Boolean)
        .slice(0, 6);
}
function cleanUseText(text) {
    let clean = String(text || "")
        .replace(/\s+/g, " ")
        .replace(/^\.\s*/, "")
        .trim();

    clean = clean.replace(/^\.\.\.\s*/, "");
    clean = clean.replace(/^\.\s*/, "");

    if (!clean) return null;

    return clean;
}
export function getSynonymSummary(sinonimos) {
    const text = String(sinonimos || "").trim();

    if (!text) {
        return ["Sin sinónimos registrados"];
    }

    return text
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean)
        .filter((item) => item.length <= 45)
        .slice(0, 12);
}

export function cleanLongText(text, maxLength = 180) {
    const clean = String(text || "")
        .replace(/\s+/g, " ")
        .replace(/\s+\|\s+/g, " | ")
        .trim();

    if (clean.length <= maxLength) {
        return clean;
    }

    return `${clean.slice(0, maxLength).trim()}...`;
}

function extractHazardCodes(text) {
    const matches = String(text || "").match(/\bH\d{3}[A-Z]?\b/g);
    return [...new Set(matches || [])];
}

function getHazardInfo(code) {
    const hazardMap = {
        H200: ["💥", "Explosivo inestable", "danger"],
        H201: ["💥", "Explosivo", "danger"],
        H202: ["💥", "Peligro de explosión", "danger"],
        H203: ["💥", "Peligro de incendio/explosión", "danger"],
        H204: ["💥", "Peligro de incendio o proyección", "warning"],
        H220: ["🔥", "Gas extremadamente inflamable", "danger"],
        H221: ["🔥", "Gas inflamable", "warning"],
        H222: ["🔥", "Aerosol extremadamente inflamable", "danger"],
        H225: ["🔥", "Líquido y vapores muy inflamables", "danger"],
        H226: ["🔥", "Líquido y vapores inflamables", "warning"],
        H228: ["🔥", "Sólido inflamable", "warning"],
        H240: ["💥", "Puede explotar al calentarse", "danger"],
        H241: ["💥", "Puede incendiarse o explotar", "danger"],
        H242: ["🔥", "Puede incendiarse al calentarse", "warning"],
        H250: ["🔥", "Se inflama espontáneamente", "danger"],
        H260: ["🔥", "Libera gases inflamables con agua", "danger"],
        H270: ["🔥", "Puede provocar o agravar incendios", "danger"],
        H271: ["🔥", "Puede provocar incendio o explosión", "danger"],
        H272: ["🔥", "Puede agravar un incendio", "warning"],
        H280: ["🧯", "Gas a presión", "info"],
        H290: ["⚠️", "Puede ser corrosivo para metales", "warning"],

        H300: ["☠️", "Mortal en caso de ingestión", "danger"],
        H301: ["☠️", "Tóxico en caso de ingestión", "danger"],
        H302: ["⚠️", "Nocivo en caso de ingestión", "warning"],
        H304: ["🫁", "Puede ser mortal si se ingiere y penetra en vías respiratorias", "danger"],
        H310: ["☠️", "Mortal por contacto con la piel", "danger"],
        H311: ["☠️", "Tóxico por contacto con la piel", "danger"],
        H312: ["⚠️", "Nocivo por contacto con la piel", "warning"],
        H314: ["🧪", "Provoca quemaduras graves en la piel y lesiones oculares", "danger"],
        H315: ["⚠️", "Irrita la piel", "warning"],
        H317: ["⚠️", "Puede provocar reacción alérgica en la piel", "warning"],
        H318: ["👁️", "Provoca lesiones oculares graves", "danger"],
        H319: ["👁️", "Provoca irritación ocular grave", "warning"],
        H330: ["☠️", "Mortal en caso de inhalación", "danger"],
        H331: ["☠️", "Tóxico en caso de inhalación", "danger"],
        H332: ["⚠️", "Nocivo en caso de inhalación", "warning"],
        H334: ["🫁", "Puede provocar síntomas de alergia o asma", "danger"],
        H335: ["🫁", "Puede irritar las vías respiratorias", "warning"],
        H336: ["🧠", "Puede provocar somnolencia o vértigo", "warning"],
        H340: ["🧬", "Puede provocar defectos genéticos", "danger"],
        H341: ["🧬", "Se sospecha que provoca defectos genéticos", "warning"],
        H350: ["☣️", "Puede provocar cáncer", "danger"],
        H351: ["☣️", "Se sospecha que provoca cáncer", "warning"],
        H360: ["⚠️", "Puede perjudicar la fertilidad o dañar al feto", "danger"],
        H361: ["⚠️", "Se sospecha que perjudica la fertilidad o daña al feto", "warning"],
        H370: ["🫀", "Provoca daños en órganos", "danger"],
        H371: ["🫀", "Puede provocar daños en órganos", "warning"],
        H372: ["🫀", "Provoca daños en órganos tras exposiciones prolongadas", "danger"],
        H373: ["🫀", "Puede provocar daños en órganos tras exposiciones prolongadas", "warning"],

        H400: ["🌊", "Muy tóxico para organismos acuáticos", "danger"],
        H410: ["🌊", "Muy tóxico para organismos acuáticos con efectos duraderos", "danger"],
        H411: ["🌊", "Tóxico para organismos acuáticos con efectos duraderos", "warning"],
        H412: ["🌊", "Nocivo para organismos acuáticos con efectos duraderos", "warning"],
        H413: ["🌊", "Puede ser nocivo para organismos acuáticos", "info"]
    };

    const item = hazardMap[code];

    if (!item) {
        return {
            code,
            icon: "⚠️",
            title: code,
            description: "Indicación de peligro registrada",
            severity: "warning"
        };
    }

    return {
        code,
        icon: item[0],
        title: code,
        description: item[1],
        severity: item[2]
    };
}

function getMaxSeverity(items) {
    if (items.some((item) => item.severity === "danger")) return "danger";
    if (items.some((item) => item.severity === "warning")) return "warning";
    return "info";
}

function splitUsefulParts(text) {
    return String(text || "")
        .split("|")
        .map((item) => item.trim())
        .filter(Boolean)
        .filter((item) => !item.toLowerCase().includes("category:"))
        .filter((item) => !item.toLowerCase().startsWith("pubchem"))
        .filter((item) => !item.toLowerCase().includes("this chemical"))
        .filter((item) => item.length > 12);
}