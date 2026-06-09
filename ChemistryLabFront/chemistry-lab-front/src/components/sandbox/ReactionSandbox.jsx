import { useMemo, useState } from "react";
import { suggestSandboxProducts } from "../../services/sandboxService";
import { balanceReaction } from "../../services/reactionService";
import "./ReactionSandbox.css";

const QUICK_ELEMENTS = ["H", "O", "C", "N", "Na", "Cl", "Fe", "Ca", "K", "S", "P", "Mg", "Al", "Cu", "Zn"];
const DIATOMIC_ELEMENTS = new Set(["H", "N", "O", "F", "Cl", "Br", "I"]);
let tokenCounter = 0;

function ReactionSandbox({ elementos = [] }) {
    const [reactivos, setReactivos] = useState([]);
    const [resultado, setResultado] = useState(null);
    const [productoSeleccionado, setProductoSeleccionado] = useState(null);
    const [reaccionBalanceada, setReaccionBalanceada] = useState(null);
    const [balanceando, setBalanceando] = useState(false);
    const [cargando, setCargando] = useState(false);
    const [error, setError] = useState("");

    const elementosDisponibles = useMemo(() => {
        const porSimbolo = new Map();
        elementos.forEach((elemento) => {
            if (elemento?.simbolo) porSimbolo.set(elemento.simbolo, elemento);
        });
        return QUICK_ELEMENTS.map((simbolo) => porSimbolo.get(simbolo) || { simbolo, nombre: simbolo });
    }, [elementos]);

    const resumenReactivos = useMemo(() => agruparReactivos(reactivos), [reactivos]);
    const queryVisual = useMemo(() => construirQueryVisual(resumenReactivos), [resumenReactivos]);
    const formulaEntrada = useMemo(() => construirFormulaEntrada(resumenReactivos), [resumenReactivos]);
    const reaccionVisual = useMemo(() => construirReaccionVisual(resumenReactivos, productoSeleccionado, reaccionBalanceada), [resumenReactivos, productoSeleccionado, reaccionBalanceada]);

    const resetBusqueda = () => {
        setResultado(null);
        setProductoSeleccionado(null);
        setReaccionBalanceada(null);
        setError("");
    };

    const agregarElemento = (elemento) => {
        if (!elemento?.simbolo) return;
        tokenCounter += 1;
        setReactivos((actuales) => [...actuales, { id: `${elemento.simbolo}-${Date.now()}-${tokenCounter}`, simbolo: elemento.simbolo, nombre: elemento.nombre || elemento.simbolo }]);
        resetBusqueda();
    };

    const quitarElemento = (id) => {
        setReactivos((actuales) => actuales.filter((elemento) => elemento.id !== id));
        resetBusqueda();
    };

    const quitarUltimoDeSimbolo = (simbolo) => {
        setReactivos((actuales) => {
            const index = [...actuales].reverse().findIndex((elemento) => elemento.simbolo === simbolo);
            if (index === -1) return actuales;
            const realIndex = actuales.length - 1 - index;
            return actuales.filter((_, posicion) => posicion !== realIndex);
        });
        resetBusqueda();
    };

    const limpiar = () => {
        setReactivos([]);
        resetBusqueda();
    };

    const probar = async () => {
        if (resumenReactivos.length === 0) return;
        setCargando(true);
        setError("");
        setResultado(null);
        setProductoSeleccionado(null);
        setReaccionBalanceada(null);
        try {
            const response = await suggestSandboxProducts(resumenReactivos.map((elemento) => ({ simbolo: elemento.simbolo, cantidad: elemento.cantidad })));
            setResultado(response);
        } catch (err) {
            setError(err.message || "No se pudieron buscar compuestos.");
        } finally {
            setCargando(false);
        }
    };

    const seleccionarProducto = async (suggestion) => {
        setProductoSeleccionado(suggestion);
        setReaccionBalanceada(null);
        setBalanceando(true);
        setError("");

        try {
            const reactantFormulas = construirReactivosParaBalance(resumenReactivos).map((reactivo) => reactivo.formula);
            const response = await balanceReaction(reactantFormulas, [suggestion.formula]);
            setReaccionBalanceada(response);
        } catch (err) {
            setError(err.message || "No se pudo balancear la reacción.");
        } finally {
            setBalanceando(false);
        }
    };

    return (
        <section className="reaction-sandbox-page">
            <header className="reaction-sandbox-hero">
                <div>
                    <span className="reaction-sandbox-kicker">Sandbox químico</span>
                    <h2>Construye una búsqueda con elementos</h2>
                    <p>Selecciona elementos como si fuera un laboratorio visual. Por dentro se genera una consulta tipo <b>Fe + O</b> para buscar compuestos posibles en la base de datos.</p>
                </div>
                <div className="reaction-sandbox-equation-preview">
                    <span>{queryVisual || "Añade elementos"}</span>
                    <button type="button" onClick={probar} disabled={reactivos.length === 0 || cargando}>{cargando ? "Buscando..." : "Buscar compuestos"}</button>
                </div>
            </header>

            <div className="reaction-sandbox-layout">
                <aside className="reaction-element-palette">
                    <h3>Elementos rápidos</h3>
                    <p>Haz clic para añadirlos a la mezcla.</p>
                    <div className="reaction-element-grid">
                        {elementosDisponibles.map((elemento) => (
                            <button type="button" key={elemento.simbolo} className="reaction-element-tile" onClick={() => agregarElemento(elemento)} title={elemento.nombre || elemento.simbolo}>
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
                            <p>Esta zona funciona como una barra de búsqueda visual.</p>
                        </div>
                        <button type="button" className="reaction-clear-button" onClick={limpiar} disabled={reactivos.length === 0}>Limpiar</button>
                    </div>

                    <div className="reaction-drop-zone">
                        {reactivos.length === 0 ? (
                            <div className="reaction-empty-state"><span>➕</span><p>Añade elementos para formar una consulta química.</p></div>
                        ) : (
                            <div className="reaction-token-row">
                                {reactivos.map((elemento, index) => (
                                    <div className="reaction-token-wrapper" key={elemento.id}>
                                        {index > 0 && <span className="reaction-plus">+</span>}
                                        <button type="button" className="reaction-token" onClick={() => quitarElemento(elemento.id)} title="Quitar elemento">
                                            <strong>{elemento.simbolo}</strong>
                                            <small>{elemento.nombre}</small>
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="reaction-summary-panel">
                        <span className="reaction-summary-label">Resumen</span>
                        {resumenReactivos.length === 0 ? (
                            <span className="reaction-summary-empty">No hay elementos seleccionados.</span>
                        ) : resumenReactivos.map((elemento) => (
                            <button type="button" className="reaction-summary-chip" key={elemento.simbolo} onClick={() => quitarUltimoDeSimbolo(elemento.simbolo)} title="Quitar una unidad">
                                <strong>{elemento.simbolo}</strong><span>x{elemento.cantidad}</span>
                            </button>
                        ))}
                    </div>

                    <div className="reaction-formula-strip">
                        <span>Fórmula construida</span>
                        <strong>{resultado?.formulaEntrada || formulaEntrada || "—"}</strong>
                        <small>{queryVisual || "Sin elementos"}</small>
                    </div>

                    <div className="reaction-suggestions-panel">
                        <h3>Compuestos encontrados</h3>
                        {cargando && <div className="reaction-suggestions-empty"><span>⏳</span><p>Buscando compuestos compatibles...</p></div>}
                        {error && <div className="reaction-error-card">{error}</div>}
                        {!cargando && !error && resultado && (
                            resultado.suggestions?.length > 0 ? (
                                <div className="reaction-suggestion-grid">
                                    {resultado.suggestions.map((suggestion) => (
                                        <button type="button" onClick={() => seleccionarProducto(suggestion)} className={`reaction-suggestion-card ${suggestion.exactMatch ? "reaction-suggestion-card-exact" : ""} ${productoSeleccionado?.id === suggestion.id ? "reaction-suggestion-card-selected" : ""}`} key={`${suggestion.id}-${suggestion.formula}`}>
                                            <span>{suggestion.exactMatch ? "Coincidencia exacta" : suggestion.compoundFamily || "Compuesto"}</span>
                                            <strong><FormulaText formula={suggestion.formula} /></strong>
                                            <small>{suggestion.nombre}</small>
                                        </button>
                                    ))}
                                </div>
                            ) : (
                                <div className="reaction-suggestions-empty"><span>🧪</span><p>No hay compuestos registrados solo con esos elementos.</p></div>
                            )
                        )}
                        {!cargando && !error && !resultado && <div className="reaction-suggestions-empty"><span>🔎</span><p>Construye una combinación y pulsa Buscar compuestos.</p></div>}
                    </div>

                    {productoSeleccionado && reaccionVisual && (
                        <div className="reaction-equation-panel">
                            <div className="reaction-equation-header">
                                <span>{reaccionVisual.balanced ? "Reacción ajustada" : "Reacción propuesta"}</span>
                                <strong>{productoSeleccionado.nombre}</strong>
                            </div>
                            <div className="reaction-equation-row">
                                <div className="reaction-equation-side">
                                    {reaccionVisual.reactivos.map((reactivo, index) => (
                                        <div className="reaction-equation-part" key={`${reactivo.formula}-${index}`}>
                                            {index > 0 && <span className="reaction-equation-plus">+</span>}
                                            <ReactionTerm term={reactivo} />
                                        </div>
                                    ))}
                                </div>
                                <span className="reaction-equation-arrow">→</span>
                                <ReactionTerm term={{ ...reaccionVisual.producto, label: productoSeleccionado.nombre }} product />
                            </div>
                            <p className="reaction-equation-note">{balanceando ? "Ajustando reacción..." : reaccionVisual.message}</p>
                        </div>
                    )}
                </section>
            </div>
        </section>
    );
}

function ReactionTerm({ term, product = false }) {
    return (
        <div className={product ? "reaction-equation-product" : "reaction-equation-term"}>
            <div className="reaction-formula-composed">
                {term.coefficient > 1 && <span className="reaction-coefficient">{term.coefficient}</span>}
                <strong><FormulaText formula={term.formula} /></strong>
            </div>
            <small>{term.label}</small>
        </div>
    );
}

function FormulaText({ formula }) {
    return String(formula || "").split(/(\d+)/).map((part, index) => (
        /^\d+$/.test(part) ? <sub key={`${part}-${index}`}>{part}</sub> : <span key={`${part}-${index}`}>{part}</span>
    ));
}

function agruparReactivos(elementos) {
    const mapa = new Map();
    elementos.forEach((elemento) => {
        const actual = mapa.get(elemento.simbolo) || { simbolo: elemento.simbolo, nombre: elemento.nombre, cantidad: 0 };
        actual.cantidad += 1;
        mapa.set(elemento.simbolo, actual);
    });
    return [...mapa.values()];
}

function construirQueryVisual(elementosAgrupados) {
    if (!Array.isArray(elementosAgrupados) || elementosAgrupados.length === 0) return "";
    return elementosAgrupados.map((elemento) => elemento.simbolo).join(" + ");
}

function construirFormulaEntrada(elementosAgrupados) {
    if (!Array.isArray(elementosAgrupados) || elementosAgrupados.length === 0) return "";
    return elementosAgrupados.map((elemento) => `${elemento.simbolo}${elemento.cantidad > 1 ? elemento.cantidad : ""}`).join("");
}

function construirReactivosParaBalance(resumenReactivos) {
    return resumenReactivos.map((elemento) => ({
        formula: DIATOMIC_ELEMENTS.has(elemento.simbolo) ? `${elemento.simbolo}2` : elemento.simbolo,
        label: elemento.nombre || elemento.simbolo,
        coefficient: 1
    }));
}

function construirReaccionVisual(resumenReactivos, producto, reaccionBalanceada) {
    if (!producto || !Array.isArray(resumenReactivos) || resumenReactivos.length === 0) return null;

    const reactivosBase = construirReactivosParaBalance(resumenReactivos);

    if (reaccionBalanceada?.balanced) {
        return {
            reactivos: reaccionBalanceada.reactants.map((term, index) => ({
                formula: term.formula,
                coefficient: term.coefficient,
                label: reactivosBase[index]?.label || term.formula
            })),
            producto: {
                formula: reaccionBalanceada.products?.[0]?.formula || producto.formula,
                coefficient: reaccionBalanceada.products?.[0]?.coefficient || 1
            },
            balanced: true,
            message: reaccionBalanceada.equation || "Reacción ajustada correctamente."
        };
    }

    return {
        reactivos: reactivosBase,
        producto: { formula: producto.formula, coefficient: 1 },
        balanced: false,
        message: reaccionBalanceada?.message || "Vista inicial sin coeficientes."
    };
}

export default ReactionSandbox;
