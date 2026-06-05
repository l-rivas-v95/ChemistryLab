import { API_BASE_URL } from "../config/api";

export async function getElements() {
    const response = await fetch(`${API_BASE_URL}/elementos`);

    if (!response.ok) {
        throw new Error(`Error HTTP ${response.status}`);
    }

    return response.json();
}
