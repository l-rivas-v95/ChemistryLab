import BohrModel3D from "./BohrModel3D";
import ElectronConfiguration from "./ElectronConfiguration";

function ElementExtraInfoLeft({ elemento, claseCategoria }) {
    return (
        <div className={`pagina-izquierda-extra ${claseCategoria}`}>
            <h3>Ciencia del elemento</h3>

            <div className="scientific-section">
                <small>Modelo Bohr 3D</small>
                <BohrModel3D modeloBohr3d={elemento.modelo3dBohr} />
            </div>

            <div className="scientific-section">
                <ElectronConfiguration
                    configuracion={elemento.configuracionElectronica}
                    bloque={elemento.bloque}
                />
            </div>
        </div>
    );
}

export default ElementExtraInfoLeft;