import { useState } from "react";
import MoleculeCard from "./MoleculeCard";
import MoleculeDetailCard from "./MoleculeDetailCard";
import "./Molecule.css";

const FILTROS_PRINCIPALES = [
    { value: "all", label: "Todas" },
    { value: "organic", label: "Orgánicas" },
    { value: "inorganic", label: "Inorgánicas" }
];

const FILTROS_ORGANICOS = [
    { value: "all-organic", label: "Todas orgánicas" },
    { value: "amine", label: "Aminas" },
    { value: "amide", label: "Amidas" },
    { value: "alcohol", label: "Alcoholes" },
    { value: "organic-acid", label: "Ácidos orgánicos" },
    { value: "carbohydrate", label: "Carbohidratos" },
    { value: "bioactive", label: "Fármacos / bioactivas" }
];

const FILTROS_INORGANICOS = [
    { value: "all-inorganic", label: "Todas inorgánicas" },
    { value: "acid", label: "Ácidos" },
    { value: "base", label: "Bases / hidróxidos" },
    { value: "oxide", label: "Óxidos" },
    { value: "salt", label: "Sales" },
    { value: "other-inorganic", label: "Otros inorgánicos" }
];

function MoleculeList({
                          moleculas,
                          paginaActual,
                          totalPaginas,
                          totalMoleculas,
                          cargando,
                          textoBusqueda,
                          busqueda,
                          categoria,
                          familia,
                          onTextoBusquedaChange,
                          onBuscar,
                          onLimpiarBusqueda,
                          onCambiarFiltros,
                          onPaginaAnterior,
                          onPaginaSiguiente
                      }) {
    const [moleculaSeleccionada, setMoleculaSeleccionada] = useState(null);

    const filtrosSecundarios =
        categoria === "organic"
            ? FILTROS_ORGANICOS
            : categoria === "inorganic"
                ? FILTROS_INORGANICOS
                : [];

    const cambiarCategoria = (nuevaCategoria) => {
        if (nuevaCategoria === "organic") {
            onCambiarFiltros("organic", "all-organic");
            return;
        }

        if (nuevaCategoria === "inorganic") {
            onCambiarFiltros("inorganic", "all-inorganic");
            return;
        }

        onCambiarFiltros("all", "all");
    };

    return (
        <section className="moleculas-page">
            <div className="moleculas-header">
                <div>
                    <h2>Moléculas</h2>
                    <p>Compuestos guardados en tu base de datos.</p>
                </div>

                <span className="moleculas-count">
                    {totalMoleculas} compuestos
                </span>
            </div>

            <div className="moleculas-search">
                <input
                    type="text"
                    value={textoBusqueda}
                    onChange={(event) => onTextoBusquedaChange(event.target.value)}
                    onKeyDown={(event) => {
                        if (event.key === "Enter" && !cargando) {
                            onBuscar();
                        }
                    }}
                    placeholder="Buscar por nombre, fórmula, tipo o sinónimos..."
                />

                <button type="button" onClick={onBuscar} disabled={cargando}>
                    Buscar
                </button>

                {busqueda && (
                    <button type="button" onClick={onLimpiarBusqueda} disabled={cargando}>
                        Limpiar
                    </button>
                )}
            </div>

            <div className="moleculas-filter-panel">
                <div className="moleculas-filter-row">
                    <span className="moleculas-filter-title">Tipo principal</span>

                    <div className="moleculas-filter-buttons">
                        {FILTROS_PRINCIPALES.map((filtro) => (
                            <button
                                key={filtro.value}
                                type="button"
                                disabled={cargando}
                                className={
                                    categoria === filtro.value
                                        ? `moleculas-filter-button moleculas-filter-button-active moleculas-filter-${filtro.value}`
                                        : `moleculas-filter-button moleculas-filter-${filtro.value}`
                                }
                                onClick={() => cambiarCategoria(filtro.value)}
                            >
                                {filtro.label}
                            </button>
                        ))}
                    </div>
                </div>

                {filtrosSecundarios.length > 0 && (
                    <div className="moleculas-filter-row moleculas-filter-row-secondary">
                        <span className="moleculas-filter-title">
                            {categoria === "organic" ? "Familia orgánica" : "Familia inorgánica"}
                        </span>

                        <div className="moleculas-filter-buttons">
                            {filtrosSecundarios.map((filtro) => (
                                <button
                                    key={filtro.value}
                                    type="button"
                                    disabled={cargando}
                                    className={
                                        familia === filtro.value
                                            ? `moleculas-filter-button moleculas-filter-button-active moleculas-filter-${filtro.value}`
                                            : `moleculas-filter-button moleculas-filter-${filtro.value}`
                                    }
                                    onClick={() => onCambiarFiltros(categoria, filtro.value)}
                                >
                                    {filtro.label}
                                </button>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            <div className="moleculas-filter-info">
                Mostrando {moleculas.length} moléculas de {totalMoleculas} resultados
            </div>

            <Pagination
                paginaActual={paginaActual}
                totalPaginas={totalPaginas}
                cargando={cargando}
                onPaginaAnterior={onPaginaAnterior}
                onPaginaSiguiente={onPaginaSiguiente}
            />

            {moleculas.length > 0 ? (
                <div className="moleculas-grid">
                    {moleculas.map((molecula) => (
                        <MoleculeCard
                            key={molecula.id}
                            molecula={molecula}
                            onClick={() => setMoleculaSeleccionada(molecula)}
                        />
                    ))}
                </div>
            ) : (
                <div className="moleculas-empty">
                    {cargando
                        ? "Cargando moléculas..."
                        : "No hay moléculas para ese filtro."}
                </div>
            )}

            <Pagination
                bottom
                paginaActual={paginaActual}
                totalPaginas={totalPaginas}
                cargando={cargando}
                onPaginaAnterior={onPaginaAnterior}
                onPaginaSiguiente={onPaginaSiguiente}
            />

            {moleculaSeleccionada?.id && (
                <MoleculeDetailCard
                    molecula={moleculaSeleccionada}
                    onClose={() => setMoleculaSeleccionada(null)}
                />
            )}
        </section>
    );
}

function Pagination({
                        paginaActual,
                        totalPaginas,
                        cargando,
                        bottom,
                        onPaginaAnterior,
                        onPaginaSiguiente
                    }) {
    return (
        <div className={bottom ? "moleculas-pagination moleculas-pagination-bottom" : "moleculas-pagination"}>
            <button
                type="button"
                onClick={onPaginaAnterior}
                disabled={cargando || paginaActual === 0}
            >
                ← Anterior
            </button>

            <span>
                {cargando
                    ? "Cargando..."
                    : `Página ${paginaActual + 1} de ${totalPaginas || 1}`}
            </span>

            <button
                type="button"
                onClick={onPaginaSiguiente}
                disabled={cargando || paginaActual + 1 >= totalPaginas}
            >
                Siguiente →
            </button>
        </div>
    );
}

export default MoleculeList;