import { useState } from "react";
import Navbar from "./components/navbar/Navbar";
import PeriodicTable from "./components/periodicTable/PeriodicTable";
import ElementCard from "./components/elementCard/ElementCard";
import CategoryLegend from "./components/legend/CategoryLegend";
import MoleculeList from "./components/molecules/MoleculeList";
import { useElements } from "./hooks/useElements";
import { useMolecules } from "./hooks/useMolecules";
import "./App.css";

function App() {
    const [vistaActiva, setVistaActiva] = useState("elementos");
    const [elementoSeleccionado, setElementoSeleccionado] = useState(null);

    const { elementos } = useElements();
    const {
        moleculas,
        paginaMoleculas,
        totalPaginasMoleculas,
        totalMoleculas,
        textoBusquedaMoleculas,
        busquedaMoleculas,
        categoriaMoleculas,
        familiaMoleculas,
        cargandoMoleculas,
        cambiarTextoBusquedaMoleculas,
        buscarMoleculas,
        limpiarBusquedaMoleculas,
        cambiarFiltrosMoleculas,
        irPaginaMoleculasAnterior,
        irPaginaMoleculasSiguiente
    } = useMolecules();

    return (
        <main className="app">
            <Navbar
                vistaActiva={vistaActiva}
                setVistaActiva={setVistaActiva}
            />

            <section className="app-contenido">
                {vistaActiva === "elementos" && (
                    <>
                        <CategoryLegend />

                        <PeriodicTable
                            elementos={elementos}
                            onElementoClick={setElementoSeleccionado}
                        />

                        {elementoSeleccionado && (
                            <ElementCard
                                elemento={elementoSeleccionado}
                                onClose={() => setElementoSeleccionado(null)}
                            />
                        )}
                    </>
                )}

                {vistaActiva === "moleculas" && (
                    <MoleculeList
                        moleculas={moleculas}
                        paginaActual={paginaMoleculas}
                        totalPaginas={totalPaginasMoleculas}
                        totalMoleculas={totalMoleculas}
                        cargando={cargandoMoleculas}
                        textoBusqueda={textoBusquedaMoleculas}
                        busqueda={busquedaMoleculas}
                        categoria={categoriaMoleculas}
                        familia={familiaMoleculas}
                        onTextoBusquedaChange={cambiarTextoBusquedaMoleculas}
                        onBuscar={buscarMoleculas}
                        onLimpiarBusqueda={limpiarBusquedaMoleculas}
                        onCambiarFiltros={cambiarFiltrosMoleculas}
                        onPaginaAnterior={irPaginaMoleculasAnterior}
                        onPaginaSiguiente={irPaginaMoleculasSiguiente}
                    />
                )}
            </section>
        </main>
    );
}

export default App;
