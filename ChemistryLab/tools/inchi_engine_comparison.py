#!/usr/bin/env python3
import argparse
import html
import json
import re
import shutil
import subprocess
import unicodedata
from pathlib import Path

COMPOUNDS = [
    {
        "name": "Propene",
        "formula": "C3H6",
        "family": "ORGANICA_ALQUENO",
        "inchi": "InChI=1S/C3H6/c1-3-2/h3H,1H2,2H3",
    },
    {
        "name": "Water",
        "formula": "H2O",
        "family": "COVALENTE_SIMPLE",
        "inchi": "InChI=1S/H2O/h1H2",
    },
    {
        "name": "Ammonia",
        "formula": "NH3",
        "family": "COVALENTE_SIMPLE",
        "inchi": "InChI=1S/H3N/h1H3",
    },
    {
        "name": "Carbon dioxide",
        "formula": "CO2",
        "family": "OXIDO_COVALENTE",
        "inchi": "InChI=1S/CO2/c2-1-3",
    },
    {
        "name": "Sulfuric acid",
        "formula": "H2SO4",
        "family": "OXOACIDO",
        "inchi": "InChI=1S/H2O4S/c1-5(2,3)4/h(H2,1,2,3,4)",
    },
    {
        "name": "Phosphoric acid",
        "formula": "H3PO4",
        "family": "OXOACIDO",
        "inchi": "InChI=1S/H3O4P/c1-5(2,3)4/h(H3,1,2,3,4)",
    },
    {
        "name": "Sodium chloride",
        "formula": "NaCl",
        "family": "SAL_BINARIA",
        "inchi": "InChI=1S/ClH.Na/h1H;/q;+1/p-1",
    },
    {
        "name": "Sodium carbonate",
        "formula": "Na2CO3",
        "family": "OXISAL",
        "inchi": "InChI=1S/CH2O3.2Na/c2-1(3)4;;/h(H2,2,3,4);;/q;2*+1/p-2",
    },
    {
        "name": "Calcium carbonate",
        "formula": "CaCO3",
        "family": "OXISAL",
        "inchi": "InChI=1S/CH2O3.Ca/c2-1(3)4;/h(H2,2,3,4);/q;+2/p-2",
    },
    {
        "name": "Potassium ferricyanide",
        "formula": "C6FeK3N6",
        "family": "COMPLEJO",
        "inchi": "InChI=1S/6CN.Fe.3K/c6*1-2;;;;/q6*-1;+3;3*+1",
    },
    {
        "name": "ATP",
        "formula": "C10H16N5O13P3",
        "family": "ORGANOFOSFATO",
        "inchi": "InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(25-10)1-24-30(21,22)28-31(23,27)26-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,27)(H2,11,12,13)(H2,18,19,20)",
    },
]


def slug(value: str) -> str:
    normalized = unicodedata.normalize("NFD", value)
    normalized = "".join(ch for ch in normalized if unicodedata.category(ch) != "Mn")
    return re.sub(r"[^a-zA-Z0-9]+", "_", normalized.lower()).strip("_")


def error_svg(title: str, message: str) -> str:
    return f"""<svg xmlns='http://www.w3.org/2000/svg' width='420' height='300'>
<rect width='100%' height='100%' fill='#fff1f2'/>
<text x='210' y='130' text-anchor='middle' font-family='Arial' font-size='22' font-weight='700' fill='#991b1b'>{html.escape(title)}</text>
<text x='210' y='165' text-anchor='middle' font-family='Arial' font-size='13' fill='#7f1d1d'>{html.escape(str(message)[:90])}</text>
</svg>"""


def write_error(path: Path, title: str, message: str) -> None:
    path.write_text(error_svg(title, message), encoding="utf-8")


def run_command(command, timeout=30, input_text=None):
    completed = subprocess.run(
        command,
        input=input_text,
        text=True,
        capture_output=True,
        timeout=timeout,
        check=False,
    )
    return completed.returncode, completed.stdout, completed.stderr


def smiles_from_inchi_openbabel(inchi: str) -> str:
    if not shutil.which("obabel"):
        raise RuntimeError("obabel no está en PATH")
    code, stdout, stderr = run_command(["obabel", "-iinchi", "-osmi"], input_text=inchi + "\n")
    if code != 0:
        raise RuntimeError(stderr or stdout or "OpenBabel no pudo convertir InChI a SMILES")
    return stdout.strip().split()[0]


