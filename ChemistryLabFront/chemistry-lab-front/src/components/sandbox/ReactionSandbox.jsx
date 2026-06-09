import { useMemo, useState } from "react";
import "./ReactionSandbox.css";

const QUICK_ELEMENTS = ["H", "O", "C", "N", "Na", "Cl", "Fe", "Ca", "K", "S", "P", "Mg", "Al", "Cu", "Zn"];

function ReactionSandbox({ elementos = [] }) {
    const [reactivos, setReactivos] = useState([]);
    const [resultadoSimulado, setResultadoSimulado] = useState(null);

    const elementosDisponibles = useMemo(() => {
        const porSimbolo = new Map();

        elementos.forEach((elemento) => {
            if (elemento?.simbolo) {
                porSimbolo.set(elemento.simbolo, elemento);
            }
        });

        return QUICK_ELEMENTS.map((simbolo) => porSimbolo.get(simbolo) || { simbolo, nombre: simbolo });
    }, [elementos]);

    const queryVisual = useMemo(() => construirQueryVisual(reactivos), [reactivos]);

    const agregarElemento = (elemento) => {
        if (!elemento?.simbolo) {
            return;
        }

        setReactivos((actuales) => [
            ...actuales,
            {
                id: `${elemento.simbolo}-${crypto.randomUUID()}`,
                simbolo: elemento.simbolo,
                nombre: elemento.nombre || elemento.simbolo
            }
        ]);
        setResultadoSimulado(null);
    };

    const quitarElemento = (id) => {
        setReactivos((actuales) => actuales.filter((elemento) => elemento.id !== id));
        setResultadoSimulado(null);
    };

    const limpiar = () => {
        setReactivos([]);
        setResultadoSimulado(null);
    };

    const probar = () => {
        const elementosUnicos = [...new Set(reactivos.map((elemento) => elemento.simbolo))];

        setResultadoSimulado({
            query: queryVisual,
            elementos: elementosUnicos,
            mensaje: reactivos.length > 0
                ? "Vista preparada: aquí se llamará al backend para buscar compuestos con esos elementos."
                : "Añade elementos para probar una combinación."
        });
    };

    return (
        <section className="reaction-sandbox-page">
            <header className="reaction-sandbox-hero">
                <div>
                    <span className="reaction-sandbox-kicker">Sandbox químico</span>
                    <h2>Construye una búsqueda con elementos</h2>
                    <p>
                        Arrastra mentalmente la idea: eliges elementos, se forma una consulta tipo <b>Fe + O</b> y luego el backend buscará compuestos posibles en la base de datos.
                    </p>
                </div>

                <div className="reaction-sandbox-equation-preview">
                    <span>{queryVisual || "Añade elementos"}</span>
                    <button type="button" onClick={probar} disabled={reactivos.length === 0}>Probar</button>
                </div>
            </header>

            <div className="reaction-sandbox-layout">
                <aside className="reaction-element-palette">
                    <h3>Elementos rápidos</h3>
                    <p>Haz clic para añadirlos a la mezcla.</p>

                    <div className="reaction-element-grid">
                        {elementosDisponibles.map((elemento) => (
                            <button
                                type="button"
                                key={elemento.simbolo}
                                className="reaction-element-tile"
                                onClick={() => agregarElemento(elemento)}
                                title={elemento.nombre || elemento.simbolo}
                            >
                                <strong>{elemento.simbolo}</strong>
                                <small>{elemento.nombre || elemento.simbolo}</small>
                            </button>
                        ))}
                    </div>
                </aside>

                <section className="reaction-workbench">
                    <div className="reaction-workbench-header">
                        <div>
                            <h3>Reactivos</h3>
                            <p>Esta zona funcionará como una barra de búsqueda visual.</p>
                        </div>

                        <button type="button" className="reaction-clear-button" onClick={limpiar} disabled={reactivos.length === 0}>Limpiar</button>
                    </div>

                    <div className="reaction-drop-zone">
                        {reactivos.length === 0 ? (
                            <div className="reaction-empty-state">
                                <span>➕</span>
                                <p>Añade elementos para formar una consulta química.</p>
                            </div>
                        ) : (
                            <div className="reaction-token-row">
                                {reactivos.map((elemento, index) => (
                                    <div className="reaction-token-wrapper" key={elemento.id}>
                                        {index > 0 && <span className="reaction-plus">+</span>}
                                        <button
                                            type="button"
                                            className="reaction-token"
                                            onClick={() => quitarElemento(elemento.id)}
                                            title="Quitar elemento"
                                        >
                                            <strong>{elemento.simbolo}</strong>
                                            <small>{elemento.nombre}</small>
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="reaction-query-card">
                        <span>Consulta generada</span>
                        <strong>{queryVisual || "—"}</strong>
                    </div>

                    {resultadoSimulado && (
                        <div className="reaction-result-card">
                            <h3>Resultado preparado</h3>
                            <p>{resultadoSimulado.mensaje}</p>
                            {resultadoSimulado.query && <code>{resultadoSimulado.query}</code>}
                        </div>
                    )}
                </section>
            </div>
        </section>
    );
}

function construirQueryVisual(elementos) {
    if (!Array.isArray(elementos) || elementos.length === 0) {
        return "";
    }

    return elementos.map((elemento) => elemento.simbolo).join(" + ");
}

export default ReactionSandbox;
