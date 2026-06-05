package org.chemistrylab.chemistry.connectivity.rules;

import org.chemistrylab.chemistry.connectivity.MolecularBond;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Order(30)
public class CovalentX2OConnectivityRule implements MolecularConnectivityRule {

    @Override
    public Optional<MolecularConnectivity> tryBuild(MolecularConnectivityContext context) {
        Map<String, Integer> atomosFormula = context.getAtomosFormula();

        if (atomosFormula.size() != 2 || atomosFormula.getOrDefault("O", 0) != 1) {
            return Optional.empty();
        }

        String elemento = atomosFormula.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .findFirst()
                .orElse(null);

        if (elemento == null || atomosFormula.getOrDefault(elemento, 0) != 2) {
            return Optional.empty();
        }

        return Optional.of(MolecularConnectivity.builder()
                .central(elemento)
                .bonds(List.of(
                        MolecularBond.builder().from(elemento).to(elemento).order(3).build(),
                        MolecularBond.builder().from(elemento).to("O").order(1).build()
                ))
                .lonePairs(0)
                .build());
    }
}
