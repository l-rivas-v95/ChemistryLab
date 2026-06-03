import { useMemo, useState } from "react";
import MoleculeCard from "./MoleculeCard";
import MoleculeDetailCard from "./MoleculeDetailCard";
import "./Molecule.css";

const MOLECULAS_POR_PAGINA = 12;

function MoleculeList({ moleculas }) {
    const [paginaActual, setPaginaActual] = useState(1);
    const [moleculaSeleccionada, setMoleculaSeleccionada] = useState(null);

    const totalPaginas = Math.ceil(moleculas.length / MOLECULAS_POR_PAGINA);

    const moleculasPagina = useMemo(() => {
        const inicio = (paginaActual - 1) * MOLECULAS_POR_PAGINA;
        const fin = inicio + MOLECULAS_POR_PAGINA;
        return moleculas.slice(inicio, fin);
    }, [moleculas, paginaActual]);

    const irPaginaAnterior = () => {
        setPaginaActual((pagina) => Math.max(pagina - 1, 1));
    };

    const irPaginaSiguiente = () => {
        setPaginaActual((pagina) => Math.min(pagina + 1, totalPaginas));
    };

    return (
        <section className="moleculas-page">
            <div className="moleculas-header">
                <div>
                    <h2>Moléculas inorgánicas</h2>
                    <p>Compuestos obtenidos desde PubChem y guardados en tu base de datos.</p>
                </div>

                <span className="moleculas-count">
                    {moleculas.length} compuestos
                </span>
            </div>

            <div className="moleculas-pagination">
                <button onClick={irPaginaAnterior} disabled={paginaActual === 1}>
                    ← Anterior
                </button>

                <span>
                    Página {paginaActual} de {totalPaginas || 1}
                </span>

                <button onClick={irPaginaSiguiente} disabled={paginaActual === totalPaginas}>
                    Siguiente →
                </button>
            </div>

            <div className="moleculas-grid">
                {moleculasPagina.map((molecula) => (
                    <MoleculeCard
                        key={molecula.id}
                        molecula={molecula}
                        onClick={() => setMoleculaSeleccionada(molecula)}
                    />
                ))}
            </div>

            <div className="moleculas-pagination moleculas-pagination-bottom">
                <button onClick={irPaginaAnterior} disabled={paginaActual === 1}>
                    ← Anterior
                </button>

                <span>
                    Página {paginaActual} de {totalPaginas || 1}
                </span>

                <button onClick={irPaginaSiguiente} disabled={paginaActual === totalPaginas}>
                    Siguiente →
                </button>
            </div>

            {moleculaSeleccionada && (
                <MoleculeDetailCard
                    molecula={moleculaSeleccionada}
                    onClose={() => setMoleculaSeleccionada(null)}
                />
            )}
        </section>
    );
}

export default MoleculeList;