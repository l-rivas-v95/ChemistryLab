package org.chemistrylab.representation;

public record RepresentationDecision(
        RepresentationFamily family,
        RepresentationStrategy strategy,
        String reason
) {
}
