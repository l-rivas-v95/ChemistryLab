import ElementTile from "./ElementTitle";
import "./PeriodicTable.css";

function PeriodicTable({ elementos, onElementoClick }) {
    return (
        <section className="tabla-periodica">
            {elementos.map((elemento) => (
                <ElementTile
                    key={elemento.id}
                    elemento={elemento}
                    onClick={onElementoClick}
                />
            ))}
        </section>
    );
}

export default PeriodicTable;