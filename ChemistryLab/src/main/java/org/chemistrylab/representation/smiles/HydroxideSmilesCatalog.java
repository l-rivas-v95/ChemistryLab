package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.SmilesOverrideCatalog.put;

public final class HydroxideSmilesCatalog {

    private HydroxideSmilesCatalog() {
    }

    public static void register(Map<String, String> overrides) {
        put(overrides, "LiOH", "[H]O[Li]", "HLiO");
        put(overrides, "NaOH", "[H]O[Na]", "HNaO");
        put(overrides, "KOH", "[H]O[K]", "HKO");
        put(overrides, "Mg(OH)2", "[H]O[Mg]O[H]", "H2MgO2");
        put(overrides, "Ca(OH)2", "[H]O[Ca]O[H]", "CaH2O2");
        put(overrides, "Ba(OH)2", "[H]O[Ba]O[H]", "BaH2O2");
        put(overrides, "Al(OH)3", "O[Al](O)O", "AlH3O3");
        put(overrides, "Fe(OH)3", "O[Fe](O)O", "FeH3O3");
    }
}
