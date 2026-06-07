package org.chemistrylab.representation.smiles;

import java.util.Map;

import static org.chemistrylab.representation.smiles.SmilesOverrideCatalog.put;

public final class OxideSmilesCatalog {

    private OxideSmilesCatalog() {
    }

    public static void register(Map<String, String> overrides) {
        covalentOxides(overrides);
        metallicOxides(overrides);
    }

    private static void covalentOxides(Map<String, String> overrides) {
        put(overrides, "SO3", "O=S(=O)=O", "O3S");
        put(overrides, "P2O5", "O=P(=O)OP(=O)=O", "O5P2");
        put(overrides, "Cl2O", "ClOCl", "OCl2");
        put(overrides, "Cl2O7", "O=Cl(=O)(=O)OCl(=O)(=O)=O", "O7Cl2");
    }

    private static void metallicOxides(Map<String, String> overrides) {
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
}
