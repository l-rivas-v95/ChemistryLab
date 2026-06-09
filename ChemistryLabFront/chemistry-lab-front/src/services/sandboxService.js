import { API_BASE_URL } from "../config/api";

export async function suggestSandboxProducts(elementos) {
    const response = await fetch(`${API_BASE_URL}/sandbox/suggest`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ elementos })
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Error HTTP ${response.status}`);
    }

    return response.json();
}
