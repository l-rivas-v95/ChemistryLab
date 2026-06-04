import { useEffect, useState } from "react";
import Navbar from "./components/navbar/Navbar";
import PeriodicTable from "./components/periodicTable/PeriodicTable";
import ElementCard from "./components/elementCard/ElementCard";
import CategoryLegend from "./components/legend/CategoryLegend";
import MoleculeList from "./components/molecules/MoleculeList";
import "./App.css";

function App() {
    const [vistaActiva, setVistaActiva] = useState("elementos");

    const [elementos, setElementos] = useState([]);
    const [elementoSeleccionado, setElementoSeleccionado] = useState(null);

    const [moleculas, setMoleculas] = useState([]);
    const [paginaMoleculas, setPaginaMoleculas] = useState(0);
    const [totalPaginasMoleculas, setTotalPaginasMoleculas] = useState(0);
    const [totalMoleculas, setTotalMoleculas] = useState(0);

    const [textoBusquedaMoleculas, setTextoBusquedaMoleculas] = useState("");
    const [busquedaMoleculas, setBusquedaMoleculas] = useState("");

    const [categoriaMoleculas, setCategoriaMoleculas] = useState("all");
    const [familiaMoleculas, setFamiliaMoleculas] = useState("all");

    const [requestKeyMoleculas, setRequestKeyMoleculas] = useState(0);
    const [requestKeyMoleculasFinalizada, setRequestKeyMoleculasFinalizada] = useState(0);

    const cargandoMoleculas = requestKeyMoleculas !== requestKeyMoleculasFinalizada;

    useEffect(() => {
        fetch("http://localhost:8080/api/elementos")
            .then((response) => response.json())
            .then((data) => setElementos(data))
            .catch((error) => console.error("Error cargando elementos:", error));
    }, []);

    useEffect(() => {
        const controller = new AbortController();

        const params = new URLSearchParams();
        params.set("page", String(paginaMoleculas));
        params.set("size", "10");

        if (busquedaMoleculas.trim()) {
            params.set("search", busquedaMoleculas.trim());
        }

        if (categoriaMoleculas && categoriaMoleculas !== "all") {
            params.set("categoria", categoriaMoleculas);
        }

        if (familiaMoleculas && familiaMoleculas !== "all") {
            params.set("familia", familiaMoleculas);
        }

        fetch(`http://localhost:8080/api/moleculas?${params.toString()}`, {
            signal: controller.signal
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Error HTTP " + response.status);
                }

                return response.json();
            })
            .then((data) => {
                console.log("Página de moléculas cargada:", data);

                setMoleculas(Array.isArray(data.content) ? data.content : []);
                setTotalPaginasMoleculas(data.totalPages || 0);
                setTotalMoleculas(data.totalElements || 0);
            })
            .catch((error) => {
                if (error.name === "AbortError") {
                    return;
                }

                console.error("Error cargando moléculas:", error);
                setMoleculas([]);
                setTotalPaginasMoleculas(0);
                setTotalMoleculas(0);
            })
            .finally(() => {
                setRequestKeyMoleculasFinalizada(requestKeyMoleculas);
            });

        return () => {
            controller.abort();
        };
    }, [
        paginaMoleculas,
        busquedaMoleculas,
        categoriaMoleculas,
        familiaMoleculas,
        requestKeyMoleculas
    ]);

    const cargarPaginaMoleculas = (nuevaPagina) => {
        if (cargandoMoleculas) {
            return;
        }

        setPaginaMoleculas(nuevaPagina);
        setRequestKeyMoleculas((key) => key + 1);
    };

    const irPaginaMoleculasAnterior = () => {
        const nuevaPagina = Math.max(paginaMoleculas - 1, 0);
        cargarPaginaMoleculas(nuevaPagina);
    };

    const irPaginaMoleculasSiguiente = () => {
        const ultimaPagina = Math.max(totalPaginasMoleculas - 1, 0);
        const nuevaPagina = Math.min(paginaMoleculas + 1, ultimaPagina);
        cargarPaginaMoleculas(nuevaPagina);
    };

    const cambiarTextoBusquedaMoleculas = (texto) => {
        setTextoBusquedaMoleculas(texto);
    };

    const buscarMoleculas = () => {
        if (cargandoMoleculas) {
            return;
        }

        setBusquedaMoleculas(textoBusquedaMoleculas);
        setPaginaMoleculas(0);
        setRequestKeyMoleculas((key) => key + 1);
    };

    const limpiarBusquedaMoleculas = () => {
        if (cargandoMoleculas) {
            return;
        }

        setTextoBusquedaMoleculas("");
        setBusquedaMoleculas("");
        setPaginaMoleculas(0);
        setRequestKeyMoleculas((key) => key + 1);
    };

    const cambiarFiltrosMoleculas = (nuevaCategoria, nuevaFamilia) => {
        if (cargandoMoleculas) {
            return;
        }

        setCategoriaMoleculas(nuevaCategoria);
        setFamiliaMoleculas(nuevaFamilia);
        setPaginaMoleculas(0);
        setRequestKeyMoleculas((key) => key + 1);
    };

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