import { useEffect, useRef, useState } from "react";
import SmilesDrawer from "smiles-drawer";

function SmilesDrawerStructure({ smiles, formula }) {
    const canvasRef = useRef(null);
    const [failed, setFailed] = useState(false);

    useEffect(() => {
        if (!smiles || !canvasRef.current) {
            setFailed(true);
            return;
        }

        setFailed(false);

        const canvas = canvasRef.current;
        const parent = canvas.parentElement;
        const width = Math.max(parent?.clientWidth || 220, 160);
        const height = Math.max(parent?.clientHeight || 160, 140);

        canvas.width = width;
        canvas.height = height;

        const drawer = new SmilesDrawer.Drawer({
            width,
            height,
            compactDrawing: false,
            explicitHydrogens: true,
            terminalCarbons: true
        });

        SmilesDrawer.parse(
            smiles,
            (tree) => {
                drawer.draw(tree, canvas, "light", false);
            },
            () => {
                setFailed(true);
            }
        );
    }, [smiles]);

    if (failed) {
        return null;
    }

    return (
        <div className="formula-structure formula-structure-html molecule-smilesdrawer-structure-html" title={formula || smiles}>
            <canvas ref={canvasRef} className="molecule-smilesdrawer-canvas" />
        </div>
    );
}

export default SmilesDrawerStructure;
