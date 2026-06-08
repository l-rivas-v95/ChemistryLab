package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class BorohydrideSmilesRule {

    private static final Pattern ALKALI_METAL_BOROHYDRIDE = Pattern.compile("^(Li|Na|K|Rb|Cs)BH4$");

    private final FormulaParserService formulaParserService;

    public BorohydrideSmilesRule(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.normalizarFormulaVisual(formula);
        var matcher = ALKALI_METAL_BOROHYDRIDE.matcher(normalizedFormula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "+].[BH4-]");
    }
}
