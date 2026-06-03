function ElementExtraInfoRight({ elemento, claseCategoria }) {
    return (
        <div className={`pagina-derecha ${claseCategoria}`}>
            <h3>Información adicional</h3>

            <div className="extra-info-grid">
                <InfoItem
                    icono="👁️"
                    tipo="apariencia"
                    titulo="Apariencia"
                    valor={elemento.apariencia}
                    grande
                />

                <InfoItem
                    icono="⚖️"
                    tipo="densidad"
                    titulo="Densidad"
                    valor={elemento.densidad}
                    unidad="g/cm³"
                />

                <InfoItem
                    icono="🌡️"
                    tipo="calor"
                    titulo="Calor molar"
                    valor={elemento.calorMolar}
                    unidad="J/(mol·K)"
                />

                <InfoItem
                    icono="🌋"
                    tipo="calor"
                    titulo="Punto de ebullición"
                    valor={elemento.puntoEbullicion}
                    unidad="K"
                />

                <InfoItem
                    icono="🔥"
                    tipo="calor"
                    titulo="Punto de fusión"
                    valor={elemento.puntoFusion}
                    unidad="K"
                />

                <InfoItem
                    icono="⚡"
                    tipo="electricidad"
                    titulo="Afinidad electrónica"
                    valor={elemento.afinidadElectronica}
                    unidad="kJ/mol"
                />

                <InfoItem
                    icono="🧲"
                    tipo="electricidad"
                    titulo="Electronegatividad"
                    valor={elemento.electronegatividad}
                    unidad=""
                />

                <InfoItem
                    icono="🔬"
                    tipo="descubrimiento"
                    titulo="Descubierto por"
                    valor={elemento.descubiertoPor}
                />

                <InfoItem
                    icono="🏷️"
                    tipo="nombre"
                    titulo="Nombrado por"
                    valor={elemento.nombradoPor}
                />

            </div>

            <div className="extra-links">
                {elemento.fuente && (
                    <a href={elemento.fuente} target="_blank" rel="noreferrer">
                        📚 Fuente
                    </a>
                )}

                {elemento.imagenUrl && (
                    <a href={elemento.imagenUrl} target="_blank" rel="noreferrer">
                        🖼️ Imagen original
                    </a>
                )}

                {elemento.modelo3dBohr && (
                    <a href={elemento.modelo3dBohr} target="_blank" rel="noreferrer">
                        🧊 Modelo 3D
                    </a>
                )}
            </div>
        </div>
    );
}

function InfoItem({icono, tipo, titulo, valor, unidad, grande = false}) {
    const valorFinal =
        valor !== null && valor !== undefined && valor !== "" ? valor : "N/A";

    return (
        <div
            className={`extra-info-item extra-info-${tipo} ${
                grande ? "extra-info-grande" : ""
            }`}
        >
            <div className="extra-info-icono">{icono}</div>

            <div className="extra-info-contenido">
                <small>{titulo}</small>

                <strong>
                    {valorFinal}
                    {valorFinal !== "N/A" && unidad && (
                        <span className="extra-info-unidad"> {unidad}</span>
                    )}
                </strong>
            </div>
        </div>
    );
}

export default ElementExtraInfoRight;