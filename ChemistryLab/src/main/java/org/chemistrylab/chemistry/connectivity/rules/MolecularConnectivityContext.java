package org.chemistrylab.chemistry.connectivity.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.chemistrylab.entity.ElementoEntity;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MolecularConnectivityContext {

    private final String formulaVisual;
    private final Map<String, Integer> atomosFormula;
    private final Map<String, ElementoEntity> elementos;
}
