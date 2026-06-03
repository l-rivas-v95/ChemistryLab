import MoleculeCard from "./MoleculeCard";
import "./Molecule.css";

function MoleculeList({ moleculas }) {
return (
<section className="moleculas-page">
    <div className="moleculas-header">
        <div>
            <h2>Moléculas inorgánicas</h2>
            <p>Compuestos obtenidos desde PubChem y guardados en tu base de datos.</p>
        </div>

        <span className="moleculas-count">{moleculas.length} compuestos</span>
    </div>

    <div className="moleculas-grid">
        {moleculas.map((molecula) => (
        <MoleculeCard key={molecula.id} molecula={molecula} />
        ))}
    </div>
</section>
);
}

export default MoleculeList;