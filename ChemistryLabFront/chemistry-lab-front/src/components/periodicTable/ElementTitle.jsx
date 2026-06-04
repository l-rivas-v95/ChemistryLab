import { getClaseCategoria } from "../../utils/categoriaUtils.js";

function ElementTile({ elemento, onClick }) {
    return (
        <button
            className={`elemento ${getClaseCategoria(elemento.categoria)}`}
            style={{
                gridColumn: elemento.posicionX,
                gridRow: elemento.posicionY,
            }}
            onClick={() => onClick(elemento)}
        >
            <span className="numero">{elemento.numeroAtomico}</span>
            <strong>{elemento.simbolo}</strong>
            <span className="nombre">{elemento.nombre}</span>
        </button>
    );
}

export default ElementTile;