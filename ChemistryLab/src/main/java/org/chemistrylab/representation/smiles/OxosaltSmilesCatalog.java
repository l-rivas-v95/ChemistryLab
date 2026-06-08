package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class OxosaltSmilesCatalog {

    private OxosaltSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        carbonates(smilesByFormula);
        sulfates(smilesByFormula);
        phosphates(smilesByFormula);
        chlorineOxoanions(smilesByFormula);
        transitionMetalOxoanions(smilesByFormula);
    }

    private static void carbonates(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na2CO3", "[Na+].[O-]C(=O)[O-].[Na+]", "CNa2O3");
        add(smilesByFormula, "K2CO3", "[K+].[O-]C(=O)[O-].[K+]", "CK2O3");
        add(smilesByFormula, "MgCO3", "[O-]C(=O)[O-].[Mg+2]", "CMgO3");
        add(smilesByFormula, "CaCO3", "[O-]C(=O)[O-].[Ca+2]", "CCaO3");
        add(smilesByFormula, "KHCO3", "OC(=O)[O-].[K+]");
        add(smilesByFormula, "NaHCO3", "OC(=O)[O-].[Na+]");
        add(smilesByFormula, "Ca(HCO3)2", "OC(=O)[O-].[Ca+2].OC(=O)[O-]");
    }

    private static void sulfates(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na2SO4", "[Na+].[O-]S(=O)(=O)[O-].[Na+]");
        add(smilesByFormula, "K2SO4", "[K+].[O-]S(=O)(=O)[O-].[K+]");
        add(smilesByFormula, "CaSO4", "[O-]S(=O)(=O)[O-].[Ca+2]");
        add(smilesByFormula, "MgSO4", "[O-]S(=O)(=O)[O-].[Mg+2]");
        add(smilesByFormula, "CuSO4", "[O-]S(=O)(=O)[O-].[Cu+2]");
        add(smilesByFormula, "ZnSO4", "[O-]S(=O)(=O)[O-].[Zn+2]");
        add(smilesByFormula, "FeSO4", "[O-]S(=O)(=O)[O-].[Fe+2]");
        add(smilesByFormula, "Al2(SO4)3", "[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-].[Al+3].[O-]S(=O)(=O)[O-]");
    }

    private static void phosphates(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na3PO4", "[Na+].[O-]P(=O)([O-])[O-].[Na+].[Na+]");
        add(smilesByFormula, "K3PO4", "[K+].[O-]P(=O)([O-])[O-].[K+].[K+]");
        add(smilesByFormula, "Ca3(PO4)2", "[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2].[O-]P(=O)([O-])[O-].[Ca+2]");
        add(smilesByFormula, "Mg3(PO4)2", "[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2].[O-]P(=O)([O-])[O-].[Mg+2]");
        add(smilesByFormula, "AlPO4", "[O-]P(=O)([O-])[O-].[Al+3]");
        add(smilesByFormula, "FePO4", "[O-]P(=O)([O-])[O-].[Fe+3]");
        add(smilesByFormula, "NaH2PO4", "OP(=O)(O)[O-].[Na+]");
        add(smilesByFormula, "Na2HPO4", "OP(=O)([O-])[O-].[Na+].[Na+]");
        add(smilesByFormula, "CaHPO4", "OP(=O)([O-])[O-].[Ca+2]");
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
        add(smilesByFormula, "KMnO4", "O=[Mn](=O)(=O)[O-].[K+]");
        add(smilesByFormula, "K2Cr2O7", "[K+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[K+]", "Cr2K2O7");
        add(smilesByFormula, "Na2Cr2O7", "[Na+].O=[Cr](=O)([O-])O[Cr](=O)(=O)[O-].[Na+]", "Cr2Na2O7");
    }
}
