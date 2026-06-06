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

        if (!esDioxidoDeNitrogeno(context.getFormulaVisual(), atomosFormula)) {
            return Optional.empty();
        }

        return Optional.of(MolecularConnectivity.builder()
                .central("N")
                .bonds(List.of(
                        MolecularBond.builder().from("N").to("O").order(1).build(),
                        MolecularBond.builder().from("N").to("O").order(1).build()
                ))
                .lonePairs(1)
                .build());
    }

    private boolean esDioxidoDeNitrogeno(String formulaVisual, Map<String, Integer> atomosFormula) {
        String formulaNormalizada = normalizarFormula(formulaVisual);

        if ("NO2".equals(formulaNormalizada)) {
            return true;
        }

        return atomosFormula.size() == 2
                && atomosFormula.getOrDefault("N", 0) == 1
                && atomosFormula.getOrDefault("O", 0) == 2;
    }

    private String normalizarFormula(String formula) {
        if (formula == null) {
            return "";
        }

        return formula
                .replace("₀", "0")
                .replace("₁", "1")
                .replace("₂", "2")
                .replace("₃", "3")
                .replace("₄", "4")
                .replace("₅", "5")
                .replace("₆", "6")
                .replace("₇", "7")
                .replace("₈", "8")
                .replace("₉", "9")
                .replace(" ", "")
                .trim()
                .toUpperCase();
    }
}
