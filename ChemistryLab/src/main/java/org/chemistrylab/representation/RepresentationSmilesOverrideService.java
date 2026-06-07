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
        hidroxidosSeleccionados(overrides);
        salesBinariasUnoAUno(overrides);
        salesAmonioHalogenuro(overrides);
        oxidosCovalentes(overrides);
        oxidosMetalicosCompactos(overrides);
        oxisalesCompactas(overrides);
        casosIonicLegacyUtiles(overrides);

        return Map.copyOf(overrides);
    }

    private static void covalentesPequenas(Map<String, String> overrides) {
        put(overrides, "NH3", "[H]N([H])[H]", "H3N");
        put(overrides, "CO", "[C-]#[O+]", "OC");
        put(overrides, "CO2", "O=C=O", "O2C");
        put(overrides, "NO2", "O=[N+][O-]", "O2N");
        put(overrides, "HCN", "[H]C#N", "CNH");
    }

    private static void acidosHidracidos(Map<String, String> overrides) {
        put(overrides, "HF", "[H]F", "FH");
        put(overrides, "HCl", "[H]Cl", "ClH");
        put(overrides, "HBr", "[H]Br", "BrH");
        put(overrides, "HI", "[H]I", "IH");
        put(overrides, "H2S", "[H]S[H]", "SH2");
    }

    private static void hidroxidosSeleccionados(Map<String, String> overrides) {
        put(overrides, "LiOH", "[H]O[Li]", "HLiO");
        put(overrides, "NaOH", "[H]O[Na]", "HNaO");
        put(overrides, "KOH", "[H]O[K]", "HKO");
        put(overrides, "Mg(OH)2", "[H]O[Mg]O[H]", "H2MgO2");
        put(overrides, "Ca(OH)2", "[H]O[Ca]O[H]", "CaH2O2");
        put(overrides, "Ba(OH)2", "[H]O[Ba]O[H]", "BaH2O2");
        put(overrides, "Al(OH)3", "O[Al](O)O", "AlH3O3");
        put(overrides, "Fe(OH)3", "O[Fe](O)O", "FeH3O3");
        put(overrides, "NH4OH", "[NH4+].[OH-]", "H5NO");
    }

    private static void salesBinariasUnoAUno(Map<String, String> overrides) {
        put(overrides, "NaCl", "[Na]Cl", "ClNa");
        put(overrides, "KCl", "[K]Cl", "ClK");
        put(overrides, "LiCl", "[Li]Cl", "ClLi");
        put(overrides, "AgCl", "[Ag]Cl", "ClAg");
        put(overrides, "NaF", "[Na]F", "FNa");
        put(overrides, "KF", "[K]F", "FK");
        put(overrides, "NaBr", "[Na]Br", "BrNa");
        put(overrides, "KBr", "[K]Br", "BrK");
        put(overrides, "NaI", "[Na]I", "INa");
        put(overrides, "KI", "[K]I", "IK");
    }

    private static void salesAmonioHalogenuro(Map<String, String> overrides) {
        put(overrides, "NH4F", "[NH4+].[F-]", "FH4N");
        put(overrides, "NH4Cl", "[NH4+].[Cl-]", "ClH4N");
        put(overrides, "NH4Br", "[NH4+].[Br-]", "BrH4N");
        put(overrides, "NH4I", "[NH4+].[I-]", "H4IN");
    }

    private static void oxidosCovalentes(Map<String, String> overrides) {
        put(overrides, "SO3", "O=S(=O)=O", "O3S");
        put(overrides, "P2O5", "O=P(=O)OP(=O)=O", "O5P2");
        put(overrides, "Cl2O", "ClOCl", "OCl2");
        put(overrides, "Cl2O7", "O=Cl(=O)(=O)OCl(=O)(=O)=O", "O7Cl2");
    }

    private static void oxidosMetalicosCompactos(Map<String, String> overrides) {
        put(overrides, "Na2O", "[Na]O[Na]", "ONa2");
        put(overrides, "K2O", "[K]O[K]", "OK2");
        put(overrides, "CaO", "[Ca]=O", "OCa");
        put(overrides, "MgO", "[Mg]=O", "OMg");
        put(overrides, "FeO", "[Fe]=O", "OFe");
        put(overrides, "CuO", "[Cu]=O", "OCu");
        put(overrides, "ZnO", "[Zn]=O", "OZn");
        put(overrides, "TiO2", "O=[Ti]=O", "O2Ti");
        put(overrides, "Al2O3", "O[Al]O[Al]O", "O3Al2");
        put(overrides, "Fe2O3", "O[Fe]O[Fe]O", "O3Fe2");
    }

    private static void oxisalesCompactas(Map<String, String> overrides) {
        put(overrides, "NaNO3", "[Na]O[N+](=O)[O-]");
        put(overrides, "KNO3", "[K]O[N+](=O)[O-]");
        put(overrides, "AgNO3", "[Ag]O[N+](=O)[O-]");
        put(overrides, "Ca(NO3)2", "O=N(=O)O[Ca]O[N+](=O)[O-]");
        put(overrides, "Cu(NO3)2", "O=N(=O)O[Cu]O[N+](=O)[O-]");
        put(overrides, "Fe(NO3)3", "O=N(=O)O[Fe](O[N+](=O)[O-])O[N+](=O)[O-]");

        put(overrides, "NaNO2", "[Na]ON=O");
        put(overrides, "KNO2", "[K]ON=O");

        put(overrides, "Na2CO3", "[Na]OC(=O)O[Na]");
        put(overrides, "K2CO3", "[K]OC(=O)O[K]");
        put(overrides, "MgCO3", "O=C(O[Mg])O");
        put(overrides, "CaCO3", "O=C(O[Ca])O");
        put(overrides, "KHCO3", "[K]OC(=O)O[H]");
        put(overrides, "NaHCO3", "[Na]OC(=O)O[H]");

        put(overrides, "Na2SO4", "[Na]OS(=O)(=O)O[Na]");
        put(overrides, "K2SO4", "[K]OS(=O)(=O)O[K]");
        put(overrides, "CaSO4", "O=S(=O)(O[Ca])O");
        put(overrides, "MgSO4", "O=S(=O)(O[Mg])O");
        put(overrides, "CuSO4", "O=S(=O)(O[Cu])O");
        put(overrides, "ZnSO4", "O=S(=O)(O[Zn])O");
        put(overrides, "FeSO4", "O=S(=O)(O[Fe])O");
        put(overrides, "Al2(SO4)3", "O=S(=O)(O[Al]OS(=O)(=O)O[Al]OS(=O)(=O)O)O");
        put(overrides, "(NH4)2SO4", "[NH4+].[NH4+].[O-]S(=O)(=O)[O-]");

        put(overrides, "Na3PO4", "[Na]OP(=O)(O[Na])O[Na]");
        put(overrides, "K3PO4", "[K]OP(=O)(O[K])O[K]");
        put(overrides, "Ca3(PO4)2", "O=P(O[Ca]OP(=O)(O[Ca])O[Ca])O");
        put(overrides, "Mg3(PO4)2", "O=P(O[Mg]OP(=O)(O[Mg])O[Mg])O");
        put(overrides, "AlPO4", "O=P(O[Al])(O)O");
        put(overrides, "FePO4", "O=P(O[Fe])(O)O");
        put(overrides, "NaH2PO4", "[Na]OP(=O)(O[H])O[H]");
        put(overrides, "Na2HPO4", "[Na]OP(=O)(O[Na])O[H]");
        put(overrides, "CaHPO4", "O=P(O[Ca])(O[H])O");

        put(overrides, "NaClO", "[Na]OCl");
        put(overrides, "NaClO2", "[Na]OCl=O");
        put(overrides, "NaClO3", "[Na]OCl(=O)=O");
        put(overrides, "KClO3", "[K]OCl(=O)=O");
        put(overrides, "NaClO4", "[Na]OCl(=O)(=O)=O");
        put(overrides, "KClO4", "[K]OCl(=O)(=O)=O");
        put(overrides, "KMnO4", "[K]O[Mn](=O)(=O)=O");
        put(overrides, "K2Cr2O7", "[K]O[Cr](=O)(=O)O[Cr](=O)(=O)O[K]");
        put(overrides, "Na2Cr2O7", "[Na]O[Cr](=O)(=O)O[Cr](=O)(=O)O[Na]");
    }

    private static void casosIonicLegacyUtiles(Map<String, String> overrides) {
        put(overrides, "(NH4)3PO4", "[NH4+].[NH4+].[NH4+].[O-]P(=O)([O-])[O-]");
        put(overrides, "NH4NO3", "[NH4+].[O-][N+](=O)[O-]", "H4NNO3");
    }

    private static void put(Map<String, String> overrides, String formula, String smiles, String... aliases) {
        overrides.put(formula, smiles);
        for (String alias : aliases) {
            overrides.put(alias, smiles);
        }
    }
}
