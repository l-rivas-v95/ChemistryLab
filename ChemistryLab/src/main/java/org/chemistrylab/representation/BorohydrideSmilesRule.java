package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class BorohydrideSmilesRule {

    private static final Pattern METAL_FIRST_BOROHYDRIDE = Pattern.compile("^(Li|Na|K|Rb|Cs)BH4$");
    private static final Pattern BOROHYDRIDE_FIRST = Pattern.compile("^BH4(Li|Na|K|Rb|Cs)$");
    private static final String BOROHYDRIDE_SMILES = "[H][B-]([H])([H])[H]";

    private final FormulaParserService formulaParserService;

    public BorohydrideSmilesRule(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.normalizarFormulaVisual(formula);
        return extractMetal(normalizedFormula)
                .map(metal -> "[" + metal + "+]." + BOROHYDRIDE_SMILES);
    }

    private Optional<String> extractMetal(String formula) {
        var metalFirstMatcher = METAL_FIRST_BOROHYDRIDE.matcher(formula);
        if (metalFirstMatcher.matches()) {
            return Optional.of(metalFirstMatcher.group(1));
        }

        var borohydrideFirstMatcher = BOROHYDRIDE_FIRST.matcher(formula);
        if (borohydrideFirstMatcher.matches()) {
            return Optional.of(borohydrideFirstMatcher.group(1));
        }

        return Optional.empty();
    }
}
