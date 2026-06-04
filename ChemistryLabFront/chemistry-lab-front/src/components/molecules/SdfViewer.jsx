import { useEffect, useRef } from "react";
import $3Dmol from "3dmol";

function SdfViewer({ cid }) {
    const viewerRef = useRef(null);

    useEffect(() => {
        if (!cid || !viewerRef.current) {
            return;
        }

        const container = viewerRef.current;
        container.innerHTML = "";

        const viewer = $3Dmol.createViewer(container, {
            backgroundColor: "white"
        });

        const url3d = `https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/${cid}/SDF?record_type=3d`;

        async function cargarModelo() {
            try {
                const response = await fetch(url3d);

                if (!response.ok) {
                    throw new Error(`PubChem no tiene modelo 3D para CID ${cid}`);
                }

                const sdf = await response.text();

                viewer.clear();
                viewer.addModel(sdf, "sdf");

                viewer.setStyle({}, {
                    stick: { radius: 0.14 },
                    sphere: { scale: 0.25 }
                });

                viewer.zoomTo();
                viewer.render();
                viewer.resize();
                viewer.spin(true);
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
            viewer.clear();
            container.innerHTML = "";
        };
    }, [cid]);

    return <div className="sdf-viewer" ref={viewerRef}></div>;
}

export default SdfViewer;