package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RepresentationSmilesOverrideService {

    private final FormulaParserService formulaParserService;

    private static final Map<String, String> OVERRIDES = construirOverrides();

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

    private static Map<String, String> construirOverrides() {
        Map<String, String> overrides = new LinkedHashMap<>();

        covalentesPequenas(overrides);
        acidosHidracidos(overrides);
        hidroxidosCompactos(overrides);
        salesBinariasCompactas(overrides);
        oxidosCovalentes(overrides);
        oxidosMetalicosCompactos(overrides);
        oxoanionesYSales(overrides);
        casosIonicLegacyUtiles(overrides);

        return Map.copyOf(overrides);
    }

    private static void covalentesPequenas(Map<String, String> overrides) {
        put(overrides, "H2O", "[H]O[H]", "OH2");
        put(overrides, "NH3", "[H]N([H])[H]", "H3N");
        put(overrides, "H2O2", "[H]OO[H]", "O2H2");
        put(overrides, "CO", "[C-]#[O+]", "OC");
        put(overrides, "CO2", "O=C=O", "O2C");
        put(overrides, "NO", "N=O", "ON");
        put(overrides, "NO2", "O=[N+][O-]", "O2N");
        put(overrides, "N2O", "N#[N+]O", "ON2");
        put(overrides, "HCN", "[H]C#N", "CNH");
    }

    private static void acidosHidracidos(Map<String, String> overrides) {
        put(overrides, "HF", "[H]F", "FH");
        put(overrides, "HCl", "[H]Cl", "ClH");
        put(overrides, "HBr", "[H]Br", "BrH");
        put(overrides, "HI", "[H]I", "IH");
        put(overrides, "H2S", "[H]S[H]", "SH2");
    }

    private static void hidroxidosCompactos(Map<String, String> overrides) {
        put(overrides, "NaOH", "[Na]O[H]");
        put(overrides, "KOH", "[K]O[H]");
        put(overrides, "LiOH", "[Li]O[H]");
        put(overrides, "Ca(OH)2", "[H]O[Ca]O[H]");
        put(overrides, "Mg(OH)2", "[H]O[Mg]O[H]");
        put(overrides, "Ba(OH)2", "[H]O[Ba]O[H]");
        put(overrides, "Al(OH)3", "O[Al](O)O");
        put(overrides, "NH4OH", "[NH4]O[H]");
    }

    private static void salesBinariasCompactas(Map<String, String> overrides) {
        put(overrides, "NaCl", "[Na]Cl", "ClNa");
        put(overrides, "KCl", "[K]Cl", "ClK");
        put(overrides, "LiCl", "[Li]Cl", "ClLi");
        put(overrides, "NaF", "[Na]F", "FNa");
        put(overrides, "KF", "[K]F", "FK");
        put(overrides, "NaBr", "[Na]Br", "BrNa");
        put(overrides, "KBr", "[K]Br", "BrK");
        put(overrides, "NaI", "[Na]I", "INa");
        put(overrides, "KI", "[K]I", "IK");
        put(overrides, "MgCl2", "Cl[Mg]Cl", "Cl2Mg");
        put(overrides, "CaCl2", "Cl[Ca]Cl", "Cl2Ca");
        put(overrides, "AlCl3", "Cl[Al](Cl)Cl", "Cl3Al");
        put(overrides, "NaCN", "[Na]C#N", "CNNa");
        put(overrides, "KCN", "[K]C#N", "CNK");
    }

    private static void oxidosCovalentes(Map<String, String> overrides) {
        put(overrides, "SO2", "O=S=O", "O2S");
        put(overrides, "SO3", "O=S(=O)=O", "O3S");
        put(overrides, "P2O5", "O=P(=O)OP(=O)=O", "O5P2");
        put(overrides, "Cl2O", "ClOCl", "OCl2");
        put(overrides, "Cl2O7", "O=Cl(=O)(=O)OCl(=O)(=O)=O", "O7Cl2");
    }

    private static void oxidosMetalicosCompactos(Map<String, String> overrides) {
        put(overrides, "CaO", "[Ca]=O", "OCa");
        put(overrides, "MgO", "[Mg]=O", "OMg");
        put(overrides, "FeO", "[Fe]=O", "OFe");
        put(overrides, "CuO", "[Cu]=O", "OCu");
        put(overrides, "ZnO", "[Zn]=O", "OZn");
        put(overrides, "TiO2", "O=[Ti]=O", "O2Ti");
        put(overrides, "Al2O3", "O[Al]O[Al]O", "O3Al2");
        put(overrides, "Fe2O3", "O[Fe]O[Fe]O", "O3Fe2");
    }

    private static void oxoanionesYSales(Map<String, String> overrides) {
        put(overrides, "HNO3", "O[N+](=O)[O-]");
        put(overrides, "HNO2", "ON=O");
        put(overrides, "H2SO4", "OS(=O)(=O)O");
        put(overrides, "H2SO3", "OS(=O)O");
        put(overrides, "H2CO3", "OC(=O)O");
        put(overrides, "H3PO4", "OP(=O)(O)O");
        put(overrides, "H3BO3", "OB(O)O");
        put(overrides, "B(OH)3", "OB(O)O");

        put(overrides, "NaNO3", "[Na]O[N+](=O)[O-]");
        put(overrides, "KNO3", "[K]O[N+](=O)[O-]");
        put(overrides, "NaNO2", "[Na]ON=O");
        put(overrides, "KNO2", "[K]ON=O");
        put(overrides, "Na2CO3", "[Na]OC(=O)O[Na]");
        put(overrides, "K2CO3", "[K]OC(=O)O[K]");
        put(overrides, "CaCO3", "O=C(O[Ca])O");
        put(overrides, "NaHCO3", "[Na]OC(=O)O[H]");
        put(overrides, "Na2SO4", "[Na]OS(=O)(=O)O[Na]");
        put(overrides, "K2SO4", "[K]OS(=O)(=O)O[K]");
        put(overrides, "CaSO4", "O=S(=O)(O[Ca])O");
        put(overrides, "Na3PO4", "[Na]OP(=O)(O[Na])O[Na]");
        put(overrides, "K3PO4", "[K]OP(=O)(O[K])O[K]");
        put(overrides, "Ca3(PO4)2", "O=P(O[Ca]OP(=O)(O[Ca])O[Ca])O");
    }

    private static void casosIonicLegacyUtiles(Map<String, String> overrides) {
        put(overrides, "(NH4)3PO4", "[NH4]OP(=O)(O[NH4])O[NH4]");
        put(overrides, "NH4NO3", "[NH4]O[N+](=O)[O-]");
        put(overrides, "NH4Cl", "[NH4]Cl");
    }

    private static void put(Map<String, String> overrides, String formula, String smiles, String... aliases) {
        overrides.put(formula, smiles);
        for (String alias : aliases) {
            overrides.put(alias, smiles);
        }
    }
}
