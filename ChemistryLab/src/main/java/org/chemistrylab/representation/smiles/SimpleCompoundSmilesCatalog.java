package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.register;

public final class SimpleCompoundSmilesCatalog {

    private SimpleCompoundSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        covalentMolecules(smilesByFormula);
        binaryAcids(smilesByFormula);
        oneToOneBinarySalts(smilesByFormula);
    }

    private static void covalentMolecules(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "NH3", "[H]N([H])[H]", "H3N");
        register(smilesByFormula, "CO", "[C-]#[O+]", "OC");
        register(smilesByFormula, "CO2", "O=C=O", "O2C");
        register(smilesByFormula, "NO2", "O=[N+][O-]", "O2N");
        register(smilesByFormula, "HCN", "[H]C#N", "CNH");
    }

    private static void binaryAcids(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "HF", "[H]F", "FH");
        register(smilesByFormula, "HCl", "[H]Cl", "ClH");
        register(smilesByFormula, "HBr", "[H]Br", "BrH");
        register(smilesByFormula, "HI", "[H]I", "IH");
        register(smilesByFormula, "H2S", "[H]S[H]", "SH2");
    }

    private static void oneToOneBinarySalts(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "NaCl", "[Na]Cl", "ClNa");
        register(smilesByFormula, "KCl", "[K]Cl", "ClK");
        register(smilesByFormula, "LiCl", "[Li]Cl", "ClLi");
        register(smilesByFormula, "AgCl", "[Ag]Cl", "ClAg");
        register(smilesByFormula, "NaF", "[Na]F", "FNa");
        register(smilesByFormula, "KF", "[K]F", "FK");
        register(smilesByFormula, "NaBr", "[Na]Br", "BrNa");
        register(smilesByFormula, "KBr", "[K]Br", "BrK");
        register(smilesByFormula, "NaI", "[Na]I", "INa");
        register(smilesByFormula, "KI", "[K]I", "IK");
    }
}
