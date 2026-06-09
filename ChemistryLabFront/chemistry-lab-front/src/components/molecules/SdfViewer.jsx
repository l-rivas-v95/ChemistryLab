import { useEffect, useRef } from "react";
import $3Dmol from "3dmol";

function SdfViewer({ cid, modelo3dUrl }) {
    const viewerRef = useRef(null);

    useEffect(() => {
        if ((!cid && !modelo3dUrl) || !viewerRef.current) {
            return;
        }

        const container = viewerRef.current;
        container.innerHTML = "";

        const viewer = $3Dmol.createViewer(container, {
            backgroundColor: "white"
        });

        const url3d = modelo3dUrl || `https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/${cid}/SDF?record_type=3d`;
        let renderTimeoutId;

        async function cargarModelo() {
            try {
                const response = await fetch(url3d);

                if (!response.ok) {
                    throw new Error(`PubChem no tiene modelo 3D para CID ${cid || "desconocido"}`);
                }

                const sdf = await response.text();
                if (!sdf || !sdf.trim()) {
                    throw new Error(`Modelo 3D vacío para CID ${cid || "desconocido"}`);
                }

                viewer.clear();
                viewer.addModel(sdf, "sdf");

                viewer.setStyle({}, {
                    stick: { radius: 0.14 },
                    sphere: { scale: 0.25 }
                });

                renderTimeoutId = window.setTimeout(() => {
                    viewer.zoomTo();
                    viewer.resize();
                    viewer.render();
                    viewer.spin(true);
                }, 80);
            } catch (error) {
                console.warn(error.message);

                container.innerHTML = `
                    <div class="molecule-3d-empty">
                        PubChem no tiene modelo 3D para esta molécula
                    </div>
                `;
            }
        }

        cargarModelo();

        return () => {
            if (renderTimeoutId) {
                window.clearTimeout(renderTimeoutId);
            }

            viewer.clear();
            container.innerHTML = "";
        };
    }, [cid, modelo3dUrl]);

    return <div className="sdf-viewer" ref={viewerRef}></div>;
}

export default SdfViewer;
