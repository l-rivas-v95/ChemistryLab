package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class MetalOxideSmilesRule implements FormulaSmilesRule {

    private static final Pattern ALKALI_METAL_OXIDE = Pattern.compile("^(Li|Na|K|Rb|Cs)2O$");
    private static final Pattern SIMPLE_MONOXIDE = Pattern.compile("^(Mg|Ca|Sr|Ba|Fe|Cu|Zn|Mn|Ni|Co|Pb|Sn|Hg)O$");
    private static final Pattern SIMPLE_DIOXIDE = Pattern.compile("^(Mn|Ti|Pb|Sn)O2$");
    private static final Pattern COPPER_SILVER_OXIDE = Pattern.compile("^(Cu|Ag)2O$");

    private final FormulaParserService formulaParserService;

    public MetalOxideSmilesRule(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    @Override
    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.normalizarFormulaVisual(formula);

        return buildAlkaliMetalOxide(normalizedFormula)
                .or(() -> buildSimpleMonoxide(normalizedFormula))
                .or(() -> buildSimpleDioxide(normalizedFormula))
                .or(() -> buildCopperSilverOxide(normalizedFormula));
    }

    private Optional<String> buildAlkaliMetalOxide(String formula) {
        var matcher = ALKALI_METAL_OXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]O[" + metal + "]");
    }

    private Optional<String> buildSimpleMonoxide(String formula) {
        var matcher = SIMPLE_MONOXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]O");
    }

    private Optional<String> buildSimpleDioxide(String formula) {
        var matcher = SIMPLE_DIOXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("O[" + metal + "]O");
    }

    private Optional<String> buildCopperSilverOxide(String formula) {
        var matcher = COPPER_SILVER_OXIDE.matcher(formula);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String metal = matcher.group(1);
        return Optional.of("[" + metal + "]O[" + metal + "]");
    }
}
