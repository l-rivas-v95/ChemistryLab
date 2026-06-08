package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class MetalNitrideSmilesRule implements FormulaSmilesRule {

    private static final Pattern ALKALI_METAL_NITRIDE = Pattern.compile("^(Li|Na|K|Rb|Cs)3N$");
    private static final Pattern ALKALINE_EARTH_NITRIDE = Pattern.compile("^(Mg|Ca|Sr|Ba)3N2$");
    private static final Pattern GROUP_13_NITRIDE = Pattern.compile("^(B|Al|Ga|In)N$");

    private final FormulaParserService formulaParserService;

    public MetalNitrideSmilesRule(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    @Override
    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.normalizarFormulaVisual(formula);

        return buildAlkaliMetalNitride(normalizedFormula)
                .or(() -> buildAlkalineEarthNitride(normalizedFormula))
                .or(() -> buildGroup13Nitride(normalizedFormula));
    }

    private Optional<String> buildAlkaliMetalNitride(String formula) {
        var matcher = ALKALI_METAL_NITRIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "].N([" + metal + "])[" + metal + "]");
    }

    private Optional<String> buildAlkalineEarthNitride(String formula) {
        var matcher = ALKALINE_EARTH_NITRIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("N[" + metal + "][" + metal + "][" + metal + "]N");
    }

    private Optional<String> buildGroup13Nitride(String formula) {
        var matcher = GROUP_13_NITRIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]N");
    }
}
