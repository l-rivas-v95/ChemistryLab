package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.SmilesOverrideCatalog.put;

public final class AmmoniumSmilesCatalog {

    public static final String AMMONIUM = "[H][N+]([H])([H])[H]";

    private AmmoniumSmilesCatalog() {
    }

    public static void register(Map<String, String> overrides) {
        halides(overrides);
        oxosalts(overrides);
        hydroxide(overrides);
    }

    private static void halides(Map<String, String> overrides) {
        put(overrides, "NH4F", AMMONIUM + ".[F-]", "FH4N");
        put(overrides, "NH4Cl", AMMONIUM + ".[Cl-]", "ClH4N");
        put(overrides, "NH4Br", AMMONIUM + ".[Br-]", "BrH4N");
        put(overrides, "NH4I", AMMONIUM + ".[I-]", "H4IN");
    }

    private static void oxosalts(Map<String, String> overrides) {
        put(overrides, "NH4NO3", AMMONIUM + ".[O-][N+](=O)[O-]", "H4N2O3", "H4NNO3");
        put(overrides, "(NH4)2SO4", AMMONIUM + "." + AMMONIUM + ".[O-]S(=O)(=O)[O-]", "H8N2O4S");
        put(overrides, "(NH4)3PO4", AMMONIUM + "." + AMMONIUM + "." + AMMONIUM + ".[O-]P(=O)([O-])[O-]", "H12N3O4P");
    }

    private static void hydroxide(Map<String, String> overrides) {
        put(overrides, "NH4OH", AMMONIUM + ".[OH-]", "H5NO");
    }
}
