import { useEffect, useRef, useState } from "react";
import SmilesDrawer from "smiles-drawer";

const API_BASE_URL = "http://localhost:8080/api";

function MoleculeStructure({ molecula }) {
    const [representacion, setRepresentacion] = useState(null);
    const [error, setError] = useState(false);

    useEffect(() => {
        if (!molecula?.id) {
            setRepresentacion(null);
            return;
        }

        const controller = new AbortController();

        setError(false);

        fetch(`${API_BASE_URL}/moleculas/${molecula.id}/representacion`, {
            signal: controller.signal
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Error HTTP " + response.status);
                }

                return response.json();
            })
            .then((data) => {
                setRepresentacion(data);
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
    }, [molecula?.id]);

    if (error || !representacion) {
        return <FormulaStructure formula={molecula?.formula} />;
    }

    if (representacion.tipoRepresentacion === "SMILES") {
        return (
            <SmilesStructure
                smiles={representacion.canonicalSmiles || representacion.isomericSmiles}
                nombre={molecula?.nombre}
                formula={representacion.formulaVisual || molecula?.formula}
            />
        );
    }

    if (representacion.tipoRepresentacion === "VSEPR") {
        return <VseprStructure representacion={representacion} />;
    }

    if (representacion.tipoRepresentacion === "IONICA") {
        return (
            <IonicStructure
                formula={representacion.formulaVisual}
                texto={representacion.texto}
            />
        );
    }

    return <FormulaStructure formula={representacion.formulaVisual || molecula?.formula} />;
}

function SmilesStructure({ smiles, nombre, formula }) {
    const canvasRef = useRef(null);
    const [error, setError] = useState(false);

    useEffect(() => {
        if (!smiles || !canvasRef.current) {
            return;
        }

        let cancelled = false;
        const canvas = canvasRef.current;
        const context = canvas.getContext("2d");

        context.clearRect(0, 0, canvas.width, canvas.height);

        const drawer = new SmilesDrawer.Drawer({
            width: 260,
            height: 180,
            bondThickness: 1.5,
            bondLength: 30,
            bondSpacing: 5,
            fontSizeLarge: 16,
            fontSizeSmall: 10,
            padding: 24,
            compactDrawing: true,
            terminalCarbons: false,
            explicitHydrogens: false,
            themes: {
                light: {
                    C: "#222222",
                    O: "#e74c3c",
                    N: "#3498db",
                    F: "#27ae60",
                    CL: "#27ae60",
                    BR: "#8e44ad",
                    I: "#8e44ad",
                    P: "#e67e22",
                    S: "#f2a900",
                    B: "#e67e22",
                    H: "#666666",
                    BACKGROUND: "#ffffff"
                }
            }
        });

        SmilesDrawer.parse(
            smiles,
            (tree) => {
                if (cancelled) return;

                drawer.draw(tree, canvas, "light", false);
                setError(false);
            },
            (parseError) => {
                if (cancelled) return;

                console.error("Error dibujando SMILES:", parseError, smiles);
                setError(true);
            }
        );

        return () => {
            cancelled = true;
        };
    }, [smiles]);

    if (error || !smiles) {
        return <FormulaStructure formula={formula} />;
    }

    return (
        <canvas
            ref={canvasRef}
            width="260"
            height="180"
            aria-label={nombre || "Molécula"}
        />
    );
}

function VseprStructure({ representacion }) {
    const layout = getVseprLayout(representacion);

    return (
        <svg viewBox="0 0 260 180" className="formula-structure vsepr-structure">
            <rect x="0" y="0" width="260" height="180" rx="12" className="formula-bg" />

            {layout.bonds.map((bond, index) => (
                <line
                    key={`bond-${index}`}
                    x1={bond.x1}
                    y1={bond.y1}
                    x2={bond.x2}
                    y2={bond.y2}
                    className="vsepr-bond"
                />
            ))}

            {layout.atoms.map((atom, index) => (
                <text
                    key={`atom-${index}`}
                    x={atom.x}
                    y={atom.y}
                    textAnchor="middle"
                    dominantBaseline="middle"
                    className={`vsepr-atom vsepr-atom-${atom.symbol}`}
                >
                    {atom.symbol}
                </text>
            ))}

            {layout.lonePairs.map((pair, index) => (
                <text
                    key={`lp-${index}`}
                    x={pair.x}
                    y={pair.y}
                    textAnchor="middle"
                    dominantBaseline="middle"
                    className="vsepr-lone-pair"
                >
                    ··
                </text>
            ))}

            <text x="130" y="138" textAnchor="middle" className="vsepr-caption-main">
                {representacion.vsepr} · {representacion.geometria}
            </text>

            <text x="130" y="158" textAnchor="middle" className="vsepr-caption-sub">
                {representacion.polaridad}
            </text>
        </svg>
    );
}

function IonicStructure({ formula, texto }) {
    return (
        <svg viewBox="0 0 260 180" className="formula-structure">
            <rect x="0" y="0" width="260" height="180" rx="12" className="formula-bg" />

            <text x="130" y="54" textAnchor="middle" className="formula-big">
                {formula || "Sin fórmula"}
            </text>

            <text x="130" y="98" textAnchor="middle" className="formula-ion-view">
                {texto || "representación iónica"}
            </text>

            <text x="130" y="132" textAnchor="middle" className="formula-caption">
                representación formal
            </text>
        </svg>
    );
}

function FormulaStructure({ formula }) {
    return (
        <svg viewBox="0 0 260 180" className="formula-structure">
            <rect x="0" y="0" width="260" height="180" rx="12" className="formula-bg" />

            <text x="130" y="74" textAnchor="middle" className="formula-big">
                {formula || "Sin estructura"}
            </text>

            <text x="130" y="116" textAnchor="middle" className="formula-caption">
                fórmula química
            </text>
        </svg>
    );
}

function getVseprLayout(representacion) {
    const central = {
        symbol: representacion.atomoCentral,
        x: 130,
        y: 78
    };

    const terminales = representacion.atomosTerminales || [];
    const layout = getLayoutName(representacion.vsepr);

    if (layout === "linear") {
        return {
            atoms: [
                { symbol: terminales[0], x: 70, y: 78 },
                central,
                { symbol: terminales[1] || terminales[0], x: 190, y: 78 }
            ],
            bonds: [
                { x1: 92, y1: 78, x2: 112, y2: 78 },
                { x1: 148, y1: 78, x2: 168, y2: 78 }
            ],
            lonePairs: []
        };
    }

    if (layout === "bent") {
        return {
            atoms: [
                central,
                { symbol: terminales[0], x: 84, y: 116 },
                { symbol: terminales[1] || terminales[0], x: 176, y: 116 }
            ],
            bonds: [
                { x1: 114, y1: 90, x2: 98, y2: 105 },
                { x1: 146, y1: 90, x2: 162, y2: 105 }
            ],
            lonePairs: [
                { x: 112, y: 48 },
                { x: 148, y: 48 }
            ].slice(0, representacion.paresLibres || 0)
        };
    }

    if (layout === "trigonal") {
        return {
            atoms: [
                central,
                { symbol: terminales[0], x: 130, y: 30 },
                { symbol: terminales[1] || terminales[0], x: 78, y: 112 },
                { symbol: terminales[2] || terminales[0], x: 182, y: 112 }
            ],
            bonds: [
                { x1: 130, y1: 58, x2: 130, y2: 46 },
                { x1: 114, y1: 90, x2: 94, y2: 104 },
                { x1: 146, y1: 90, x2: 166, y2: 104 }
            ],
            lonePairs: []
        };
    }

    if (layout === "pyramidal") {
        return {
            atoms: [
                central,
                { symbol: terminales[0], x: 130, y: 122 },
                { symbol: terminales[1] || terminales[0], x: 78, y: 110 },
                { symbol: terminales[2] || terminales[0], x: 182, y: 110 }
            ],
            bonds: [
                { x1: 130, y1: 94, x2: 130, y2: 108 },
                { x1: 114, y1: 90, x2: 94, y2: 102 },
                { x1: 146, y1: 90, x2: 166, y2: 102 }
            ],
            lonePairs: [
                { x: 130, y: 42 }
            ]
        };
    }

    return {
        atoms: [central],
        bonds: [],
        lonePairs: []
    };
}

function getLayoutName(vsepr) {
    if (vsepr === "AX2") return "linear";
    if (vsepr === "AX2E" || vsepr === "AX2E2") return "bent";
    if (vsepr === "AX3") return "trigonal";
    if (vsepr === "AX3E") return "pyramidal";
    return "formula";
}

export default MoleculeStructure;