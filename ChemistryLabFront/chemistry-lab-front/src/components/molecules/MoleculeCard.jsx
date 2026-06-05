import MoleculeStructure from "./MoleculeStructure";
import {
    cleanText,
    formatPropertyShort,
    hasValue
} from "../../utils/moleculeFormatters";
import {
    getMoleculeCategoryClass,
    getTipoVisible
} from "../../utils/moleculeCategory";
import { getFormulaVisible } from "../../utils/moleculeFormula";

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

export default MoleculeCard;
