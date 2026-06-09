package org.chemistrylab.representation.smiles;

import java.util.Map;

final class CuratedSmilesRegistry {

    private CuratedSmilesRegistry() {
    }

    static void add(Map<String, String> smilesByFormula, String formula, String smiles, String... aliases) {
        smilesByFormula.put(formula, smiles);
        for (String alias : aliases) {
            smilesByFormula.put(alias, smiles);
        }
    }
}
