import { API_BASE_URL } from "../config/api";

export async function getMolecules(params) {
    const response = await fetch(`${API_BASE_URL}/moleculas?${params.toString()}`);

    if (!response.ok) {
        throw new Error(`Error HTTP ${response.status}`);
    }

    return response.json();
}

export async function importMolecule(query) {
    const response = await fetch(`${API_BASE_URL}/moleculas/importar`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ query })
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Error HTTP ${response.status}`);
    }

    return response.json();
}

export async function getMoleculeRepresentation(id, signal) {
    const response = await fetch(`${API_BASE_URL}/moleculas/${id}/representacion`, {
        signal
    });

    if (!response.ok) {
        throw new Error(`Error HTTP ${response.status}`);
    }

    return response.json();
}
