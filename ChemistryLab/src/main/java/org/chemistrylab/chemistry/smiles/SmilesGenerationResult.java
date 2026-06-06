package org.chemistrylab.chemistry.smiles;

public record SmilesGenerationResult(
        String canonicalSmiles,
        String isomericSmiles,
        boolean generated
) {
}
