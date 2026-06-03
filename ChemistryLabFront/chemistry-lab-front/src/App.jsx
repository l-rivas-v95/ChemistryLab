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

    useEffect(() => {
        fetch("http://localhost:8080/api/elementos")
            .then((response) => response.json())
            .then((data) => setElementos(data))
            .catch((error) => console.error("Error cargando elementos:", error));
    }, []);

    useEffect(() => {
        fetch("http://localhost:8080/api/moleculas")
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Error HTTP " + response.status);
                }
                return response.json();
            })
            .then((data) => {
                console.log("Moléculas cargadas:", data);
                setMoleculas(Array.isArray(data) ? data : []);
            })
            .catch((error) => {
                console.error("Error cargando moléculas:", error);
                setMoleculas([]);
            });
    }, []);

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
                    <>
                        {moleculas.length > 0 ? (
                            <MoleculeList moleculas={moleculas} />
                        ) : (
                            <div style={{ padding: "40px", fontSize: "24px", fontWeight: "bold" }}>
                                No se han cargado moléculas. Revisa si el backend está levantado y si existe
                                el endpoint http://localhost:8080/api/moleculas
                            </div>
                        )}
                    </>
                )}
            </section>
        </main>
    );
}

export default App;