def svg_openbabel_from_smiles(smiles: str, output: Path) -> None:
    if not shutil.which("obabel"):
        raise RuntimeError("obabel no está en PATH")
    code, stdout, stderr = run_command(["obabel", "-:" + smiles, "-O", str(output), "--gen2d"], timeout=30)
    if code != 0 or not output.exists() or output.stat().st_size == 0:
        raise RuntimeError(stderr or stdout or "OpenBabel no pudo generar SVG")


def rdkit_available():
    try:
        from rdkit import Chem  # noqa
        from rdkit.Chem import AllChem, Draw  # noqa
        return True
    except Exception:
        return False


def smiles_from_inchi_rdkit(inchi: str) -> str:
    from rdkit import Chem
    mol = Chem.MolFromInchi(inchi)
    if mol is None:
        raise RuntimeError("RDKit MolFromInchi devolvió None")
    return Chem.MolToSmiles(mol, canonical=True, isomericSmiles=True)


def svg_rdkit_from_smiles(smiles: str, output: Path) -> None:
    from rdkit import Chem
    from rdkit.Chem import AllChem, Draw
    mol = Chem.MolFromSmiles(smiles)
    if mol is None:
        raise RuntimeError("RDKit MolFromSmiles devolvió None")
    AllChem.Compute2DCoords(mol)
    drawer = Draw.MolDraw2DSVG(420, 300)
    drawer.drawOptions().padding = 0.08
    drawer.DrawMolecule(mol)
    drawer.FinishDrawing()
    output.write_text(drawer.GetDrawingText(), encoding="utf-8")


def indigo_available():
    try:
        from indigo import Indigo  # noqa
        from indigo.renderer import IndigoRenderer  # noqa
        return True
    except Exception:
        return False


def smiles_from_inchi_indigo(inchi: str) -> str:
    from indigo import Indigo
    indigo = Indigo()
    mol = indigo.loadMolecule(inchi)
    return mol.canonicalSmiles()


def svg_indigo_from_smiles(smiles: str, output: Path) -> None:
    from indigo import Indigo
    from indigo.renderer import IndigoRenderer
    indigo = Indigo()
    renderer = IndigoRenderer(indigo)
    indigo.setOption("render-output-format", "svg")
    indigo.setOption("render-image-size", 420, 300)
    indigo.setOption("render-margins", 10, 10)
    indigo.setOption("render-coloring", True)
    indigo.setOption("render-comment", "")
    mol = indigo.loadMolecule(smiles)
    mol.layout()
    renderer.renderToFile(mol, str(output))


