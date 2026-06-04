export function getClaseCategoria(categoria) {
    if (!categoria) return "cat-desconocida";

    const texto = categoria.toLowerCase();

    if (texto.includes("alkali metal") || texto.includes("metal alcalino")) {
        return "cat-alcalino";
    }

    if (
        texto.includes("alkaline earth metal") ||
        texto.includes("alcalinotérreo") ||
        texto.includes("alcalinoterreo")
    ) {
        return "cat-alcalinoterreo";
    }

    if (texto.includes("transition metal") || texto.includes("metal de transición")) {
        return "cat-transicion";
    }

    if (texto.includes("post-transition metal") || texto.includes("post-transición")) {
        return "cat-postransicion";
    }

    if (texto.includes("metalloid") || texto.includes("metaloide")) {
        return "cat-metaloide";
    }

    if (texto.includes("noble gas") || texto.includes("gas noble")) {
        return "cat-gas-noble";
    }

    if (
        texto.includes("diatomic nonmetal") ||
        texto.includes("polyatomic nonmetal") ||
        texto.includes("nonmetal") ||
        texto.includes("no metal")
    ) {
        return "cat-no-metal";
    }

    if (texto.includes("lanthanide") || texto.includes("lantánido")) {
        return "cat-lantanido";
    }

    if (texto.includes("actinide") || texto.includes("actínido")) {
        return "cat-actinido";
    }

    return "cat-desconocida";
}