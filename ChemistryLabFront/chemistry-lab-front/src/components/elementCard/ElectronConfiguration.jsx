import { formatearConfiguracionMoller } from "../../utils/electronUtils";

function ElectronConfiguration({ configuracion, bloque }) {
    const filas = formatearConfiguracionMoller(configuracion);

    return (
        <div className="dato-chip dato-configuracion">
            <span className="dato-icono">🧬</span>

            <div className="configuracion-contenido">
                <div className="configuracion-header">
                    <small>Configuración electrónica</small>

                    <span className={`bloque-chip bloque-${bloque}`}>
            Bloque {bloque ?? "N/A"}
          </span>
                </div>

                <div className="configuracion-diagrama">
                    {filas.map((fila) => (
                        <div className="configuracion-nivel" key={fila.nivel}>
                            <span className="nivel-label">{fila.nivel}</span>

                            {fila.orbitales.map((orbital, index) => (
                                <span
                                    key={index}
                                    className={`orbital orbital-${index} ${
                                        orbital.clave === "" ? "orbital-vacio" : ""
                                    } ${
                                        orbital.relleno
                                            ? "orbital-relleno"
                                            : "orbital-sin-rellenar"
                                    }`}
                                >
                  {orbital.texto}
                </span>
                            ))}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default ElectronConfiguration;