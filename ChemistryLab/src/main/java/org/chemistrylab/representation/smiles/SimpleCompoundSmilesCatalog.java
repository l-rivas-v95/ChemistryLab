package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class SimpleCompoundSmilesCatalog {

    private SimpleCompoundSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        covalentMolecules(smilesByFormula);
        binaryAcids(smilesByFormula);
    }

    private static void covalentMolecules(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH3", "[H]N([H])[H]", "H3N");
        add(smilesByFormula, "CO", "[C-]#[O+]", "OC");
        add(smilesByFormula, "CO2", "O=C=O", "O2C");
        add(smilesByFormula, "NO2", "O=[N+][O-]", "O2N");
        add(smilesByFormula, "HCN", "[H]C#N", "CNH");
    }

    private static void binaryAcids(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "HF", "[H]F", "FH");
        add(smilesByFormula, "HCl", "[H]Cl", "ClH");
        add(smilesByFormula, "HBr", "[H]Br", "BrH");
        add(smilesByFormula, "HI", "[H]I", "IH");
        add(smilesByFormula, "H2S", "[H]S[H]", "SH2");
    }
}
