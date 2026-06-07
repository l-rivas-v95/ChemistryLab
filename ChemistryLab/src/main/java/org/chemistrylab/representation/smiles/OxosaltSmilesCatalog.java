package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.register;

public final class OxosaltSmilesCatalog {

    private OxosaltSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        nitratesAndNitrites(smilesByFormula);
        carbonates(smilesByFormula);
        sulfates(smilesByFormula);
        phosphates(smilesByFormula);
        chlorineOxoanions(smilesByFormula);
        transitionMetalOxoanions(smilesByFormula);
    }

    private static void nitratesAndNitrites(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "NaNO3", "[O-][N+](=O)[O-].[Na+]", "NNaO3");
        register(smilesByFormula, "KNO3", "[O-][N+](=O)[O-].[K+]", "KNO3");
        register(smilesByFormula, "AgNO3", "[O-][N+](=O)[O-].[Ag+]", "AgNO3");
        register(smilesByFormula, "Ca(NO3)2", "[O-][N+](=O)[O-].[Ca+2].[O-][N+](=O)[O-]");
        register(smilesByFormula, "Cu(NO3)2", "[O-][N+](=O)[O-].[Cu+2].[O-][N+](=O)[O-]");
        register(smilesByFormula, "Fe(NO3)3", "[O-][N+](=O)[O-].[O-][N+](=O)[O-].[Fe+3].[O-][N+](=O)[O-]");

        register(smilesByFormula, "NaNO2", "[O-]N=O.[Na+]", "NNaO2");
        register(smilesByFormula, "KNO2", "[O-]N=O.[K+]", "KNO2");
        register(smilesByFormula, "Ca(NO2)2", "[O-]N=O.[Ca+2].[O-]N=O");
    }

    private static void carbonates(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "Na2CO3", "[Na+].[O-]C(=O)[O-].[Na+]", "CNa2O3");
        register(smilesByFormula, "K2CO3", "[K+].[O-]C(=O)[O-].[K+]", "CK2O3");
        register(smilesByFormula, "MgCO3", "[O-]C(=O)[O-].[Mg+2]", "CMgO3");
        register(smilesByFormula, "CaCO3", "[O-]C(=O)[O-].[Ca+2]", "CCaO3");
        register(smilesByFormula, "KHCO3", "OC(=O)[O-].[K+]", "CHKO3");
        register(smilesByFormula, "NaHCO3", "OC(=O)[O-].[Na+]", "CHNaO3");
        register(smilesByFormula, "Ca(HCO3)2", "OC(=O)[O-].[Ca+2].OC(=O)[O-]", "CaH2C2O6");
    }

    private static void sulfates(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "Na2SO4", "[Na+].[O-]S(=O)(=O)[O-].[Na+]", "Na2O4S");
        register(smilesByFormula, "K2SO4", "[K+].[O-]S(=O)(=O)[O-].[K+]", "K2O4S");
        register(smilesByFormula, "CaSO4", "[O-]S(=O)(=O)[O-].[Ca+2]", "CaO4S");
        register(smilesByFormula, "MgSO4", "[O-]S(=O)(=O)[O-].[Mg+2]", "MgO4S");
        register(smilesByFormula, "CuSO4", "[O-]S(=O)(=O)[O-].[Cu+2]", "CuO4S");
        register(smilesByFormula, "ZnSO4", "[O-]S(=O)(=O)[O-].[Zn+2]", "O4SZn");
        register(smilesByFormula, "FeSO4", "[O-]S(=O)(=O)[O-].[Fe+2]", "FeO4S");
        register(smilesByFormula, "Al2(SO4)3", "[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-]");
    }

    private static void phosphates(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "Na3PO4", "[Na+].[O-]P(=O)([O-])[O-].[Na+].[Na+]", "Na3O4P");
        register(smilesByFormula, "K3PO4", "[K+].[O-]P(=O)([O-])[O-].[K+].[K+]", "K3O4P");
        register(smilesByFormula, "Ca3(PO4)2", "[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2]");
        register(smilesByFormula, "Mg3(PO4)2", "[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2]");
        register(smilesByFormula, "AlPO4", "[O-]P(=O)([O-])[O-].[Al+3]", "AlO4P");
        register(smilesByFormula, "FePO4", "[O-]P(=O)([O-])[O-].[Fe+3]", "FeO4P");
        register(smilesByFormula, "NaH2PO4", "OP(=O)(O)[O-].[Na+]", "H2NaO4P");
        register(smilesByFormula, "Na2HPO4", "OP(=O)([O-])[O-].[Na+].[Na+]", "HNa2O4P");
        register(smilesByFormula, "CaHPO4", "OP(=O)([O-])[O-].[Ca+2]", "CaHO4P");
    }

    private static void chlorineOxoanions(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "NaClO", "Cl[O-].[Na+]", "ClNaO");
        register(smilesByFormula, "NaClO2", "O=Cl[O-].[Na+]", "ClNaO2");
        register(smilesByFormula, "NaClO3", "O=Cl(=O)[O-].[Na+]", "ClNaO3");
        register(smilesByFormula, "KClO3", "O=Cl(=O)[O-].[K+]", "ClKO3");
        register(smilesByFormula, "NaClO4", "O=Cl(=O)(=O)[O-].[Na+]", "ClNaO4");
        register(smilesByFormula, "KClO4", "O=Cl(=O)(=O)[O-].[K+]", "ClKO4");
    }

    private static void transitionMetalOxoanions(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "KMnO4", "O=[Mn](=O)(=O)[O-].[K+]", "KMnO4");
        register(smilesByFormula, "K2Cr2O7", "[K+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[K+]", "Cr2K2O7");
        register(smilesByFormula, "Na2Cr2O7", "[Na+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[Na+]", "Cr2Na2O7");
    }
}
