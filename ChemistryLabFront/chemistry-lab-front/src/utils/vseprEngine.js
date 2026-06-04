import { useEffect, useRef, useState } from "react";
import SmilesDrawer from "smiles-drawer";

function MoleculeStructure({ molecula }) {
    const tipo = normalizeText(molecula?.tipoCompuesto);
    const smiles = getSmiles(molecula);
    const imagen2d = getImagen2d(molecula);

    if (esOrganica(tipo) && smiles) {
        return (
            <SmilesStructure
                smiles={smiles}
                nombre={molecula?.nombre}
                formula={molecula?.formula}
            />
        );
    }

    if (esInorganica(tipo) && imagen2d && debeUsarImagen2d(tipo)) {
        return (
            <PubChemImage
                src={imagen2d}
                nombre={molecula?.nombre}
                fallback={<FormulaFallback molecula={molecula} />}
            />
        );
    }

    return <FormulaFallback molecula={molecula} />;
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
            explicitHydrogens: true,
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
                if (cancelled) {
                    return;
                }

                drawer.draw(tree, canvas, "light", false);
                setError(false);
            },
            (parseError) => {
                if (cancelled) {
                    return;
                }

                console.error("Error dibujando SMILES:", parseError, smiles);
                setError(true);
            }
        );

        return () => {
            cancelled = true;
        };
    }, [smiles]);

    if (error) {
        return <FormulaFallback molecula={{ formula, nombre }} />;
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

function PubChemImage({ src, nombre, fallback }) {
    const [error, setError] = useState(false);

    if (error) {
        return fallback;
    }

    return (
        <img
            className="pubchem-structure-img"
            src={src}
            alt={nombre || "Estructura molecular"}
            onError={() => setError(true)}
        />
    );
}

function FormulaFallback({ molecula }) {
    const tipo = normalizeText(molecula?.tipoCompuesto);
    const formula = cleanFormula(molecula?.formula);

    const textoFormal = getTextoFormal(tipo);

    return (
        <svg viewBox="0 0 260 180" className="formula-structure">
            <rect x="0" y="0" width="260" height="180" rx="12" className="formula-bg" />

            <text x="130" y="56" textAnchor="middle" className="formula-big">
                {formula || "Sin estructura"}
            </text>

            {textoFormal && (
                <text x="130" y="98" textAnchor="middle" className="formula-ion-view">
                    {textoFormal}
                </text>
            )}

            <text x="130" y="132" textAnchor="middle" className="formula-caption">
                {textoFormal ? "representación formal" : "fórmula química"}
            </text>
        </svg>
    );
}

function debeUsarImagen2d(tipo) {
    return (
        tipo.includes("inorganica") ||
        tipo.includes("inorganico") ||
        tipo.includes("oxido")
    );
}

function getTextoFormal(tipo) {
    if (tipo.includes("sal")) {
        return "catión + anión";
    }

    if (tipo.includes("acido")) {
        return "H⁺ + anión";
    }

    if (tipo.includes("base") || tipo.includes("hidroxido")) {
        return "catión + OH⁻";
    }

    if (tipo.includes("oxido")) {
        return "elemento + O²⁻";
    }

    return null;
}

function esOrganica(tipo) {
    return (
        (tipo.includes("organica") || tipo.includes("organico")) &&
        !tipo.includes("inorganica") &&
        !tipo.includes("inorganico")
    );
}

function esInorganica(tipo) {
    return (
        tipo.includes("inorganica") ||
        tipo.includes("inorganico") ||
        tipo.includes("sal") ||
        tipo.includes("acido") ||
        tipo.includes("base") ||
        tipo.includes("hidroxido") ||
        tipo.includes("oxido")
    );
}

function getSmiles(molecula) {
    return (
        cleanValue(molecula?.canonicalSmiles) ||
        cleanValue(molecula?.isomericSmiles) ||
        cleanValue(molecula?.canonical_smiles) ||
        cleanValue(molecula?.isomeric_smiles) ||
        null
    );
}

function getImagen2d(molecula) {
    return (
        cleanValue(molecula?.imagen2d) ||
        cleanValue(molecula?.imagen2D) ||
        cleanValue(molecula?.imagen_2d) ||
        (molecula?.pubchemCid
            ? `https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/${molecula.pubchemCid}/PNG`
            : null)
    );
}

function cleanFormula(value) {
    return String(value || "")
        .replace(/[+-]\d*$/g, "")
        .replace(/\d*[+-]$/g, "")
        .replace(/\s/g, "");
}

function cleanValue(value) {
    if (value === null || value === undefined) {
        return null;
    }

    const clean = String(value).trim();

    if (!clean || clean.toLowerCase() === "null") {
        return null;
    }

    return clean;
}

function normalizeText(value) {
    return String(value || "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}

export default MoleculeStructure;