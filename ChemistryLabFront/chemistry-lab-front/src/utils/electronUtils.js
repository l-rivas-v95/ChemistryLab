export function formatearConfiguracionMoller(configuracion) {
    if (!configuracion) return [];

    const orbitalesElemento = configuracion.split(" ");

    const mapa = {};
    orbitalesElemento.forEach((orbital) => {
        const clave = orbital.replace(/\d+$/, "");
        mapa[clave] = orbital;
    });

    const estructura = [
        ["1s", "", "", ""],
        ["2s", "2p", "", ""],
        ["3s", "3p", "3d", ""],
        ["4s", "4p", "4d", "4f"],
        ["5s", "5p", "5d", "5f"],
        ["6s", "6p", "6d", ""],
        ["7s", "7p", "", ""],
        ["8s", "", "", ""],
    ];

    return estructura.map((fila, index) => ({
        nivel: `n=${index + 1}`,
        orbitales: fila.map((clave) => {
            if (clave === "") {
                return {
                    clave: "",
                    texto: "",
                    relleno: false,
                };
            }

            return {
                clave,
                texto: mapa[clave] || clave,
                relleno: Boolean(mapa[clave]),
            };
        }),
    }));
}