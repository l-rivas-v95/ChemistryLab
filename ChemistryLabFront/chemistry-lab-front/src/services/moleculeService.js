import { API_BASE_URL } from "../config/api";

export async function getMolecules(params) {
    const response = await fetch(`${API_BASE_URL}/moleculas?${params.toString()}`);

    if (!response.ok) {
        throw new Error(`Error HTTP ${response.status}`);
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
