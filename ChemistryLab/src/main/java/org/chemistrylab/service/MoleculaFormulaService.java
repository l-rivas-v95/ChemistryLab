package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoleculaFormulaService {

    private final FormulaParserService formulaParserService;

    public String obtenerFormulaVisible(String nombre, String formula) {
        return formulaParserService.normalizarFormulaVisual(formula);
    }
}
