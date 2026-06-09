import { API_BASE_URL } from "../config/api";

export async function balanceReaction(reactants, products) {
    const response = await fetch(`${API_BASE_URL}/reactions/balance`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ reactants, products })
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Error HTTP ${response.status}`);
    }

    return response.json();
}
