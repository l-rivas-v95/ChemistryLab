package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class AmmoniumSmilesCatalog {

    public static final String AMMONIUM = "[H][N+]([H])([H])[H]";

    private AmmoniumSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        halides(smilesByFormula);
        simpleSalts(smilesByFormula);
        oxosalts(smilesByFormula);
        acidOxosalts(smilesByFormula);
        hydroxide(smilesByFormula);
    }

    private static void halides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4F", AMMONIUM + ".[F-]", "FH4N");
        add(smilesByFormula, "NH4Cl", AMMONIUM + ".[Cl-]", "ClH4N");
        add(smilesByFormula, "NH4Br", AMMONIUM + ".[Br-]", "BrH4N");
        add(smilesByFormula, "NH4I", AMMONIUM + ".[I-]", "H4IN");
    }

    private static void simpleSalts(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4CN", AMMONIUM + ".[C-]#N", "CH4N2");
        add(smilesByFormula, "NH4SCN", AMMONIUM + ".[S-]C#N", "CH4N2S");
    }

    private static void oxosalts(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4NO3", AMMONIUM + ".[O-][N+](=O)[O-]", "H4N2O3", "H4NNO3");
        add(smilesByFormula, "NH4NO2", AMMONIUM + ".[O-]N=O", "H4N2O2");
        add(smilesByFormula, "(NH4)2CO3", AMMONIUM + "." + AMMONIUM + ".[O-]C(=O)[O-]", "CH8N2O3");
        add(smilesByFormula, "(NH4)2SO3", AMMONIUM + "." + AMMONIUM + ".[O-]S(=O)[O-]", "H8N2O3S");
        add(smilesByFormula, "(NH4)2SO4", AMMONIUM + "." + AMMONIUM + ".[O-]S(=O)(=O)[O-]", "H8N2O4S");
        add(smilesByFormula, "(NH4)3PO4", AMMONIUM + "." + AMMONIUM + "." + AMMONIUM + ".[O-]P(=O)([O-])[O-]", "H12N3O4P");
        add(smilesByFormula, "NH4ClO3", AMMONIUM + ".O=Cl(=O)[O-]", "ClH4NO3");
        add(smilesByFormula, "NH4ClO4", AMMONIUM + ".O=Cl(=O)(=O)[O-]", "ClH4NO4");
    }

    private static void acidOxosalts(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4HCO3", AMMONIUM + ".OC(=O)[O-]", "CH5NO3");
        add(smilesByFormula, "NH4HSO4", AMMONIUM + ".OS(=O)(=O)[O-]", "H5NO4S");
        add(smilesByFormula, "NH4H2PO4", AMMONIUM + ".OP(=O)(O)[O-]", "H6NO4P");
        add(smilesByFormula, "(NH4)2HPO4", AMMONIUM + "." + AMMONIUM + ".OP(=O)([O-])[O-]", "H9N2O4P");
    }

    private static void hydroxide(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NH4OH", AMMONIUM + ".[H]O", "H5NO");
    }
}
