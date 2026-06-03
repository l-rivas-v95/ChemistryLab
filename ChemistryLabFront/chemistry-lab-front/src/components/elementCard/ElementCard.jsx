import { useState } from "react";
import { getClaseCategoria } from "../../utils/categoriaUtils";
import ElementExtraInfoLeft from "./ElementExtraInfoLeft";
import ElementExtraInfoRight from "./ElementExtraInfoRight";
import "./ElementCard.css";

function ElementCard({ elemento, onClose }) {
    const [mostrarPaginasExtra, setMostrarPaginasExtra] = useState(false);

    const claseCategoria = getClaseCategoria(elemento.categoria);

    function cerrarModal() {
        setMostrarPaginasExtra(false);
        onClose();
    }

    return (
        <div className="modal-carta" onClick={cerrarModal}>
            <section
                className={`libro-elemento ${
                    mostrarPaginasExtra ? "libro-abierto-completo" : ""
                }`}
                onClick={(event) => event.stopPropagation()}
            >
                {mostrarPaginasExtra && (
                    <ElementExtraInfoLeft
                        elemento={elemento}
                        claseCategoria={claseCategoria}
                    />
                )}

                <div className={`carta-elemento pagina-centro ${claseCategoria}`}>
                    <button className="cerrar-carta" onClick={cerrarModal}>
                        X
                    </button>

                    <div className="carta-cabecera">
                        <h2>{elemento.nombre}</h2>
                        <span>{elemento.simbolo}</span>
                    </div>

                    <div className="carta-imagen">
                        {elemento.imagenUrl ? (
                            <img src={elemento.imagenUrl} alt={elemento.nombre}/>
                        ) : (
                            <div className="sin-imagen">{elemento.simbolo}</div>
                        )}
                    </div>

                    <div className="carta-tipo">
                        {elemento.categoria || "Elemento químico"}
                    </div>

                    <div className="carta-descripcion">
                        {elemento.descripcion || "Sin descripción disponible."}
                    </div>

                    <div className="carta-stats">
                        <span>Nº {elemento.numeroAtomico}</span>
                        <span>Masa {elemento.masaAtomica}</span>
                    </div>

                    <div className="carta-datos">
                        <div className="dato-chip">
                            <span className="dato-icono">⚗️</span>
                            <div>
                                <small>Estado</small>
                                <strong>{elemento.estado25c || "N/A"}</strong>
                            </div>
                        </div>

                        <div className="dato-chip">
                            <span className="dato-icono">🔋</span>
                            <div>
                            <small>Valencia</small>
                                <strong>{elemento.electronesValencia ?? "N/A"}</strong>
                            </div>
                        </div>

                        <div className="dato-chip">
                            <span className="dato-icono">📊</span>
                            <div>
                                <small>Grupo</small>
                                <strong>{elemento.grupoPeriodico ?? "N/A"}</strong>
                            </div>
                        </div>

                        <div className="dato-chip">
                            <span className="dato-icono">🧱</span>
                            <div>
                                <small>Periodo</small>
                                <strong>{elemento.periodo ?? "N/A"}</strong>
                            </div>
                        </div>

                    </div>
                </div>

                {mostrarPaginasExtra && (
                    <ElementExtraInfoRight
                        elemento={elemento}
                        claseCategoria={claseCategoria}
                    />
                )}

                <button
                    className={`boton-info-libro ${mostrarPaginasExtra ? "activo" : ""}`}
                    onClick={() => setMostrarPaginasExtra(!mostrarPaginasExtra)}
                >
                    {mostrarPaginasExtra ? "Cerrar información" : "Ver más información"}
                </button>
            </section>
        </div>
    );
}

export default ElementCard;