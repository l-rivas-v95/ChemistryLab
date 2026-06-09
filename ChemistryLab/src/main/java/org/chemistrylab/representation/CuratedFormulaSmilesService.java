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

        String cleanedFormula = formulaParserService.limpiarFormula(formula);
        if (cleanedFormula == null || cleanedFormula.isBlank()) {
            return Optional.empty();
        }

        Optional<String> directMatch = findCuratedSmiles(cleanedFormula);
        if (directMatch.isPresent()) {
            return directMatch;
        }

        String visualFormula = formulaParserService.normalizarFormulaVisual(cleanedFormula);
        if (visualFormula.equals(cleanedFormula)) {
            return Optional.empty();
        }

        return findCuratedSmiles(visualFormula);
    }

    private Optional<String> findCuratedSmiles(String formula) {
        return Optional.ofNullable(CURATED_SMILES_BY_FORMULA.get(formula));
    }
}
