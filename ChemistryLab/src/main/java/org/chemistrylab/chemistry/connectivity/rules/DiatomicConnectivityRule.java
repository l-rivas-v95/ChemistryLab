package org.chemistrylab.chemistry.connectivity.rules;

import org.chemistrylab.chemistry.connectivity.MolecularBond;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Order(10)
public class DiatomicConnectivityRule implements MolecularConnectivityRule {

    @Override
    public Optional<MolecularConnectivity> tryBuild(MolecularConnectivityContext context) {
        Map<String, Integer> atomosFormula = context.getAtomosFormula();

        int totalAtomos = atomosFormula.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalAtomos != 2) {
            return Optional.empty();
        }

        List<String> atomosExpandidos = expandirAtomos(atomosFormula);

        if (atomosExpandidos.size() != 2) {
            return Optional.empty();
        }

        String from = atomosExpandidos.get(0);
        String to = atomosExpandidos.get(1);

        return Optional.of(MolecularConnectivity.builder()
                .central(from)
                .bonds(List.of(MolecularBond.builder()
                        .from(from)
                        .to(to)
                        .order(estimarOrdenEnlaceDiatomico(from, to))
                        .build()))
                .lonePairs(0)
                .build());
    }

    private int estimarOrdenEnlaceDiatomico(String from, String to) {
        if (("C".equals(from) && "O".equals(to)) || ("O".equals(from) && "C".equals(to))) {
            return 3;
        }

        return 1;
    }

    private List<String> expandirAtomos(Map<String, Integer> atomosFormula) {
        List<String> atomosExpandidos = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : atomosFormula.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                atomosExpandidos.add(entry.getKey());
            }
        }

        return atomosExpandidos;
    }
}
