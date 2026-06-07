package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RepresentationSmilesOverrideService {

    private static final String AMMONIUM = "[H][N+]([H])([H])[H]";

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
        put(overrides, "NH4OH", AMMONIUM + ".[OH-]", "H5NO");
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
        put(overrides, "NH4F", AMMONIUM + ".[F-]", "FH4N");
        put(overrides, "NH4Cl", AMMONIUM + ".[Cl-]", "ClH4N");
        put(overrides, "NH4Br", AMMONIUM + ".[Br-]", "BrH4N");
        put(overrides, "NH4I", AMMONIUM + ".[I-]", "H4IN");
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
        put(overrides, "Al2O3", "[O-2].[O-2].[Al+3].[Al+3].[O-2]", "O3Al2");
        put(overrides, "Fe2O3", "[O-2].[O-2].[Fe+3].[Fe+3].[O-2]", "O3Fe2");
    }

    private static void oxisalesCompactas(Map<String, String> overrides) {
        put(overrides, "NaNO3", "[O-][N+](=O)[O-].[Na+]", "NNaO3");
        put(overrides, "KNO3", "[O-][N+](=O)[O-].[K+]", "KNO3");
        put(overrides, "AgNO3", "[O-][N+](=O)[O-].[Ag+]", "AgNO3");
        put(overrides, "Ca(NO3)2", "[O-][N+](=O)[O-].[Ca+2].[O-][N+](=O)[O-]");
        put(overrides, "Cu(NO3)2", "[O-][N+](=O)[O-].[Cu+2].[O-][N+](=O)[O-]");
        put(overrides, "Fe(NO3)3", "[O-][N+](=O)[O-].[O-][N+](=O)[O-].[Fe+3].[O-][N+](=O)[O-]");

        put(overrides, "NaNO2", "[O-]N=O.[Na+]", "NNaO2");
        put(overrides, "KNO2", "[O-]N=O.[K+]", "KNO2");
        put(overrides, "Ca(NO2)2", "[O-]N=O.[Ca+2].[O-]N=O");

        put(overrides, "Na2CO3", "[Na+].[O-]C(=O)[O-].[Na+]", "CNa2O3");
        put(overrides, "K2CO3", "[K+].[O-]C(=O)[O-].[K+]", "CK2O3");
        put(overrides, "MgCO3", "[O-]C(=O)[O-].[Mg+2]", "CMgO3");
        put(overrides, "CaCO3", "[O-]C(=O)[O-].[Ca+2]", "CCaO3");
        put(overrides, "KHCO3", "OC(=O)[O-].[K+]", "CHKO3");
        put(overrides, "NaHCO3", "OC(=O)[O-].[Na+]", "CHNaO3");
        put(overrides, "Ca(HCO3)2", "OC(=O)[O-].[Ca+2].OC(=O)[O-]", "CaH2C2O6");

        put(overrides, "Na2SO4", "[Na+].[O-]S(=O)(=O)[O-].[Na+]", "Na2O4S");
        put(overrides, "K2SO4", "[K+].[O-]S(=O)(=O)[O-].[K+]", "K2O4S");
        put(overrides, "CaSO4", "[O-]S(=O)(=O)[O-].[Ca+2]", "CaO4S");
        put(overrides, "MgSO4", "[O-]S(=O)(=O)[O-].[Mg+2]", "MgO4S");
        put(overrides, "CuSO4", "[O-]S(=O)(=O)[O-].[Cu+2]", "CuO4S");
        put(overrides, "ZnSO4", "[O-]S(=O)(=O)[O-].[Zn+2]", "O4SZn");
        put(overrides, "FeSO4", "[O-]S(=O)(=O)[O-].[Fe+2]", "FeO4S");
        put(overrides, "Al2(SO4)3", "[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-]");
        put(overrides, "(NH4)2SO4", AMMONIUM + "." + AMMONIUM + ".[O-]S(=O)(=O)[O-]", "H8N2O4S");

        put(overrides, "Na3PO4", "[Na+].[O-]P(=O)([O-])[O-].[Na+].[Na+]", "Na3O4P");
        put(overrides, "K3PO4", "[K+].[O-]P(=O)([O-])[O-].[K+].[K+]", "K3O4P");
        put(overrides, "Ca3(PO4)2", "[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2]");
        put(overrides, "Mg3(PO4)2", "[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2]");
        put(overrides, "AlPO4", "[O-]P(=O)([O-])[O-].[Al+3]", "AlO4P");
        put(overrides, "FePO4", "[O-]P(=O)([O-])[O-].[Fe+3]", "FeO4P");
        put(overrides, "NaH2PO4", "OP(=O)(O)[O-].[Na+]", "H2NaO4P");
        put(overrides, "Na2HPO4", "OP(=O)([O-])[O-].[Na+].[Na+]", "HNa2O4P");
        put(overrides, "CaHPO4", "OP(=O)([O-])[O-].[Ca+2]", "CaHO4P");

        put(overrides, "NaClO", "Cl[O-].[Na+]", "ClNaO");
        put(overrides, "NaClO2", "O=Cl[O-].[Na+]", "ClNaO2");
        put(overrides, "NaClO3", "O=Cl(=O)[O-].[Na+]", "ClNaO3");
        put(overrides, "KClO3", "O=Cl(=O)[O-].[K+]", "ClKO3");
        put(overrides, "NaClO4", "O=Cl(=O)(=O)[O-].[Na+]", "ClNaO4");
        put(overrides, "KClO4", "O=Cl(=O)(=O)[O-].[K+]", "ClKO4");
        put(overrides, "KMnO4", "O=[Mn](=O)(=O)[O-].[K+]", "KMnO4");
        put(overrides, "K2Cr2O7", "[K+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[K+]", "Cr2K2O7");
        put(overrides, "Na2Cr2O7", "[Na+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[Na+]", "Cr2Na2O7");
    }

    private static void casosIonicLegacyUtiles(Map<String, String> overrides) {
        put(overrides, "(NH4)3PO4", AMMONIUM + "." + AMMONIUM + "." + AMMONIUM + ".[O-]P(=O)([O-])[O-]", "H12N3O4P");
        put(overrides, "NH4NO3", AMMONIUM + ".[O-][N+](=O)[O-]", "H4N2O3", "H4NNO3");
    }

    private static void put(Map<String, String> overrides, String formula, String smiles, String... aliases) {
        overrides.put(formula, smiles);
        for (String alias : aliases) {
            overrides.put(alias, smiles);
        }
    }
}
