package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class AmmoniumSmilesCatalog {

    public static final String AMMONIUM = "[H][N+]([H])([H])[H]";

    private AmmoniumSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        halides(smilesByFormula);
        oxosalts(smilesByFormula);
        hydroxide(smilesByFormula);
    }

    private static void halides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4F", AMMONIUM + ".[F-]", "FH4N");
        add(smilesByFormula, "NH4Cl", AMMONIUM + ".[Cl-]", "ClH4N");
        add(smilesByFormula, "NH4Br", AMMONIUM + ".[Br-]", "BrH4N");
        add(smilesByFormula, "NH4I", AMMONIUM + ".[I-]", "H4IN");
    }

    private static void oxosalts(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4NO3", AMMONIUM + ".[O-][N+](=O)[O-]", "H4N2O3", "H4NNO3");
        add(smilesByFormula, "(NH4)2SO4", AMMONIUM + "." + AMMONIUM + ".[O-]S(=O)(=O)[O-]", "H8N2O4S");
        add(smilesByFormula, "(NH4)3PO4", AMMONIUM + "." + AMMONIUM + "." + AMMONIUM + ".[O-]P(=O)([O-])[O-]", "H12N3O4P");
    }

    private static void hydroxide(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4OH", AMMONIUM + ".[OH-]", "H5NO");
    }
}
