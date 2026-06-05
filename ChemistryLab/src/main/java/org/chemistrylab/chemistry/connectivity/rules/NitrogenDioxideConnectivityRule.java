package org.chemistrylab.chemistry.connectivity.rules;

import org.chemistrylab.chemistry.connectivity.MolecularBond;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Order(25)
public class NitrogenDioxideConnectivityRule implements MolecularConnectivityRule {

    @Override
    public Optional<MolecularConnectivity> tryBuild(MolecularConnectivityContext context) {
        Map<String, Integer> atomosFormula = context.getAtomosFormula();

        if (atomosFormula.size() != 2
                || atomosFormula.getOrDefault("N", 0) != 1
                || atomosFormula.getOrDefault("O", 0) != 2) {
            return Optional.empty();
        }

        return Optional.of(MolecularConnectivity.builder()
                .central("N")
                .bonds(List.of(
                        MolecularBond.builder().from("N").to("O").order(2).build(),
                        MolecularBond.builder().from("N").to("O").order(1).build()
                ))
                .lonePairs(1)
                .build());
    }
}
