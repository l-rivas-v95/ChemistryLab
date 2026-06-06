package org.chemistrylab.chemistry.connectivity.rules;

import org.chemistrylab.chemistry.connectivity.MolecularBond;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Order(20)
public class HydrogenPeroxideConnectivityRule implements MolecularConnectivityRule {

    @Override
    public Optional<MolecularConnectivity> tryBuild(MolecularConnectivityContext context) {
        Map<String, Integer> atomosFormula = context.getAtomosFormula();

        if (atomosFormula.size() != 2
                || atomosFormula.getOrDefault("O", 0) < 2
                || atomosFormula.getOrDefault("H", 0) < 2) {
            return Optional.empty();
        }

        return Optional.of(MolecularConnectivity.builder()
                .central("O")
                .bonds(List.of(
                        MolecularBond.builder().from("H").to("O").order(1).build(),
                        MolecularBond.builder().from("O").to("O").order(1).build(),
                        MolecularBond.builder().from("O").to("H").order(1).build()
                ))
                .lonePairs(2)
                .build());
    }
}
