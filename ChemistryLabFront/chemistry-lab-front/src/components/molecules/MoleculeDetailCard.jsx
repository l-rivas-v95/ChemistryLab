function MoleculeDetailCard({ molecula, onClose }) {
    return (
        <div className="molecule-modal-overlay">
            <article className="molecule-detail-card">
                <button className="molecule-detail-close" onClick={onClose}>
                    X
                </button>

                <section className="molecule-detail-main">
                    <div className="molecule-detail-title">
                        <div>
                            <h2>{molecula.nombre}</h2>
                            <p>{molecula.nombreIupac || "Nombre IUPAC no disponible"}</p>
                        </div>

                        <strong>{molecula.formula}</strong>
                    </div>

                    <div className="molecule-detail-image">
                        {molecula.imagen2d ? (
                            <img src={molecula.imagen2d} alt={molecula.nombre} />
                        ) : (
                            <div className="molecule-no-image">⚗️</div>
                        )}
                    </div>

                    <div className="molecule-detail-type">
                        {molecula.tipoCompuesto || "Inorgánico"}
                    </div>

                    <p className="molecule-detail-description">
                        {molecula.descripcion || "Sin descripción disponible."}
                    </p>
                </section>

                <section className="molecule-detail-info">
                    <h3>Información adicional</h3>

                    <div className="molecule-detail-grid">
                        <DetailItem icono="⚖️" label="Masa molecular" value={molecula.masaMolecular} unit="g/mol" />
                        <DetailItem icono="⚡" label="Carga" value={molecula.carga} />
                        <DetailItem icono="🔥" label="Punto de fusión" value={molecula.puntoFusion} />
                        <DetailItem icono="🌋" label="Punto de ebullición" value={molecula.puntoEbullicion} />
                        <DetailItem icono="💧" label="Solubilidad" value={molecula.solubilidad} />
                        <DetailItem icono="🧪" label="pH" value={molecula.ph} />
                        <DetailItem icono="🧲" label="Densidad" value={molecula.densidad} />
                        <DetailItem icono="🧬" label="Átomos pesados" value={molecula.atomosPesados} />
                        <DetailItem icono="🔗" label="Enlaces rotables" value={molecula.enlacesRotables} />
                        <DetailItem icono="🧠" label="Complejidad" value={molecula.complejidad} />
                    </div>

                    <TextInfoCard
                        icono="☣️"
                        titulo="Riesgos"
                        texto={molecula.riesgos}
                        vacio="Sin riesgos registrados"
                    />

                    <TextInfoCard
                        icono="🧰"
                        titulo="Usos"
                        texto={molecula.usos}
                        vacio="Sin usos registrados"
                    />

                    <TextInfoCard
                        icono="🏷️"
                        titulo="Sinónimos"
                        texto={molecula.sinonimos}
                        vacio="Sin sinónimos registrados"
                    />

                    <div className="molecule-detail-actions">
                        {molecula.modelo3dUrl && (
                            <a href={molecula.modelo3dUrl} target="_blank" rel="noreferrer">
                                🧊 Modelo 3D
                            </a>
                        )}

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
            </article>
        </div>
    );
}

function DetailItem({ icono, label, value, unit }) {
    const tieneValor = value !== null && value !== undefined && value !== "";
    const finalValue = tieneValor ? value : "N/A";

    return (
        <div className="molecule-detail-item">
            <span>{icono}</span>

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

function TextInfoCard({ icono, titulo, texto, vacio }) {
    const items = texto && texto.trim() !== ""
        ? texto.split("|").map((item) => item.trim()).filter(Boolean)
        : [vacio];

    return (
        <div className="molecule-detail-text-card">
            <div className="molecule-detail-text-card-header">
                <span>{icono}</span>
                <h4>{titulo}</h4>
            </div>

            <div className="molecule-tags-list">
                {items.slice(0, 8).map((item, index) => (
                    <span key={index} className="molecule-tag">
                        {item}
                    </span>
                ))}
            </div>
        </div>
    );
}
export default MoleculeDetailCard;