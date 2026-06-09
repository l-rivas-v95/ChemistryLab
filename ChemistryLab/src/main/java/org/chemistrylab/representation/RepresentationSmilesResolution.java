package org.chemistrylab.representation;

public record RepresentationSmilesResolution(
        String smiles,
        String source,
        String reason
) {
}
