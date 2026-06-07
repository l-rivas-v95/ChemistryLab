package org.chemistrylab.representation.smiles;

import java.util.Map;

final class SmilesOverrideCatalog {

    private SmilesOverrideCatalog() {
    }

    static void put(Map<String, String> overrides, String formula, String smiles, String... aliases) {
        overrides.put(formula, smiles);
        for (String alias : aliases) {
            overrides.put(alias, smiles);
        }
    }
}
