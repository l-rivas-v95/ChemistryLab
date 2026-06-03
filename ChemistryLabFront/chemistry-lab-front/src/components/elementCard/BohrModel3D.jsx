import { Canvas } from "@react-three/fiber";
import { OrbitControls, Stage, useGLTF } from "@react-three/drei";

function Modelo({ url }) {
    const gltf = useGLTF(url);
    return <primitive object={gltf.scene} />;
}

function BohrModel3D({ modeloBohr3d }) {
    if (!modeloBohr3d) {
        return <div className="bohr-modelo-vacio">Modelo 3D no disponible</div>;
    }

    return (
        <div className="bohr-modelo-3d">
            <Canvas camera={{ position: [0, 0, 4], fov: 45 }}>
                <Stage environment="city" intensity={0.7}>
                    <Modelo url={modeloBohr3d} />
                </Stage>

                <OrbitControls enableZoom={true} />
            </Canvas>
        </div>
    );
}

export default BohrModel3D;