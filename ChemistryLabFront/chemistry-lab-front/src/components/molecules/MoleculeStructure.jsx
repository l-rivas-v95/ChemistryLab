import { useState } from "react";
import { useMoleculeRepresentation } from "../../hooks/useMoleculeRepresentation";
import ChemicalFormulaText from "./ChemicalFormulaText";

function MoleculeStructure({ molecula }) {
    const moleculaId = molecula?.id;
    const { representacion, error } = useMoleculeRepresentation(moleculaId);

    if (error || !representacion) {
        return <FormulaStructure formula={molecula?.formula} />;
    }

    if (representacion.tipoRepresentacion === "IMAGEN_2D" && representacion.imagen2d) {
        return (
            <ExternalImageStructure
                src={representacion.imagen2d}
                alt={molecula?.nombre || representacion.formulaVisual || "Molécula"}
                formula={representacion.formulaVisual || molecula?.formula}
            />
        );
    }

    return <FormulaStructure formula={representacion.formulaVisual || molecula?.formula} />;
}

function ExternalImageStructure({ src, alt, formula }) {
    const [error, setError] = useState(false);

    if (error || !src) {
        return <FormulaStructure formula={formula || alt} />;
    }

    return (
        <div className="formula-structure formula-structure-html external-image-structure-html">
            <img
                src={src}
                alt={alt}
                className="external-molecule-image"
                loading="lazy"
                onError={() => setError(true)}
            />
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

export default MoleculeStructure;
