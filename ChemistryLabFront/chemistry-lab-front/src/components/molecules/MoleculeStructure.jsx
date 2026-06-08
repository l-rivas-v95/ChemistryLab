import ChemicalFormulaText from "./ChemicalFormulaText";
import SmilesDrawerStructure from "./SmilesDrawerStructure";
import { getMoleculeCategoryClass } from "../../utils/moleculeCategory";

function MoleculeStructure({ molecula }) {
    if (shouldUseSmilesDrawer(molecula)) {
        return (
            <SmilesDrawerStructure
                smiles={molecula.canonicalSmiles || molecula.isomericSmiles}
                formula={molecula.formula}
            />
        );
    }

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

function shouldUseSmilesDrawer(molecula) {
    if (!molecula?.canonicalSmiles && !molecula?.isomericSmiles) {
        return false;
    }

    return getMoleculeCategoryClass(molecula) === "organic";
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
