function MoleculeCard({ molecula, onClick }) {
    return (
        <button className="molecule-card" onClick={onClick}>
            <div className="molecule-card-top">
                <span className="molecule-type">
                    {molecula.tipoCompuesto || "Inorgánico"}
                </span>

                <span className="molecule-cid">
                    CID {molecula.pubchemCid || "N/A"}
                </span>
            </div>

            <div className="molecule-image-box">
                {molecula.imagen2d ? (
                    <img
                        src={molecula.imagen2d}
                        alt={molecula.nombre || "Molécula"}
                    />
                ) : (
                    <div className="molecule-no-image">⚗️</div>
                )}
            </div>

            <div className="molecule-main-info">
                <h3>{molecula.nombre || "Molécula sin nombre"}</h3>
                <strong>{molecula.formula || "Fórmula no disponible"}</strong>
            </div>

            <p className="molecule-description">
                {molecula.descripcion || "Sin descripción disponible."}
            </p>

            <div className="molecule-data-grid">
                <InfoChip icono="⚖️" label="Masa" value={molecula.masaMolecular} unit="g/mol" />
                <InfoChip icono="⚡" label="Carga" value={molecula.carga} />
                <InfoChip icono="🔥" label="Fusión" value={molecula.puntoFusion} />
                <InfoChip icono="🌋" label="Ebullición" value={molecula.puntoEbullicion} />
            </div>

            <div className="molecule-open-hint">
                Ver ficha completa
            </div>
        </button>
    );
}

function InfoChip({ icono, label, value, unit }) {
    const tieneValor = value !== null && value !== undefined && value !== "";
    const finalValue = tieneValor ? value : "N/A";

    return (
        <div className="molecule-chip">
            <span className="molecule-chip-icon">{icono}</span>

            <div>
                <small>{label}</small>
                <strong>
                    {finalValue}
                    {tieneValor && unit ? ` ${unit}` : ""}
                </strong>
            </div>
        </div>
    );
}

export default MoleculeCard;