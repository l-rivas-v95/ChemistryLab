package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class OxideSmilesCatalog {

    private OxideSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        covalentOxides(smilesByFormula);
        alkaliAndAlkalineEarthOxides(smilesByFormula);
        transitionMetalOxides(smilesByFormula);
        postTransitionMetalOxides(smilesByFormula);
    }

    private static void covalentOxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "SO2", "O=S=O", "O2S");
        add(smilesByFormula, "SO3", "O=S(=O)=O", "O3S");
        add(smilesByFormula, "SiO2", "O=[Si]=O", "O2Si");
        add(smilesByFormula, "B2O3", "O=B-O-B=O", "O3B2");
        add(smilesByFormula, "N2O", "[N-]=[N+]=O", "ON2");
        add(smilesByFormula, "N2O3", "O=NN(=O)=O", "O3N2");
        add(smilesByFormula, "N2O5", "O=N(=O)ON(=O)=O", "O5N2");
        add(smilesByFormula, "P2O5", "O=P(=O)OP(=O)=O", "O5P2");
        add(smilesByFormula, "P4O10", "O=P(=O)OP(=O)(OP(=O)(=O))OP(=O)=O", "O10P4");
        add(smilesByFormula, "Cl2O", "ClOCl", "OCl2");
        add(smilesByFormula, "Cl2O7", "O=Cl(=O)(=O)OCl(=O)(=O)=O", "O7Cl2");
    }

    private static void alkaliAndAlkalineEarthOxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Li2O", "[Li]O[Li]", "Li2O");
        add(smilesByFormula, "Na2O", "[Na]O[Na]", "ONa2");
        add(smilesByFormula, "K2O", "[K]O[K]", "OK2");
        add(smilesByFormula, "MgO", "[Mg]=O", "OMg");
        add(smilesByFormula, "CaO", "[Ca]=O", "OCa");
        add(smilesByFormula, "SrO", "[Sr]=O", "OSr");
        add(smilesByFormula, "BaO", "[Ba]=O", "BaO");
    }

    private static void transitionMetalOxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "FeO", "[Fe]=O", "OFe");
        add(smilesByFormula, "Fe2O3", "[O-2].[O-2].[Fe+3].[Fe+3].[O-2]", "O3Fe2");
        add(smilesByFormula, "Cu2O", "[Cu]O[Cu]", "OCu2");
        add(smilesByFormula, "CuO", "[Cu]=O", "OCu");
        add(smilesByFormula, "ZnO", "[Zn]=O", "OZn");
        add(smilesByFormula, "MnO", "[Mn]=O", "MnO");
        add(smilesByFormula, "MnO2", "O=[Mn]=O", "MnO2");
        add(smilesByFormula, "Cr2O3", "[O-2].[O-2].[Cr+3].[Cr+3].[O-2]", "Cr2O3");
        add(smilesByFormula, "CrO3", "O=[Cr](=O)=O", "CrO3");
        add(smilesByFormula, "TiO2", "O=[Ti]=O", "O2Ti");
        add(smilesByFormula, "NiO", "[Ni]=O", "NiO");
        add(smilesByFormula, "CoO", "[Co]=O", "CoO");
    }

    private static void postTransitionMetalOxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Al2O3", "[O-2].[O-2].[Al+3].[Al+3].[O-2]", "O3Al2");
        add(smilesByFormula, "PbO", "[Pb]=O", "OPb");
        add(smilesByFormula, "PbO2", "O=[Pb]=O", "O2Pb");
        add(smilesByFormula, "SnO", "[Sn]=O", "OSn");
        add(smilesByFormula, "SnO2", "O=[Sn]=O", "O2Sn");
        add(smilesByFormula, "Ag2O", "[Ag]O[Ag]", "Ag2O");
        add(smilesByFormula, "HgO", "[Hg]=O", "HgO");
    }
}
