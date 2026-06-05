package org.chemistrylab.chemistry.connectivity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MolecularBond {

    private final String from;
    private final String to;
    private final int order;
}
