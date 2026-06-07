package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.register;

public final class HydroxideSmilesCatalog {

    private HydroxideSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "LiOH", "[H]O[Li]", "HLiO");
        register(smilesByFormula, "NaOH", "[H]O[Na]", "HNaO");
        register(smilesByFormula, "KOH", "[H]O[K]", "HKO");
        register(smilesByFormula, "Mg(OH)2", "[H]O[Mg]O[H]", "H2MgO2");
        register(smilesByFormula, "Ca(OH)2", "[H]O[Ca]O[H]", "CaH2O2");
        register(smilesByFormula, "Ba(OH)2", "[H]O[Ba]O[H]", "BaH2O2");
        register(smilesByFormula, "Al(OH)3", "O[Al](O)O", "AlH3O3");
        register(smilesByFormula, "Fe(OH)3", "O[Fe](O)O", "FeH3O3");
    }
}
