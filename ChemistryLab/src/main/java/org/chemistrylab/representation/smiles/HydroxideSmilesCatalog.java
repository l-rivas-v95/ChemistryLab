package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class HydroxideSmilesCatalog {

    private HydroxideSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        alkaliMetalHydroxides(smilesByFormula);
        alkalineEarthHydroxides(smilesByFormula);
        transitionMetalHydroxides(smilesByFormula);
        otherMetalHydroxides(smilesByFormula);
    }

    private static void alkaliMetalHydroxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "LiOH", "[H]O[Li]", "HLiO");
        add(smilesByFormula, "NaOH", "[H]O[Na]", "HNaO");
        add(smilesByFormula, "KOH", "[H]O[K]", "HKO");
        add(smilesByFormula, "RbOH", "[H]O[Rb]", "HORb");
        add(smilesByFormula, "CsOH", "[H]O[Cs]", "CsHO");
    }

    private static void alkalineEarthHydroxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Mg(OH)2", "[H]O[Mg]O[H]", "H2MgO2");
        add(smilesByFormula, "Ca(OH)2", "[H]O[Ca]O[H]", "CaH2O2");
        add(smilesByFormula, "Sr(OH)2", "[H]O[Sr]O[H]", "H2O2Sr");
        add(smilesByFormula, "Ba(OH)2", "[H]O[Ba]O[H]", "BaH2O2");
    }

    private static void transitionMetalHydroxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Fe(OH)2", "O[Fe]O", "FeH2O2");
        add(smilesByFormula, "Fe(OH)3", "O[Fe](O)O", "FeH3O3");
        add(smilesByFormula, "Cu(OH)2", "O[Cu]O", "CuH2O2");
        add(smilesByFormula, "Zn(OH)2", "O[Zn]O", "H2O2Zn");
        add(smilesByFormula, "Ni(OH)2", "O[Ni]O", "H2NiO2");
        add(smilesByFormula, "Co(OH)2", "O[Co]O", "CoH2O2");
        add(smilesByFormula, "Cr(OH)3", "O[Cr](O)O", "CrH3O3");
        add(smilesByFormula, "Mn(OH)2", "O[Mn]O", "H2MnO2");
    }

    private static void otherMetalHydroxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Al(OH)3", "O[Al](O)O", "AlH3O3");
        add(smilesByFormula, "Pb(OH)2", "O[Pb]O", "H2O2Pb");
        add(smilesByFormula, "Sn(OH)2", "O[Sn]O", "H2O2Sn");
    }
}
