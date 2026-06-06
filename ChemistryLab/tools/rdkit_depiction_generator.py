#!/usr/bin/env python3
import argparse
from pathlib import Path

MOLECULES = [
    ("Water", "O"),
    ("Water explicit", "[H]O[H]"),
    ("Ammonia", "N"),
    ("Ammonia explicit", "[H]N([H])[H]"),
    ("Hydrogen peroxide", "OO"),
    ("Hydrogen peroxide explicit", "[H]OO[H]"),
    ("Carbon dioxide", "O=C=O"),
    ("Carbon monoxide", "[C-]#[O+]"),
    ("Sulfur dioxide", "O=S=O"),
    ("Sulfur trioxide", "O=S(=O)=O"),
    ("Nitric oxide", "N=O"),
    ("Nitrogen dioxide", "O=[N+][O-]"),
    ("Dinitrogen monoxide", "N#[N+][O-]"),
    ("Ozone", "O=[O+][O-]"),
    ("Hydrogen cyanide", "C#N"),
    ("Hydrogen cyanide explicit", "[H]C#N"),
    ("Hydrochloric acid", "Cl"),
    ("Hydrochloric acid explicit", "[H]Cl"),
    ("Nitric acid", "O[N+](=O)[O-]"),
    ("Sulfuric acid", "OS(=O)(=O)O"),
    ("Phosphoric acid", "OP(=O)(O)O"),
    ("Carbonic acid", "OC(=O)O"),
    ("Sodium chloride", "[Na+].[Cl-]"),
    ("Potassium chloride", "[K+].[Cl-]"),
    ("Calcium chloride", "[Ca+2].[Cl-].[Cl-]"),
    ("Sodium hydroxide", "[Na+].[OH-]"),
    ("Calcium hydroxide", "[Ca+2].[OH-].[OH-]"),
    ("Sodium carbonate", "[Na+].[Na+].[O-]C(=O)[O-]"),
    ("Calcium carbonate", "[Ca+2].[O-]C(=O)[O-]"),
    ("Sodium bicarbonate", "[Na+].OC(=O)[O-]"),
    ("Potassium carbonate", "[K+].[K+].[O-]C(=O)[O-]"),
    ("Sodium sulfate", "[Na+].[Na+].[O-]S(=O)(=O)[O-]"),
    ("Potassium sulfate", "[K+].[K+].[O-]S(=O)(=O)[O-]"),
    ("Calcium sulfate", "[Ca+2].[O-]S(=O)(=O)[O-]"),
    ("Aluminum sulfate", "[Al+3].[Al+3].[O-]S(=O)(=O)[O-].[O-]S(=O)(=O)[O-].[O-]S(=O)(=O)[O-]"),
    ("Sodium nitrate", "[Na+].[O-][N+](=O)[O-]"),
    ("Calcium nitrate", "[Ca+2].[O-][N+](=O)[O-].[O-][N+](=O)[O-]"),
    ("Iron(III) nitrate", "[Fe+3].[O-][N+](=O)[O-].[O-][N+](=O)[O-].[O-][N+](=O)[O-]"),
    ("Sodium phosphate", "[Na+].[Na+].[Na+].[O-]P(=O)([O-])[O-]"),
    ("Calcium phosphate", "[Ca+2].[Ca+2].[Ca+2].[O-]P(=O)([O-])[O-].[O-]P(=O)([O-])[O-]"),
    ("Ammonium sulfate", "[NH4+].[NH4+].[O-]S(=O)(=O)[O-]"),
    ("Potassium ferricyanide", "[K+].[K+].[K+].N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N"),
    ("Sodium dichromate", "[Na+].[Na+].[O-][Cr](=O)(=O)O[Cr](=O)(=O)[O-]"),
    ("Potassium permanganate", "[K+].[O-][Mn](=O)(=O)=O"),
    ("Tetrahydrocannabinol", "CCCCCC1=CC(=C2C3C=C(CC(C3CC(CC2=C1O)(C)C)O)C)O"),
    ("ATP", "Nc1ncnc2c1ncn2C3OC(COP(=O)(O)OP(=O)(O)OP(=O)(O)O)C(O)C3O"),
]


def slug(value: str) -> str:
    import re
    import unicodedata
    normalized = unicodedata.normalize("NFD", value)
    normalized = "".join(ch for ch in normalized if unicodedata.category(ch) != "Mn")
    normalized = re.sub(r"[^a-zA-Z0-9]+", "_", normalized.lower()).strip("_")
    return normalized


def error_svg(title: str, message: str) -> str:
    import html
    return f"""<svg xmlns='http://www.w3.org/2000/svg' width='420' height='300'>
<rect width='100%' height='100%' fill='#fff1f2'/>
<text x='210' y='130' text-anchor='middle' font-family='Arial' font-size='22' font-weight='700' fill='#991b1b'>{html.escape(title)}</text>
<text x='210' y='165' text-anchor='middle' font-family='Arial' font-size='13' fill='#7f1d1d'>{html.escape(message[:80])}</text>
</svg>"""


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--out", default="depiction-test-output/rdkit")
    args = parser.parse_args()
    out_dir = Path(args.out)
    out_dir.mkdir(parents=True, exist_ok=True)

    try:
        from rdkit import Chem
        from rdkit.Chem import AllChem, Draw
    except Exception as exc:
        for name, _ in MOLECULES:
            (out_dir / f"{slug(name)}.svg").write_text(error_svg("RDKit missing", str(exc)), encoding="utf-8")
        return 2

    for name, smiles in MOLECULES:
        output = out_dir / f"{slug(name)}.svg"
        try:
            mol = Chem.MolFromSmiles(smiles)
            if mol is None:
                raise ValueError("MolFromSmiles returned None")
            AllChem.Compute2DCoords(mol)
            drawer = Draw.MolDraw2DSVG(420, 300)
            drawer.drawOptions().padding = 0.08
            drawer.DrawMolecule(mol)
            drawer.FinishDrawing()
            output.write_text(drawer.GetDrawingText(), encoding="utf-8")
        except Exception as exc:
            output.write_text(error_svg("RDKit error", str(exc)), encoding="utf-8")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
