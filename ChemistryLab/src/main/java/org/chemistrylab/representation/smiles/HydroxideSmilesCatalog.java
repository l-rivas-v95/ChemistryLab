package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class HydroxideSmilesCatalog {

    private HydroxideSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Mg(OH)2", "[H]O[Mg]O[H]", "H2MgO2");
        add(smilesByFormula, "Ca(OH)2", "[H]O[Ca]O[H]", "CaH2O2");
        add(smilesByFormula, "Ba(OH)2", "[H]O[Ba]O[H]", "BaH2O2");
        add(smilesByFormula, "Al(OH)3", "O[Al](O)O", "AlH3O3");
        add(smilesByFormula, "Fe(OH)3", "O[Fe](O)O", "FeH3O3");
    }
}
