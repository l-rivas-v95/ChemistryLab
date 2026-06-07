package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

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
        add(smilesByFormula, "NaNO3", "[O-][N+](=O)[O-].[Na+]", "NNaO3");
        add(smilesByFormula, "KNO3", "[O-][N+](=O)[O-].[K+]", "KNO3");
        add(smilesByFormula, "AgNO3", "[O-][N+](=O)[O-].[Ag+]", "AgNO3");
        add(smilesByFormula, "Ca(NO3)2", "[O-][N+](=O)[O-].[Ca+2].[O-][N+](=O)[O-]");
        add(smilesByFormula, "Cu(NO3)2", "[O-][N+](=O)[O-].[Cu+2].[O-][N+](=O)[O-]");
        add(smilesByFormula, "Fe(NO3)3", "[O-][N+](=O)[O-].[O-][N+](=O)[O-].[Fe+3].[O-][N+](=O)[O-]");

        add(smilesByFormula, "NaNO2", "[O-]N=O.[Na+]", "NNaO2");
        add(smilesByFormula, "KNO2", "[O-]N=O.[K+]", "KNO2");
        add(smilesByFormula, "Ca(NO2)2", "[O-]N=O.[Ca+2].[O-]N=O");
    }

    private static void carbonates(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na2CO3", "[Na+].[O-]C(=O)[O-].[Na+]", "CNa2O3");
        add(smilesByFormula, "K2CO3", "[K+].[O-]C(=O)[O-].[K+]", "CK2O3");
        add(smilesByFormula, "MgCO3", "[O-]C(=O)[O-].[Mg+2]", "CMgO3");
        add(smilesByFormula, "CaCO3", "[O-]C(=O)[O-].[Ca+2]", "CCaO3");
        add(smilesByFormula, "KHCO3", "OC(=O)[O-].[K+]", "CHKO3");
        add(smilesByFormula, "NaHCO3", "OC(=O)[O-].[Na+]", "CHNaO3");
        add(smilesByFormula, "Ca(HCO3)2", "OC(=O)[O-].[Ca+2].OC(=O)[O-]", "CaH2C2O6");
    }

    private static void sulfates(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na2SO4", "[Na+].[O-]S(=O)(=O)[O-].[Na+]", "Na2O4S");
        add(smilesByFormula, "K2SO4", "[K+].[O-]S(=O)(=O)[O-].[K+]", "K2O4S");
        add(smilesByFormula, "CaSO4", "[O-]S(=O)(=O)[O-].[Ca+2]", "CaO4S");
        add(smilesByFormula, "MgSO4", "[O-]S(=O)(=O)[O-].[Mg+2]", "MgO4S");
        add(smilesByFormula, "CuSO4", "[O-]S(=O)(=O)[O-].[Cu+2]", "CuO4S");
        add(smilesByFormula, "ZnSO4", "[O-]S(=O)(=O)[O-].[Zn+2]", "O4SZn");
        add(smilesByFormula, "FeSO4", "[O-]S(=O)(=O)[O-].[Fe+2]", "FeO4S");
        add(smilesByFormula, "Al2(SO4)3", "[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-]");
    }

    private static void phosphates(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na3PO4", "[Na+].[O-]P(=O)([O-])[O-].[Na+].[Na+]", "Na3O4P");
        add(smilesByFormula, "K3PO4", "[K+].[O-]P(=O)([O-])[O-].[K+].[K+]", "K3O4P");
        add(smilesByFormula, "Ca3(PO4)2", "[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2]");
        add(smilesByFormula, "Mg3(PO4)2", "[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2]");
        add(smilesByFormula, "AlPO4", "[O-]P(=O)([O-])[O-].[Al+3]", "AlO4P");
        add(smilesByFormula, "FePO4", "[O-]P(=O)([O-])[O-].[Fe+3]", "FeO4P");
        add(smilesByFormula, "NaH2PO4", "OP(=O)(O)[O-].[Na+]", "H2NaO4P");
        add(smilesByFormula, "Na2HPO4", "OP(=O)([O-])[O-].[Na+].[Na+]", "HNa2O4P");
        add(smilesByFormula, "CaHPO4", "OP(=O)([O-])[O-].[Ca+2]", "CaHO4P");
    }

    private static void chlorineOxoanions(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "NaClO", "Cl[O-].[Na+]", "ClNaO");
        add(smilesByFormula, "NaClO2", "O=Cl[O-].[Na+]", "ClNaO2");
        add(smilesByFormula, "NaClO3", "O=Cl(=O)[O-].[Na+]", "ClNaO3");
        add(smilesByFormula, "KClO3", "O=Cl(=O)[O-].[K+]", "ClKO3");
        add(smilesByFormula, "NaClO4", "O=Cl(=O)(=O)[O-].[Na+]", "ClNaO4");
        add(smilesByFormula, "KClO4", "O=Cl(=O)(=O)[O-].[K+]", "ClKO4");
    }

    private static void transitionMetalOxoanions(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "KMnO4", "O=[Mn](=O)(=O)[O-].[K+]", "KMnO4");
        add(smilesByFormula, "K2Cr2O7", "[K+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[K+]", "Cr2K2O7");
        add(smilesByFormula, "Na2Cr2O7", "[Na+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[Na+]", "Cr2Na2O7");
    }
}
