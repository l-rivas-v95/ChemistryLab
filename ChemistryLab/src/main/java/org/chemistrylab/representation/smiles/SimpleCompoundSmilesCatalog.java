package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.SmilesOverrideCatalog.put;

public final class SimpleCompoundSmilesCatalog {

    private SimpleCompoundSmilesCatalog() {
    }

    public static void register(Map<String, String> overrides) {
        covalentMolecules(overrides);
        binaryAcids(overrides);
        oneToOneBinarySalts(overrides);
    }

    private static void covalentMolecules(Map<String, String> overrides) {
        put(overrides, "NH3", "[H]N([H])[H]", "H3N");
        put(overrides, "CO", "[C-]#[O+]", "OC");
        put(overrides, "CO2", "O=C=O", "O2C");
        put(overrides, "NO2", "O=[N+][O-]", "O2N");
        put(overrides, "HCN", "[H]C#N", "CNH");
    }

    private static void binaryAcids(Map<String, String> overrides) {
        put(overrides, "HF", "[H]F", "FH");
        put(overrides, "HCl", "[H]Cl", "ClH");
        put(overrides, "HBr", "[H]Br", "BrH");
        put(overrides, "HI", "[H]I", "IH");
        put(overrides, "H2S", "[H]S[H]", "SH2");
    }

    private static void oneToOneBinarySalts(Map<String, String> overrides) {
        put(overrides, "NaCl", "[Na]Cl", "ClNa");
        put(overrides, "KCl", "[K]Cl", "ClK");
        put(overrides, "LiCl", "[Li]Cl", "ClLi");
        put(overrides, "AgCl", "[Ag]Cl", "ClAg");
        put(overrides, "NaF", "[Na]F", "FNa");
        put(overrides, "KF", "[K]F", "FK");
        put(overrides, "NaBr", "[Na]Br", "BrNa");
        put(overrides, "KBr", "[K]Br", "BrK");
        put(overrides, "NaI", "[Na]I", "INa");
        put(overrides, "KI", "[K]I", "IK");
    }
}
