import MoleculeStructure from "./MoleculeStructure";
import {
    cleanText,
    formatPropertyShort,
    hasValue
} from "../../utils/moleculeFormatters";

function MoleculeCard({ molecula, onClick }) {
    const categoria = getMoleculeCategoryClass(molecula);
    const tipoVisible = getTipoVisible(molecula);
    const formulaVisible = getFormulaVisible(molecula);

    return (
        <button
            type="button"
            className={`molecule-card molecule-card-${categoria}`}
            onClick={onClick}
        >
            <div className="molecule-card-top">
                <span className="molecule-type">
                    {tipoVisible}
                </span>

                {molecula.pubchemCid && (
                    <span className="molecule-cid">
                        CID {molecula.pubchemCid}
                    </span>
                )}
            </div>

            <div className="molecule-image-box">
                <MoleculeStructure molecula={molecula} />
            </div>

            <div className="molecule-main-info">
                <h3>{molecula.nombre}</h3>
                <strong>{formulaVisible}</strong>
            </div>

            <p className="molecule-description">
                {formatDescriptionShort(molecula.descripcion)}
            </p>

            <div className="molecule-data-grid">
                <MoleculeChip
                    icono="⚖️"
                    label="Masa"
                    value={molecula.masaMolecular}
                    unit="g/mol"
                />

                <MoleculeChip
                    icono="⚡"
                    label="Carga"
                    value={molecula.carga}
                />

                <MoleculeChip
                    icono="🔥"
                    label="Fusión"
                    value={molecula.puntoFusion}
                />

                <MoleculeChip
                    icono="🌋"
                    label="Ebullición"
                    value={molecula.puntoEbullicion}
                />
            </div>

            <div className="molecule-open-hint">
                Ver ficha completa
            </div>
        </button>
    );
}

function MoleculeChip({ icono, label, value, unit }) {
    const tieneValor = hasValue(value);
    const textoCompleto = tieneValor ? cleanText(value) : "";
    const textoVisible = tieneValor ? formatPropertyShort(value, 26) : "N/A";

    return (
        <div className="molecule-chip" title={textoCompleto}>
            <span className="molecule-chip-icon">{icono}</span>

            <div>
                <small>{label}</small>
                <strong>
                    {textoVisible}
                    {tieneValor && unit ? ` ${unit}` : ""}
                </strong>
            </div>
        </div>
    );
}

