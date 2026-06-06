#!/usr/bin/env python3
"""
Este script es un placeholder para generar representaciones de moléculas usando Indigo.
Requiere Indigo Toolkit instalado en Python.
"""
from pathlib import Path
import argparse

MOLECULES = [
    ("ATP", "Nc1ncnc2c1ncn2C3OC(COP(=O)(O)OP(=O)(O)OP(=O)(O)O)C(O)C3O"),
    ("Hydrogen sulfide", "[H]S[H]"),
    ("Potassium ferricyanide", "[K+].[K+].[K+].N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N"),
    ("Sulfuric acid", "OS(=O)(=O)O"),
    ("Calcium carbonate", "[Ca+2].[O-]C(=O)[O-]")
]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--out', default='depiction-test-output/indigo')
    args = parser.parse_args()
    out_dir = Path(args.out)
    out_dir.mkdir(parents=True, exist_ok=True)

    for name, smiles in MOLECULES:
        filename = out_dir / (name.lower().replace(' ', '_') + '.svg')
        # Placeholder: actualmente no hace dibujo, solo crea SVG vacío
        filename.write_text(f"<svg xmlns='http://www.w3.org/2000/svg' width='420' height='300'><text x='210' y='150' text-anchor='middle'>Indigo placeholder for {name}</text></svg>")

if __name__ == '__main__':
    main()