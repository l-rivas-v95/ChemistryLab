import { useEffect, useState } from "react";
import { getMolecules } from "../services/moleculeService";

export function useMolecules(searchParams) {
    const [moleculas, setMoleculas] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!searchParams) {
            return;
        }

        setLoading(true);

        getMolecules(searchParams)
            .then((data) => {
                setMoleculas(data);
                setError(null);
            })
            .catch((err) => {
                console.error("Error cargando moléculas:", err);
                setError(err);
            })
            .finally(() => setLoading(false));
    }, [searchParams]);

    return {
        moleculas,
        loading,
        error
    };
}