function getMoleculeCategoryClass(molecula) {
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

function getTipoVisible(molecula) {
    return (
        molecula?.tipoCompuesto ||
        molecula?.tipo_compuesto ||
        molecula?.tipo ||
        "Sin clasificar"
    );
}

function getFormulaVisible(molecula) {
    const nombre = normalizeText(molecula?.nombre);
    const formula = cleanFormula(molecula?.formula || "Sin fórmula");

    const knownByName = {
        water: "H2O",
        ammonia: "NH3",
        "hydrogen peroxide": "H2O2",
        "hydrogen sulfide": "H2S",
        "carbon dioxide": "CO2",
        "carbon monoxide": "CO",
        "sulfur dioxide": "SO2",
        "sulfur trioxide": "SO3",
        "nitric oxide": "NO",
        "nitrogen dioxide": "NO2",
        "dinitrogen monoxide": "N2O",

        "hydrochloric acid": "HCl",
        "sulfuric acid": "H2SO4",
        "nitric acid": "HNO3",
        "phosphoric acid": "H3PO4",
        "carbonic acid": "H2CO3",
        "boric acid": "H3BO3",
        "hydrofluoric acid": "HF",
        "hydrobromic acid": "HBr",
        "hydroiodic acid": "HI",
        "perchloric acid": "HClO4",

        "sodium hydroxide": "NaOH",
        "potassium hydroxide": "KOH",
        "calcium hydroxide": "Ca(OH)2",
        "magnesium hydroxide": "Mg(OH)2",
        "aluminum hydroxide": "Al(OH)3",
        "iron(iii) hydroxide": "Fe(OH)3",
        "copper(ii) hydroxide": "Cu(OH)2",
        "zinc hydroxide": "Zn(OH)2",
        "barium hydroxide": "Ba(OH)2",
        "ammonium hydroxide": "NH4OH",

        "sodium chloride": "NaCl",
        "potassium chloride": "KCl",
        "calcium chloride": "CaCl2",
        "magnesium chloride": "MgCl2",
        "aluminum chloride": "AlCl3",
        "iron(iii) chloride": "FeCl3",
        "copper(ii) chloride": "CuCl2",
        "zinc chloride": "ZnCl2",
        "silver chloride": "AgCl",
        "ammonium chloride": "NH4Cl",

        "sodium carbonate": "Na2CO3",
        "calcium carbonate": "CaCO3",
        "magnesium carbonate": "MgCO3",
        "potassium carbonate": "K2CO3",
        "ammonium carbonate": "(NH4)2CO3",
        "sodium bicarbonate": "NaHCO3",
        "potassium bicarbonate": "KHCO3",

        "sodium sulfate": "Na2SO4",
        "potassium sulfate": "K2SO4",
        "calcium sulfate": "CaSO4",
        "magnesium sulfate": "MgSO4",
        "copper(ii) sulfate": "CuSO4",
        "zinc sulfate": "ZnSO4",
        "iron(ii) sulfate": "FeSO4",
        "aluminum sulfate": "Al2(SO4)3",
        "ammonium sulfate": "(NH4)2SO4",
        "sodium sulfite": "Na2SO3",
        "sodium thiosulfate": "Na2S2O3",
        "sodium sulfide": "Na2S",

        "sodium nitrate": "NaNO3",
        "potassium nitrate": "KNO3",
        "calcium nitrate": "Ca(NO3)2",
        "ammonium nitrate": "NH4NO3",
        "silver nitrate": "AgNO3",
        "copper(ii) nitrate": "Cu(NO3)2",
        "iron(iii) nitrate": "Fe(NO3)3",
        "sodium nitrite": "NaNO2",
        "potassium nitrite": "KNO2",
        "calcium nitrite": "Ca(NO2)2",

        "sodium phosphate": "Na3PO4",
        "potassium phosphate": "K3PO4",
        "calcium phosphate": "Ca3(PO4)2",
        "ammonium phosphate": "(NH4)3PO4",
        "sodium hydrogen phosphate": "Na2HPO4",
        "sodium dihydrogen phosphate": "NaH2PO4",
        "calcium hydrogen phosphate": "CaHPO4",
        "magnesium phosphate": "Mg3(PO4)2",
        "aluminum phosphate": "AlPO4",
        "iron(iii) phosphate": "FePO4",

        "sodium hypochlorite": "NaClO",
        "sodium chlorate": "NaClO3",
        "potassium chlorate": "KClO3",
        "sodium perchlorate": "NaClO4",
        "potassium permanganate": "KMnO4",
        "sodium dichromate": "Na2Cr2O7",
        "potassium dichromate": "K2Cr2O7",
        "sodium cyanide": "NaCN",
        "potassium cyanide": "KCN"
    };

    const knownByFormula = {
        ClH: "HCl",
        FH: "HF",
        BrH: "HBr",
        H3N: "NH3",
        H2O4S: "H2SO4",
        H3O4P: "H3PO4",
        CH2O3: "H2CO3",
        BH3O3: "H3BO3",
        ClHO4: "HClO4",

        HNaO: "NaOH",
        HKO: "KOH",
        CaH2O2: "Ca(OH)2",
        H2MgO2: "Mg(OH)2",
        AlH3O3: "Al(OH)3",
        FeH3O3: "Fe(OH)3",
        CuH2O2: "Cu(OH)2",
        H2O2Zn: "Zn(OH)2",
        BaH2O2: "Ba(OH)2",
        H5NO: "NH4OH",

        ClNa: "NaCl",
        ClK: "KCl",
        Cl2Mg: "MgCl2",
        Cl3Fe: "FeCl3",
        Cl2Cu: "CuCl2",
        Cl2Zn: "ZnCl2",
        ClH4N: "NH4Cl",

        CNa2O3: "Na2CO3",
        CCaO3: "CaCO3",
        CMgO3: "MgCO3",
        C2K2O6: "K2CO3",
        CHNaO3: "NaHCO3",
        CHKO3: "KHCO3",

        Na2O4S: "Na2SO4",
        K2O4S: "K2SO4",
        CaO4S: "CaSO4",
        MgO4S: "MgSO4",
        CuO4S: "CuSO4",
        ZnO4S: "ZnSO4",
        FeO4S: "FeSO4",
        Al2O12S3: "Al2(SO4)3",
        H8N2O4S: "(NH4)2SO4",
        Na2O3S: "Na2SO3",
        Na2O3S2: "Na2S2O3",
        Na2S: "Na2S",

        NNaO3: "NaNO3",
        CaN2O6: "Ca(NO3)2",
        H4N2O3: "NH4NO3",
        CuN2O6: "Cu(NO3)2",
        FeN3O9: "Fe(NO3)3",
        NNaO2: "NaNO2",
        CaN2O4: "Ca(NO2)2",

        Na3O4P: "Na3PO4",
        H2KO4P: "K3PO4",
        Ca3O8P2: "Ca3(PO4)2",
        H12N3O4P: "(NH4)3PO4",
        HNa2O4P: "Na2HPO4",
        H2NaO4P: "NaH2PO4",
        CaHO4P: "CaHPO4",
        Mg3O8P2: "Mg3(PO4)2",
        AlO4P: "AlPO4",
        FeO4P: "FePO4",

        ClNaO: "NaClO",
        ClNaO3: "NaClO3",
        ClKO3: "KClO3",
        ClNaO4: "NaClO4",
        Cr2Na2O7: "Na2Cr2O7",
        Cr2K2O7: "K2Cr2O7",
        CNNa: "NaCN",
        CKN: "KCN"
    };

    return knownByName[nombre] || knownByFormula[formula] || formula;
}

function formatDescriptionShort(value) {
    const clean = cleanText(value);

    if (!clean) {
        return "Sin descripción disponible.";
    }

    if (clean.length <= 115) {
        return clean;
    }

    return `${clean.slice(0, 115).trim()}...`;
}

function cleanFormula(value) {
    return String(value || "")
        .replace(/[+-]\d*$/g, "")
        .replace(/\d*[+-]$/g, "")
        .replace(/\s/g, "");
}

function normalizeText(value) {
    return String(value || "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}

export default MoleculeCard;