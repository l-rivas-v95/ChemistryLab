package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class OxosaltSmilesCatalog {

    private OxosaltSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        chlorineOxoanions(smilesByFormula);
        transitionMetalOxoanions(smilesByFormula);
    }

    private static void chlorineOxoanions(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NaClO", "Cl[O-].[Na+]", "ClNaO");
        add(smilesByFormula, "NaClO2", "O=Cl[O-].[Na+]", "ClNaO2");
        add(smilesByFormula, "NaClO3", "O=Cl(=O)[O-].[Na+]", "ClNaO3");
        add(smilesByFormula, "KClO3", "O=Cl(=O)[O-].[K+]", "ClKO3");
        add(smilesByFormula, "NaClO4", "O=Cl(=O)(=O)[O-].[Na+]", "ClNaO4");
        add(smilesByFormula, "KClO4", "O=Cl(=O)(=O)[O-].[K+]", "ClKO4");
    }

    private static void transitionMetalOxoanions(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "KMnO4", "O=[Mn](=O)(=O)[O-].[K+]");
        add(smilesByFormula, "K2Cr2O7", "[K+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[K+]", "Cr2K2O7");
        add(smilesByFormula, "Na2Cr2O7", "[Na+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[Na+]", "Cr2Na2O7");
    }
}
