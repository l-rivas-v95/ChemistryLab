import "./CategoryLegend.css";

function CategoryLegend() {
    return (
        <div className="leyenda">
            <span className="cat-alcalino">Alcalinos</span>
            <span className="cat-alcalinoterreo">Alcalinotérreos</span>
            <span className="cat-transicion">Metales de transición</span>
            <span className="cat-postransicion">Post-transición</span>
            <span className="cat-metaloide">Metaloides</span>
            <span className="cat-no-metal">No metales</span>
            <span className="cat-gas-noble">Gases nobles</span>
            <span className="cat-lantanido">Lantánidos</span>
            <span className="cat-actinido">Actínidos</span>
        </div>
    );
}

export default CategoryLegend;