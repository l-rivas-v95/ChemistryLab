package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.representation.smiles.CuratedSmilesCatalog;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CuratedFormulaSmilesService {

    private final FormulaParserService formulaParserService;

    private static final Map<String, String> CURATED_SMILES_BY_FORMULA = CuratedSmilesCatalog.build();

    public CuratedFormulaSmilesService(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    public Optional<String> findByFormula(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.limpiarFormula(formula);
        if (normalizedFormula == null || normalizedFormula.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(CURATED_SMILES_BY_FORMULA.get(normalizedFormula));
    }
}
