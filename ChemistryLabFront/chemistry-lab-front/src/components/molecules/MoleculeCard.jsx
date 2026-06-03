function MoleculeCard({ molecula }) {
    return (
        <article className="molecule-card">
            <div className="molecule-card-top">
        <span className="molecule-type">
          {molecula.tipoCompuesto || "Inorgánico"}
        </span>

                <span className="molecule-cid">
          CID {molecula.pubchemCid}
        </span>
            </div>

            <div className="molecule-image-box">
                {molecula.imagen2d ? (
                    <img src={molecula.imagen2d} alt={molecula.nombre} />
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

            <div className="molecule-actions">
                {molecula.modelo3dUrl && (
                    <a href={molecula.modelo3dUrl} target="_blank" rel="noreferrer">
                        Ver modelo 3D
                    </a>
                )}
            </div>
        </article>
    );
}

function InfoChip({ icono, label, value, unit }) {
    const finalValue = value !== null && value !== undefined && value !== "" ? value : "N/A";

    return (
        <div className="molecule-chip">
            <span>{icono}</span>
            <div>
                <small>{label}</small>
                <strong>
                    {finalValue}
                    {finalValue !== "N/A" && unit ? ` ${unit}` : ""}
                </strong>
            </div>
        </div>
    );
}

export default MoleculeCard;