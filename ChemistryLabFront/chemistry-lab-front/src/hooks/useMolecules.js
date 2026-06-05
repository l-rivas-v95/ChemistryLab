import { useEffect, useState } from "react";
import { getMolecules } from "../services/moleculeService";

export function useMolecules() {
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

        getMolecules(params)
            .then((data) => {
                console.log("Página de moléculas cargada:", data);

                setMoleculas(Array.isArray(data.content) ? data.content : []);
                setTotalPaginasMoleculas(data.totalPages || 0);
                setTotalMoleculas(data.totalElements || 0);
            })
            .catch((error) => {
                console.error("Error cargando moléculas:", error);
                setMoleculas([]);
                setTotalPaginasMoleculas(0);
                setTotalMoleculas(0);
            })
            .finally(() => {
                setRequestKeyMoleculasFinalizada(requestKeyMoleculas);
            });
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

    return {
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
    };
}
