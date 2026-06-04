import SdfViewer from "./SdfViewer";

function MoleculeImage({ molecula }) {
    const imagenPrincipal = molecula.imagen2d;
    const imagenFallback = molecula.pubchemCid
        ? `https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/${molecula.pubchemCid}/PNG`
        : null;

    const handleError = (event) => {
        if (imagenFallback && event.currentTarget.src !== imagenFallback) {
            event.currentTarget.src = imagenFallback;
        }
    };

    return (
        <img
            src={imagenPrincipal || imagenFallback}
            alt={molecula.nombre || "Molécula"}
            onError={handleError}
        />
    );
}

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
                            <MoleculeImage molecula={molecula} />
                        ) : (
                            <div className="molecule-no-image">⚗️</div>
                        )}
                    </div>

                    <div className="molecule-detail-3d-box">
                        <h4>🧊 Modelo 3D</h4>

                        {molecula.modelo3dUrl ? (
                            <SdfViewer cid={molecula.pubchemCid} />
                        ) : (
                            <div className="molecule-3d-empty">
                                Modelo 3D no disponible
                            </div>
                        )}
                    </div>

                    <div className="molecule-detail-type">
                        {molecula.tipoCompuesto || "Inorgánico"}
                    </div>

                    <p className="molecule-detail-description">
                        {molecula.descripcion || "Sin descripción disponible."}
                    </p>

                    <TextInfoCard
                        icono="🏷️"
                        titulo="Sinónimos"
                        texto={molecula.sinonimos}
                        vacio="Sin sinónimos registrados"
                    />
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

                    <div className="molecule-detail-text-columns">
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
    const contenido = texto && texto.trim() !== "" ? texto : vacio;
    const partes = contenido
        .split("|")
        .map((item) => item.trim())
        .filter(Boolean);

    const esTextoLargo = partes.some((item) => item.length > 90) || contenido.length > 260;

    return (
        <div className="molecule-detail-text-card">
            <div className="molecule-detail-text-card-header">
                <span>{icono}</span>
                <h4>{titulo}</h4>
            </div>

            {esTextoLargo ? (
                <div className="molecule-text-list">
                    {partes.slice(0, 6).map((item, index) => (
                        <p key={index}>{item}</p>
                    ))}
                </div>
            ) : (
                <div className="molecule-tags-list">
                    {partes.slice(0, 10).map((item, index) => (
                        <span key={index} className="molecule-tag">
                            {item}
                        </span>
                    ))}
                </div>
            )}
        </div>
    );
}

export default MoleculeDetailCard;