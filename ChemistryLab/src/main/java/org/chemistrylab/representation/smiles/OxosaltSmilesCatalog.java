package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.SmilesOverrideCatalog.put;

public final class OxosaltSmilesCatalog {

    private OxosaltSmilesCatalog() {
    }

    public static void register(Map<String, String> overrides) {
        nitratesAndNitrites(overrides);
        carbonates(overrides);
        sulfates(overrides);
        phosphates(overrides);
        chlorineOxoanions(overrides);
        transitionMetalOxoanions(overrides);
    }

    private static void nitratesAndNitrites(Map<String, String> overrides) {
        put(overrides, "NaNO3", "[O-][N+](=O)[O-].[Na+]", "NNaO3");
        put(overrides, "KNO3", "[O-][N+](=O)[O-].[K+]", "KNO3");
        put(overrides, "AgNO3", "[O-][N+](=O)[O-].[Ag+]", "AgNO3");
        put(overrides, "Ca(NO3)2", "[O-][N+](=O)[O-].[Ca+2].[O-][N+](=O)[O-]");
        put(overrides, "Cu(NO3)2", "[O-][N+](=O)[O-].[Cu+2].[O-][N+](=O)[O-]");
        put(overrides, "Fe(NO3)3", "[O-][N+](=O)[O-].[O-][N+](=O)[O-].[Fe+3].[O-][N+](=O)[O-]");

        put(overrides, "NaNO2", "[O-]N=O.[Na+]", "NNaO2");
        put(overrides, "KNO2", "[O-]N=O.[K+]", "KNO2");
        put(overrides, "Ca(NO2)2", "[O-]N=O.[Ca+2].[O-]N=O");
    }

    private static void carbonates(Map<String, String> overrides) {
        put(overrides, "Na2CO3", "[Na+].[O-]C(=O)[O-].[Na+]", "CNa2O3");
        put(overrides, "K2CO3", "[K+].[O-]C(=O)[O-].[K+]", "CK2O3");
        put(overrides, "MgCO3", "[O-]C(=O)[O-].[Mg+2]", "CMgO3");
        put(overrides, "CaCO3", "[O-]C(=O)[O-].[Ca+2]", "CCaO3");
        put(overrides, "KHCO3", "OC(=O)[O-].[K+]", "CHKO3");
        put(overrides, "NaHCO3", "OC(=O)[O-].[Na+]", "CHNaO3");
        put(overrides, "Ca(HCO3)2", "OC(=O)[O-].[Ca+2].OC(=O)[O-]", "CaH2C2O6");
    }

    private static void sulfates(Map<String, String> overrides) {
        put(overrides, "Na2SO4", "[Na+].[O-]S(=O)(=O)[O-].[Na+]", "Na2O4S");
        put(overrides, "K2SO4", "[K+].[O-]S(=O)(=O)[O-].[K+]", "K2O4S");
        put(overrides, "CaSO4", "[O-]S(=O)(=O)[O-].[Ca+2]", "CaO4S");
        put(overrides, "MgSO4", "[O-]S(=O)(=O)[O-].[Mg+2]", "MgO4S");
        put(overrides, "CuSO4", "[O-]S(=O)(=O)[O-].[Cu+2]", "CuO4S");
        put(overrides, "ZnSO4", "[O-]S(=O)(=O)[O-].[Zn+2]", "O4SZn");
        put(overrides, "FeSO4", "[O-]S(=O)(=O)[O-].[Fe+2]", "FeO4S");
        put(overrides, "Al2(SO4)3", "[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-]");
    }

    private static void phosphates(Map<String, String> overrides) {
        put(overrides, "Na3PO4", "[Na+].[O-]P(=O)([O-])[O-].[Na+].[Na+]", "Na3O4P");
        put(overrides, "K3PO4", "[K+].[O-]P(=O)([O-])[O-].[K+].[K+]", "K3O4P");
        put(overrides, "Ca3(PO4)2", "[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2]");
        put(overrides, "Mg3(PO4)2", "[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2]");
        put(overrides, "AlPO4", "[O-]P(=O)([O-])[O-].[Al+3]", "AlO4P");
        put(overrides, "FePO4", "[O-]P(=O)([O-])[O-].[Fe+3]", "FeO4P");
        put(overrides, "NaH2PO4", "OP(=O)(O)[O-].[Na+]", "H2NaO4P");
        put(overrides, "Na2HPO4", "OP(=O)([O-])[O-].[Na+].[Na+]", "HNa2O4P");
        put(overrides, "CaHPO4", "OP(=O)([O-])[O-].[Ca+2]", "CaHO4P");
    }

    private static void chlorineOxoanions(Map<String, String> overrides) {
        put(overrides, "NaClO", "Cl[O-].[Na+]", "ClNaO");
        put(overrides, "NaClO2", "O=Cl[O-].[Na+]", "ClNaO2");
        put(overrides, "NaClO3", "O=Cl(=O)[O-].[Na+]", "ClNaO3");
        put(overrides, "KClO3", "O=Cl(=O)[O-].[K+]", "ClKO3");
        put(overrides, "NaClO4", "O=Cl(=O)(=O)[O-].[Na+]", "ClNaO4");
        put(overrides, "KClO4", "O=Cl(=O)(=O)[O-].[K+]", "ClKO4");
    }

    private static void transitionMetalOxoanions(Map<String, String> overrides) {
        put(overrides, "KMnO4", "O=[Mn](=O)(=O)[O-].[K+]", "KMnO4");
        put(overrides, "K2Cr2O7", "[K+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[K+]", "Cr2K2O7");
        put(overrides, "Na2Cr2O7", "[Na+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[Na+]", "Cr2Na2O7");
    }
}
