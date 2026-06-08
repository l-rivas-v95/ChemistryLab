package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MixedOxideSmilesRule implements FormulaSmilesRule {

    private final FormulaParserService formulaParserService;

    public MixedOxideSmilesRule(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    @Override
    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.normalizarFormulaVisual(formula);
        if ("Fe3O4".equals(normalizedFormula)) {
            return Optional.of("[O-2].[Fe+3].[O-2].[Fe+2].[O-2].[Fe+3].[O-2]");
        }

        return Optional.empty();
    }
}
