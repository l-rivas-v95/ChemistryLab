package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class SimpleCompoundSmilesCatalog {

    private SimpleCompoundSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        covalentMolecules(smilesByFormula);
        binaryAcids(smilesByFormula);
        oneToOneBinarySalts(smilesByFormula);
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

    private static void oneToOneBinarySalts(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NaCl", "[Na]Cl", "ClNa");
        add(smilesByFormula, "KCl", "[K]Cl", "ClK");
        add(smilesByFormula, "LiCl", "[Li]Cl", "ClLi");
        add(smilesByFormula, "AgCl", "[Ag]Cl", "ClAg");
        add(smilesByFormula, "NaF", "[Na]F", "FNa");
        add(smilesByFormula, "KF", "[K]F", "FK");
        add(smilesByFormula, "NaBr", "[Na]Br", "BrNa");
        add(smilesByFormula, "KBr", "[K]Br", "BrK");
        add(smilesByFormula, "NaI", "[Na]I", "INa");
        add(smilesByFormula, "KI", "[K]I", "IK");
    }
}
