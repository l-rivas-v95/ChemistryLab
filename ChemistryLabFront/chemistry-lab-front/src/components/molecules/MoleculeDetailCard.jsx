import { useState } from "react";
import SdfViewer from "./SdfViewer";
import MoleculeStructure from "./MoleculeStructure";
import {
    cleanLongText,
    getSafetySummary,
    getSynonymSummary,
    getUsesSummary
} from "../../utils/moleculeSafety";
import {
    cleanText,
    formatDescription,
    formatPropertyShort,
    hasValue
} from "../../utils/moleculeFormatters";

function MoleculeDetailCard({ molecula, onClose }) {
    const [mostrarModelo3d, setMostrarModelo3d] = useState(false);

    if (!molecula) {
        return null;
    }

    const safety = getSafetySummary(molecula.riesgos) || {
        level: "unknown",
        label: "Sin información",
        items: []
    };
    const usos = getUsesSummary(molecula.usos) || [];
    const sinonimos = getSynonymSummary(molecula.sinonimos) || [];

    return (
        <div className="molecule-modal-overlay" onClick={onClose}>
            <article
                className="molecule-detail-card molecule-detail-card-three"
                onClick={(event) => event.stopPropagation()}
            >
                <button
                    type="button"
                    className="molecule-detail-close"
                    onClick={onClose}
                    aria-label="Cerrar detalle"
                >
                    ×
                </button>

                <section className="molecule-detail-column molecule-detail-main">
                    <div className="molecule-detail-title">
                        <div>
                            <h2>{molecula.nombre || "Molécula sin nombre"}</h2>
                            <p>{molecula.nombreIupac || "Nombre IUPAC no disponible"}</p>
                        </div>

                        <strong>{molecula.formula || "Sin fórmula"}</strong>
                    </div>

                    <div className="molecule-detail-image">
                        <MoleculeStructure molecula={molecula} size="detail"/>
                    </div>

                    <div className="molecule-detail-3d-box">
                        <h4>🧊 Modelo 3D</h4>

                        <div className="molecule-detail-3d-content">
                            {molecula.pubchemCid ? (
                                mostrarModelo3d ? (
                                    <SdfViewer cid={molecula.pubchemCid}/>
                                ) : (
                                    <button
                                        type="button"
                                        className="molecule-load-3d-button"
                                        onClick={() => setMostrarModelo3d(true)}
                                    >
                                        Cargar modelo 3D
                                    </button>
                                )
                            ) : (
                                <div className="molecule-3d-empty">
                                    Modelo 3D no disponible
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="molecule-detail-type">
                        {molecula.tipoCompuesto || "Sin clasificar"}
                    </div>

                    <div className="molecule-detail-actions">
                        {molecula.pubchemCid && (
                            <a
                                href={`https://pubchem.ncbi.nlm.nih.gov/compound/${molecula.pubchemCid}`}
                                target="_blank"
                                rel="noreferrer"
                            >
                                📚 PubChem
                            </a>
                        )}
                    </div>

                </section>

                <section className="molecule-detail-column molecule-detail-info">
                    <h3>Propiedades</h3>

                    <div className="molecule-detail-grid molecule-detail-grid-compact">
                        <DetailItem icono="⚖️" label="Masa molecular" value={molecula.masaMolecular} unit="g/mol"/>
                        <DetailItem icono="⚡" label="Carga" value={molecula.carga}/>
                        <DetailItem icono="🔥" label="Punto de fusión" value={molecula.puntoFusion}/>
                        <DetailItem icono="🌋" label="Punto de ebullición" value={molecula.puntoEbullicion}/>
                        <DetailItem icono="💧" label="Solubilidad" value={molecula.solubilidad}/>
                        <DetailItem icono="🧪" label="pH" value={molecula.ph}/>
                        <DetailItem icono="🧲" label="Densidad" value={molecula.densidad}/>
                        <DetailItem icono="🧬" label="Átomos pesados" value={molecula.atomosPesados}/>
                        <DetailItem icono="🔗" label="Enlaces rotables" value={molecula.enlacesRotables}/>
                        <DetailItem icono="🧠" label="Complejidad" value={molecula.complejidad}/>
                        <DetailItem icono="🫧" label="TPSA" value={molecula.tpsa}/>
                        <DetailItem icono="🧪" label="XLogP" value={molecula.xlogp}/>
                    </div>

                    <div className="molecule-description-panel molecule-description-panel-center">
                        <h3>Descripción</h3>
                        <p title={cleanText(molecula.descripcion)}>
                            {formatDescription(molecula.descripcion, 700)}
                        </p>
                    </div>
                </section>

                <section className="molecule-detail-column molecule-detail-side">
                    <h3>Seguridad y usos</h3>

                    <SafetyCard safety={safety}/>

                    <BulletListCard
                        icono="🧰"
                        titulo="Usos principales"
                        items={usos}
                    />

                    <TagListCard
                        icono="🏷️"
                        titulo="Sinónimos"
                        items={sinonimos}
                    />

                </section>
            </article>
        </div>
    );
}

function DetailItem({ icono, label, value, unit }) {
    const tieneValor = hasValue(value);
    const textoCompleto = tieneValor ? cleanText(value) : "";
    const textoVisible = tieneValor ? formatPropertyShort(value, 42) : "N/A";

    return (
        <div className="molecule-detail-item molecule-detail-item-compact" title={textoCompleto}>
            <span>{icono}</span>

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

function SafetyCard({ safety }) {
    const safeSafety = safety || {
        level: "unknown",
        label: "Sin información",
        items: []
    };
    const items = Array.isArray(safeSafety.items) ? safeSafety.items : [];

    return (
        <div className={`molecule-safety-card molecule-safety-${safeSafety.level || "unknown"}`}>
            <div className="molecule-panel-header">
                <span>☣️</span>
                <h4>Riesgos</h4>
            </div>

            <strong className="molecule-safety-label">
                {safeSafety.label || "Sin información"}
            </strong>

            <div className="molecule-hazard-list">
                {items.slice(0, 5).map((item, index) => (
                    <div
                        key={`${item?.code || "hazard"}-${index}`}
                        className={`molecule-hazard-item molecule-hazard-${item?.severity || "unknown"}`}
                    >
                        <span>{item?.icon || "ℹ️"}</span>
                        <div>
                            {item?.code && <b>{item.code}</b>}
                            <p>{item?.description || "Sin descripción disponible"}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

function BulletListCard({ icono, titulo, items }) {
    const safeItems = Array.isArray(items) ? items : [];

    return (
        <div className="molecule-simple-list-card">
            <div className="molecule-panel-header">
                <span>{icono}</span>
                <h4>{titulo}</h4>
            </div>

            <ul className="molecule-bullet-list">
                {safeItems.length > 0 ? (
                    safeItems.map((item, index) => (
                        <li key={`${item}-${index}`} title={item}>
                            {cleanLongText(item, 120)}
                        </li>
                    ))
                ) : (
                    <li>Sin información disponible.</li>
                )}
            </ul>
        </div>
    );
}

function TagListCard({ icono, titulo, items }) {
    const safeItems = Array.isArray(items) ? items : [];

    return (
        <div className="molecule-simple-list-card">
            <div className="molecule-panel-header">
                <span>{icono}</span>
                <h4>{titulo}</h4>
            </div>

            <div className="molecule-simple-list">
                {safeItems.length > 0 ? (
                    safeItems.map((item, index) => (
                        <span key={`${item}-${index}`} title={item}>
                            {cleanLongText(item, 38)}
                        </span>
                    ))
                ) : (
                    <span>Sin información disponible.</span>
                )}
            </div>
        </div>
    );
}

export default MoleculeDetailCard;
