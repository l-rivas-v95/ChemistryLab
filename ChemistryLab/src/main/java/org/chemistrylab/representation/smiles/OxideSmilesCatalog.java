package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.CuratedSmilesRegistry.add;

public final class OxideSmilesCatalog {

    private OxideSmilesCatalog() {
    }

    public static void register(Map<String, String> smilesByFormula) {
        covalentOxides(smilesByFormula);
        metallicOxides(smilesByFormula);
    }

    private static void covalentOxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "SO3", "O=S(=O)=O", "O3S");
        add(smilesByFormula, "P2O5", "O=P(=O)OP(=O)=O", "O5P2");
        add(smilesByFormula, "Cl2O", "ClOCl", "OCl2");
        add(smilesByFormula, "Cl2O7", "O=Cl(=O)(=O)OCl(=O)(=O)=O", "O7Cl2");
    }

    private static void metallicOxides(Map<String, String> smilesByFormula) {
        add(smilesByFormula, "Na2O", "[Na]O[Na]", "ONa2");
        add(smilesByFormula, "K2O", "[K]O[K]", "OK2");
        add(smilesByFormula, "CaO", "[Ca]=O", "OCa");
        add(smilesByFormula, "MgO", "[Mg]=O", "OMg");
        add(smilesByFormula, "FeO", "[Fe]=O", "OFe");
        add(smilesByFormula, "CuO", "[Cu]=O", "OCu");
        add(smilesByFormula, "ZnO", "[Zn]=O", "OZn");
        add(smilesByFormula, "TiO2", "O=[Ti]=O", "O2Ti");
        add(smilesByFormula, "Al2O3", "[O-2].[O-2].[Al+3].[Al+3].[O-2]", "O3Al2");
        add(smilesByFormula, "Fe2O3", "[O-2].[O-2].[Fe+3].[Fe+3].[O-2]", "O3Fe2");
    }
}
