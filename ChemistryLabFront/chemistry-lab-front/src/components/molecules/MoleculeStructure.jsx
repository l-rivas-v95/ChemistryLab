import { useEffect, useRef, useState } from "react";
import SmilesDrawer from "smiles-drawer";
import { useMoleculeRepresentation } from "../../hooks/useMoleculeRepresentation";
import ChemicalFormulaText from "./ChemicalFormulaText";

function MoleculeStructure({ molecula }) {
    const moleculaId = molecula?.id;
    const { representacion, error } = useMoleculeRepresentation(moleculaId);

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

    if (representacion.tipoRepresentacion === "ESTRUCTURA_2D") {
        return (
            <Molecular2DStructure
                atomos={representacion.atomos2d}
                enlaces={representacion.enlaces2d}
                texto={representacion.texto}
                polaridad={representacion.polaridad}
            />
        );
    }

    if (representacion.tipoRepresentacion === "VSEPR") {
        return <VseprStructure representacion={representacion} />;
    }

    if (representacion.tipoRepresentacion === "IONICA") {
        return <IonicStructure texto={representacion.texto} />;
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

function Molecular2DStructure({ atomos = [], enlaces = [], texto, polaridad }) {
    const atomosValidos = Array.isArray(atomos) ? atomos : [];
    const enlacesValidos = Array.isArray(enlaces) ? enlaces : [];

    const atomosPorId = new Map(atomosValidos.map((atomo) => [atomo.id, atomo]));

    return (
        <svg viewBox="0 0 260 180" className="formula-structure molecular-2d-structure">
            <rect x="0" y="0" width="260" height="180" rx="12" className="formula-bg" />

            {enlacesValidos.map((enlace, index) => {
                const origen = atomosPorId.get(enlace.origen);
                const destino = atomosPorId.get(enlace.destino);

                if (!origen || !destino) {
                    return null;
                }

                return (
                    <Bond2D
                        key={`${enlace.origen}-${enlace.destino}-${index}`}
                        origen={origen}
                        destino={destino}
                        orden={enlace.orden}
                    />
                );
            })}

            {atomosValidos.map((atomo) => (
                <Atom2D key={atomo.id} atomo={atomo} />
            ))}

            {texto && (
                <text x="130" y="148" textAnchor="middle" className="formula-label">
                    {texto}
                </text>
            )}

            {polaridad && (
                <text x="130" y="158" textAnchor="middle" className="vsepr-caption-sub">
                    {polaridad}
                </text>
            )}
        </svg>
    );
}

function Bond2D({ origen, destino, orden = 1 }) {
    const trimmed = trimBond(origen.x, origen.y, destino.x, destino.y, 20);

    return (
        <BondLine
            x1={trimmed.x1}
            y1={trimmed.y1}
            x2={trimmed.x2}
            y2={trimmed.y2}
            orden={orden}
        />
    );
}

function Atom2D({ atomo }) {
    const carga = formatearCarga(atomo.carga);

    return (
        <g>
            <LonePairs atomo={atomo} />

            <text
                x={atomo.x}
                y={atomo.y}
                textAnchor="middle"
                dominantBaseline="middle"
                className={`vsepr-atom vsepr-atom-${atomo.simbolo}`}
            >
                {atomo.simbolo}
            </text>

            {carga && (
                <text
                    x={atomo.x + 16}
                    y={atomo.y - 16}
                    textAnchor="middle"
                    className="atom-charge"
                >
                    {carga}
                </text>
            )}
        </g>
    );
}

function LonePairs({ atomo }) {
    const pares = Number(atomo.paresLibres || 0);

    if (pares <= 0) {
        return null;
    }

    const posiciones = [
        { x: atomo.x, y: atomo.y - 34 },
        { x: atomo.x, y: atomo.y + 34 },
        { x: atomo.x - 30, y: atomo.y },
        { x: atomo.x + 30, y: atomo.y }
    ];

    return (
        <>
            {posiciones.slice(0, pares).map((posicion, index) => (
                <text
                    key={`${atomo.id}-lp-${index}`}
                    x={posicion.x}
                    y={posicion.y}
                    textAnchor="middle"
                    dominantBaseline="middle"
                    className="vsepr-lone-pair"
                >
                    ··
                </text>
            ))}
        </>
    );
}

function trimBond(x1, y1, x2, y2, padding) {
    const dx = x2 - x1;
    const dy = y2 - y1;
    const longitud = Math.sqrt(dx * dx + dy * dy) || 1;
    const ux = dx / longitud;
    const uy = dy / longitud;

    return {
        x1: x1 + ux * padding,
        y1: y1 + uy * padding,
        x2: x2 - ux * padding,
        y2: y2 - uy * padding
    };
}

function calcularOffsetPerpendicular(x1, y1, x2, y2, distancia) {
    const dx = x2 - x1;
    const dy = y2 - y1;
    const longitud = Math.sqrt(dx * dx + dy * dy) || 1;

    return {
        dx: (-dy / longitud) * distancia,
        dy: (dx / longitud) * distancia
    };
}

function VseprStructure({ representacion }) {
    const layout = getVseprLayout(representacion);

    return (
        <svg viewBox="0 0 260 180" className="formula-structure vsepr-structure">
            <rect x="0" y="0" width="260" height="180" rx="12" className="formula-bg" />

            {layout.bonds.map((bond, index) => (
                <BondLine
                    key={`bond-${index}`}
                    x1={bond.x1}
                    y1={bond.y1}
                    x2={bond.x2}
                    y2={bond.y2}
                    orden={bond.orden}
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

function BondLine({ x1, y1, x2, y2, orden = 1 }) {
    if (orden === 2) {
        const offset = calcularOffsetPerpendicular(x1, y1, x2, y2, 4);

        return (
            <>
                <line
                    x1={x1 + offset.dx}
                    y1={y1 + offset.dy}
                    x2={x2 + offset.dx}
                    y2={y2 + offset.dy}
                    className="vsepr-bond"
                />
                <line
                    x1={x1 - offset.dx}
                    y1={y1 - offset.dy}
                    x2={x2 - offset.dx}
                    y2={y2 - offset.dy}
                    className="vsepr-bond"
                />
            </>
        );
    }

    if (orden === 3) {
        const offset = calcularOffsetPerpendicular(x1, y1, x2, y2, 5);

        return (
            <>
                <line x1={x1} y1={y1} x2={x2} y2={y2} className="vsepr-bond" />
                <line
                    x1={x1 + offset.dx}
                    y1={y1 + offset.dy}
                    x2={x2 + offset.dx}
                    y2={y2 + offset.dy}
                    className="vsepr-bond"
                />
                <line
                    x1={x1 - offset.dx}
                    y1={y1 - offset.dy}
                    x2={x2 - offset.dx}
                    y2={y2 - offset.dy}
                    className="vsepr-bond"
                />
            </>
        );
    }

    return <line x1={x1} y1={y1} x2={x2} y2={y2} className="vsepr-bond" />;
}

function getBondOrder(representacion, destino, index = 0) {
    const enlaces = Array.isArray(representacion.enlaces) ? representacion.enlaces : [];
    const candidatos = enlaces.filter((enlace) => enlace.destino === destino);

    if (candidatos[index]) {
        return candidatos[index].orden || 1;
    }

    return enlaces[index]?.orden || 1;
}

function IonicStructure({ texto }) {
    return (
        <div className="formula-structure formula-structure-html ionic-structure-html">
            <div className="formula-bg-html" />
            <div className="formula-ion-view-html">
                <ChemicalFormulaText value={texto || "representación iónica"} />
            </div>
            <div className="formula-caption-html">
                representación formal
            </div>
        </div>
    );
}

function FormulaStructure({ formula }) {
    return (
        <div className="formula-structure formula-structure-html">
            <div className="formula-bg-html" />
            <div className="formula-big-html">
                <ChemicalFormulaText value={formula || "Sin estructura"} />
            </div>
            <div className="formula-caption-html">
                fórmula química
            </div>
        </div>
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

    if (layout === "diatomic") {
        return {
            atoms: [
                { symbol: representacion.atomoCentral, x: 96, y: 78 },
                { symbol: terminales[0], x: 164, y: 78 }
            ],
            bonds: [
                { x1: 118, y1: 78, x2: 142, y2: 78, orden: getBondOrder(representacion, terminales[0], 0) }
            ],
            lonePairs: []
        };
    }

    if (layout === "linear") {
        return {
            atoms: [
                { symbol: terminales[0], x: 70, y: 78 },
                central,
                { symbol: terminales[1] || terminales[0], x: 190, y: 78 }
            ],
            bonds: [
                { x1: 92, y1: 78, x2: 112, y2: 78, orden: getBondOrder(representacion, terminales[0], 0) },
                { x1: 148, y1: 78, x2: 168, y2: 78, orden: getBondOrder(representacion, terminales[1] || terminales[0], 1) }
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
                { x1: 114, y1: 90, x2: 98, y2: 105, orden: getBondOrder(representacion, terminales[0], 0) },
                { x1: 146, y1: 90, x2: 162, y2: 105, orden: getBondOrder(representacion, terminales[1] || terminales[0], 1) }
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
                { x1: 130, y1: 58, x2: 130, y2: 46, orden: getBondOrder(representacion, terminales[0], 0) },
                { x1: 114, y1: 90, x2: 94, y2: 104, orden: getBondOrder(representacion, terminales[1] || terminales[0], 1) },
                { x1: 146, y1: 90, x2: 166, y2: 104, orden: getBondOrder(representacion, terminales[2] || terminales[0], 2) }
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
                { x1: 130, y1: 94, x2: 130, y2: 108, orden: getBondOrder(representacion, terminales[0], 0) },
                { x1: 114, y1: 90, x2: 94, y2: 102, orden: getBondOrder(representacion, terminales[1] || terminales[0], 1) },
                { x1: 146, y1: 90, x2: 166, y2: 102, orden: getBondOrder(representacion, terminales[2] || terminales[0], 2) }
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
    if (vsepr === "AX1") return "diatomic";
    if (vsepr === "AX2") return "linear";
    if (vsepr === "AX2E" || vsepr === "AX2E2") return "bent";
    if (vsepr === "AX3") return "trigonal";
    if (vsepr === "AX3E") return "pyramidal";
    return "formula";
}

function formatearCarga(carga) {
    if (carga === null || carga === undefined || carga === 0) {
        return null;
    }

    if (carga === 1) return "+";
    if (carga === -1) return "−";

    return carga > 0 ? `${carga}+` : `${Math.abs(carga)}−`;
}

export default MoleculeStructure;
