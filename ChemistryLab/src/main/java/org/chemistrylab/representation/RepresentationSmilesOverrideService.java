package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class RepresentationSmilesOverrideService {

    private final FormulaParserService formulaParserService;

    private static final Map<String, String> OVERRIDES = Map.ofEntries(
            Map.entry("SO3", "O=S(=O)=O"),
            Map.entry("NO", "N=O"),
            Map.entry("SO2", "O=S=O"),
            Map.entry("NO2", "O=[N+][O-]"),
            Map.entry("N2O", "N#[N+]O"),
            Map.entry("CO2", "O=C=O"),
            Map.entry("CO", "[C-]#[O+]"),
            Map.entry("H2O", "O"),
            Map.entry("NH3", "N"),
            Map.entry("H2O2", "OO"),
            Map.entry("CaO", "[Ca+2].[O-2]"),
            Map.entry("MgO", "[Mg+2].[O-2]"),
            Map.entry("FeO", "[Fe+2].[O-2]"),
            Map.entry("CuO", "[Cu+2].[O-2]"),
            Map.entry("ZnO", "[Zn+2].[O-2]"),
            Map.entry("Al2O3", "[Al+3].[Al+3].[O-2].[O-2].[O-2]"),
            Map.entry("Fe2O3", "[Fe+3].[Fe+3].[O-2].[O-2].[O-2]")
    );

    public RepresentationSmilesOverrideService(FormulaParserService formulaParserService) {
        this.formulaParserService = formulaParserService;
    }

    public Optional<String> findOverride(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        String normalizedFormula = formulaParserService.limpiarFormula(formula);
        if (normalizedFormula == null || normalizedFormula.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(OVERRIDES.get(normalizedFormula));
    }
}
