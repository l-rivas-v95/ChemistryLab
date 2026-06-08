package org.chemistrylab.representation;

import org.chemistrylab.chemistry.ionic.IonMatch;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class MetalHydroxideSmilesRule {

    private static final Pattern SINGLE_ATOM_FORMULA = Pattern.compile("[A-Z][a-z]?");
    private static final String HYDROXIDE_FORMULA = "OH";

    public Optional<String> build(IonMatch cationMatch, IonMatch anionMatch) {
        if (!HYDROXIDE_FORMULA.equals(anionMatch.ion().getFormula())) {
            return Optional.empty();
        }

        String metal = neutralAtom(cationMatch.ion().getFormula());
        if (metal == null || cationMatch.cantidad() != 1) {
            return Optional.empty();
        }

        return switch (anionMatch.cantidad()) {
            case 1 -> Optional.of("[" + metal + "]O[H]");
            case 2 -> Optional.of("[H]O[" + metal + "]O[H]");
            case 3 -> Optional.of("O[" + metal + "](O)O");
            case 4 -> Optional.of("O[" + metal + "](O)(O)O");
            default -> Optional.empty();
        };
    }

    private String neutralAtom(String formula) {
        if (formula == null || !SINGLE_ATOM_FORMULA.matcher(formula).matches()) {
            return null;
        }
        return formula;
    }
}
