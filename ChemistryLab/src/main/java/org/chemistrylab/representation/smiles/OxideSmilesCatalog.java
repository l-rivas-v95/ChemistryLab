package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.register;

public final class OxideSmilesCatalog {

    private OxideSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        covalentOxides(smilesByFormula);
        metallicOxides(smilesByFormula);
    }

    private static void covalentOxides(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "SO3", "O=S(=O)=O", "O3S");
        register(smilesByFormula, "P2O5", "O=P(=O)OP(=O)=O", "O5P2");
        register(smilesByFormula, "Cl2O", "ClOCl", "OCl2");
        register(smilesByFormula, "Cl2O7", "O=Cl(=O)(=O)OCl(=O)(=O)=O", "O7Cl2");
    }

    private static void metallicOxides(Map<String, String> smilesByFormula) {
        register(smilesByFormula, "Na2O", "[Na]O[Na]", "ONa2");
        register(smilesByFormula, "K2O", "[K]O[K]", "OK2");
        register(smilesByFormula, "CaO", "[Ca]=O", "OCa");
        register(smilesByFormula, "MgO", "[Mg]=O", "OMg");
        register(smilesByFormula, "FeO", "[Fe]=O", "OFe");
        register(smilesByFormula, "CuO", "[Cu]=O", "OCu");
        register(smilesByFormula, "ZnO", "[Zn]=O", "OZn");
        register(smilesByFormula, "TiO2", "O=[Ti]=O", "O2Ti");
        register(smilesByFormula, "Al2O3", "[O-2].[O-2].[Al+3].[Al+3].[O-2]", "O3Al2");
        register(smilesByFormula, "Fe2O3", "[O-2].[O-2].[Fe+3].[Fe+3].[O-2]", "O3Fe2");
    }
}
