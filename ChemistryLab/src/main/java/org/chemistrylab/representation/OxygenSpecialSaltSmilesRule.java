package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class OxygenSpecialSaltSmilesRule {

    private static final Pattern ALKALI_METAL_PEROXIDE = Pattern.compile("^(Li|Na|K|Rb|Cs)2O2$");
    private static final Pattern ALKALINE_EARTH_PEROXIDE = Pattern.compile("^(Mg|Ca|Sr|Ba)O2$");
    private static final Pattern ALKALI_METAL_SUPEROXIDE = Pattern.compile("^(Li|Na|K|Rb|Cs)O2$");

    private final FormulaParserService formulaParserService;

    public OxygenSpecialSaltSmilesRule(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.normalizarFormulaVisual(formula);

        return buildAlkaliMetalPeroxide(normalizedFormula)
                .or(() -> buildAlkalineEarthPeroxide(normalizedFormula))
                .or(() -> buildAlkaliMetalSuperoxide(normalizedFormula));
    }

    private Optional<String> buildAlkaliMetalPeroxide(String formula) {
        var matcher = ALKALI_METAL_PEROXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]OO[" + metal + "]");
    }

    private Optional<String> buildAlkalineEarthPeroxide(String formula) {
        var matcher = ALKALINE_EARTH_PEROXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]OO");
    }

    private Optional<String> buildAlkaliMetalSuperoxide(String formula) {
        var matcher = ALKALI_METAL_SUPEROXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]O[O]");
    }
}
