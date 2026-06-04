import { useEffect, useRef } from "react";
import $3Dmol from "3dmol";

function SdfViewer({ cid }) {
    const viewerRef = useRef(null);

    useEffect(() => {
        if (!cid || !viewerRef.current) return;

        const viewer = $3Dmol.createViewer(viewerRef.current, {
            backgroundColor: "white"
        });

        const url3d = `https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/${cid}/SDF?record_type=3d`;
        const url2d = `https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/${cid}/SDF`;

        async function cargarModelo() {
            try {
                let response = await fetch(url3d);

                if (!response.ok) {
                    console.warn("No hay modelo 3D, probando SDF 2D:", cid);
                    response = await fetch(url2d);
                }

                if (!response.ok) {
                    throw new Error("No se pudo cargar ningún SDF");
                }

                const sdf = await response.text();

                viewer.clear();
                viewer.addModel(sdf, "sdf");

                viewer.setStyle({}, {
                    stick: { radius: 0.16 },
                    sphere: { scale: 0.28 }
                });

                viewer.zoomTo();
                viewer.render();
                viewer.spin(true);
            } catch (error) {
                console.error("Error cargando modelo SDF:", error);

                viewerRef.current.innerHTML = `
                    <div class="molecule-3d-empty">
                        Modelo no disponible
                    </div>
                `;
            }
        }

        cargarModelo();

        return () => {
            viewer.clear();
        };
    }, [cid]);

    return <div className="sdf-viewer" ref={viewerRef}></div>;
}

export default SdfViewer;