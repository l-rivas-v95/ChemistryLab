import { useEffect, useState } from "react";
import { getMoleculeRepresentation } from "../services/moleculeService";

export function useMoleculeRepresentation(moleculeId) {
    const [representacion, setRepresentacion] = useState(null);
    const [error, setError] = useState(false);

    useEffect(() => {
        if (!moleculeId) {
            setRepresentacion(null);
            setError(false);
            return;
        }

        const controller = new AbortController();

        getMoleculeRepresentation(moleculeId, controller.signal)
            .then((data) => {
                setRepresentacion(data);
                setError(false);
            })
            .catch((fetchError) => {
                if (fetchError.name === "AbortError") {
                    return;
                }

                console.error("Error cargando representación:", fetchError);
                setError(true);
                setRepresentacion(null);
            });

        return () => {
            controller.abort();
        };
    }, [moleculeId]);

    return {
        representacion,
        error
    };
}
