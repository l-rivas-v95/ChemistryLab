import "./Navbar.css";

function Navbar({ vistaActiva, setVistaActiva }) {
    return (
        <aside className="sidebar">
            <div className="sidebar-logo">
                <span>⚗️</span>
                <h1>ChemistryLab</h1>
            </div>

            <nav className="sidebar-menu">
                <button
                    className={vistaActiva === "elementos" ? "activo" : ""}
                    onClick={() => setVistaActiva("elementos")}
                >
                    <span>🧪</span>
                    Tabla periódica
                </button>

                <button
                    className={vistaActiva === "moleculas" ? "activo" : ""}
                    onClick={() => setVistaActiva("moleculas")}
                >
                    <span>🧬</span>
                    Moléculas
                </button>
            </nav>
        </aside>
    );
}

export default Navbar;