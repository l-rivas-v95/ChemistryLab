package org.chemistrylab.chemistry.connectivity.rules;

import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;

import java.util.Optional;

public interface MolecularConnectivityRule {

    Optional<MolecularConnectivity> tryBuild(MolecularConnectivityContext context);
}
