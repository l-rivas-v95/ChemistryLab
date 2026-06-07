import ChemicalFormulaText from "./ChemicalFormulaText";

function MoleculeStructure({ molecula }) {
    if (molecula?.tipoRepresentacion === "SVG" && molecula?.svg) {
        return (
            <SvgStructure
                svg={molecula.svg}
                formula={molecula.formula}
            />
        );
    }

    return <FormulaStructure formula={molecula?.formula} />;
}

function SvgStructure({ svg, formula }) {
    if (!svg) {
        return <FormulaStructure formula={formula} />;
    }

    return (
        <div
            className="formula-structure formula-structure-html molecule-svg-structure-html"
            dangerouslySetInnerHTML={{ __html: svg }}
        />
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
                formula quimica
            </div>
        </div>
    );
}

export default MoleculeStructure;