def build_report(compounds, results, output_root: Path) -> str:
    report = []
    report.append("<!doctype html><html><head><meta charset='UTF-8'><title>InChI engine comparison</title>")
    report.append("<style>")
    report.append("body{font-family:Arial,sans-serif;background:#f7f4ec;padding:24px;color:#111827;}")
    report.append("h1{margin:0 0 6px}.intro{max-width:1250px;color:#4b5563;line-height:1.45;margin-bottom:22px}")
    report.append(".compound{background:white;border:1px solid #d7d0c4;border-radius:16px;padding:16px;margin-bottom:22px;box-shadow:0 4px 16px #0001}")
    report.append(".head{display:flex;justify-content:space-between;gap:10px;align-items:start}.formula{font-weight:800;background:#111827;color:#fff8c6;border-radius:999px;padding:6px 12px;white-space:nowrap}.tag{font-size:11px;font-weight:800;border:1px solid #d1d5db;border-radius:999px;padding:4px 8px;background:#f9fafb;display:inline-block;margin:8px 0}")
    report.append(".inchi{font-size:12px;word-break:break-all;background:#f3f4f6;border-radius:8px;padding:8px;margin:8px 0 14px}")
    report.append(".engines{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:14px}.engine{border:1px solid #e5e7eb;border-radius:14px;padding:10px;background:#fcfcfd}.engine-title{font-size:13px;font-weight:900;margin-bottom:8px;border-radius:999px;display:inline-block;padding:4px 8px;background:#eef2ff;border:1px solid #c7d2fe}img{width:100%;height:210px;object-fit:contain;border:1px solid #eee;border-radius:10px;background:#fff}code{font-size:12px;word-break:break-all;display:block;background:#f3f4f6;border-radius:8px;padding:8px;margin-top:10px}.status{font-size:12px;margin-top:8px;color:#374151;background:#fff7ed;border:1px solid #fed7aa;border-radius:10px;padding:8px}")
    report.append("</style></head><body>")
    report.append("<h1>InChI → SMILES → SVG comparison</h1>")
    report.append("<p class='intro'>Cada motor recibe el mismo InChI, genera su propio SMILES y dibuja desde ese SMILES. Así se separa el problema de conversión InChI→SMILES del problema de representación 2D.</p>")

    for compound in compounds:
        compound_slug = slug(compound["name"])
        report.append("<section class='compound'>")
        report.append(f"<div class='head'><h2>{html.escape(compound['name'])}</h2><span class='formula'>{html.escape(compound['formula'])}</span></div>")
        report.append(f"<span class='tag'>{html.escape(compound['family'])}</span>")
        report.append(f"<div class='inchi'>{html.escape(compound['inchi'])}</div>")
        report.append("<div class='engines'>")
        for engine in ["OpenBabel", "RDKit", "Indigo"]:
            result = results.get(compound_slug, {}).get(engine, {})
            svg_path = result.get("svg", "")
            smiles = result.get("smiles", "")
            status = result.get("status", "Sin ejecutar")
            report.append("<div class='engine'>")
            report.append(f"<div class='engine-title'>{engine}</div>")
            if svg_path:
                report.append(f"<img src='{html.escape(svg_path)}'>")
            else:
                report.append(error_svg(engine + " missing", status))
            report.append(f"<code>{html.escape(smiles or 'Sin SMILES generado')}</code>")
            report.append(f"<div class='status'>{html.escape(status)}</div>")
            report.append("</div>")
        report.append("</div></section>")
    report.append("</body></html>")
    return "".join(report)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--out", default="inchi-engine-output")
    args = parser.parse_args()
    output_root = Path(args.out)
    output_root.mkdir(parents=True, exist_ok=True)

    engines = {
        "OpenBabel": output_root / "openbabel",
        "RDKit": output_root / "rdkit",
        "Indigo": output_root / "indigo",
    }
    for directory in engines.values():
        directory.mkdir(parents=True, exist_ok=True)

    results = {}

    for compound in COMPOUNDS:
        compound_slug = slug(compound["name"])
        results[compound_slug] = {}

        for engine, directory in engines.items():
            svg_file = directory / f"{compound_slug}.svg"
            try:
                if engine == "OpenBabel":
                    generated_smiles = smiles_from_inchi_openbabel(compound["inchi"])
                    svg_openbabel_from_smiles(generated_smiles, svg_file)
                elif engine == "RDKit":
                    if not rdkit_available():
                        raise RuntimeError("RDKit no disponible")
                    generated_smiles = smiles_from_inchi_rdkit(compound["inchi"])
                    svg_rdkit_from_smiles(generated_smiles, svg_file)
                elif engine == "Indigo":
                    if not indigo_available():
                        raise RuntimeError("Indigo no disponible")
                    generated_smiles = smiles_from_inchi_indigo(compound["inchi"])
                    svg_indigo_from_smiles(generated_smiles, svg_file)
                else:
                    raise RuntimeError("Motor no soportado")

                results[compound_slug][engine] = {
                    "smiles": generated_smiles,
                    "svg": f"{engine.lower()}/{compound_slug}.svg",
                    "status": "OK",
                }
            except Exception as exc:
                write_error(svg_file, engine + " error", str(exc))
                results[compound_slug][engine] = {
                    "smiles": "",
                    "svg": f"{engine.lower()}/{compound_slug}.svg",
                    "status": "ERROR: " + str(exc),
                }

    (output_root / "results.json").write_text(json.dumps(results, ensure_ascii=False, indent=2), encoding="utf-8")
    (output_root / "comparison.html").write_text(build_report(COMPOUNDS, results, output_root), encoding="utf-8")
    print(f"Generado: {output_root / 'comparison.html'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
