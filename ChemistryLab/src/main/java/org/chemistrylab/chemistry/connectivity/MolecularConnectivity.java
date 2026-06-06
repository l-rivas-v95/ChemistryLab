package org.chemistrylab.chemistry.connectivity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MolecularConnectivity {

    private final String central;
    private final List<MolecularBond> bonds;
    private final int lonePairs;
}
