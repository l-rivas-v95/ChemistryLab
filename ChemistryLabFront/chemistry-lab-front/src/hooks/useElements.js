import { useEffect, useState } from "react";
import { getElements } from "../services/elementService";

export function useElements() {
    const [elementos, setElementos] = useState([]);

    useEffect(() => {
        getElements()
            .then((data) => setElementos(data))
            .catch((error) => console.error("Error cargando elementos:", error));
    }, []);

    return { elementos };
}
