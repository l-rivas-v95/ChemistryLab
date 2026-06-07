package org.chemistrylab.representation.smiles;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CuratedSmilesCatalog {

    private CuratedSmilesCatalog() {
    }

    public static Map<String, String> build() {
        Map<String, String> overrides = new LinkedHashMap<>();

        SimpleCompoundSmilesCatalog.register(overrides);
        HydroxideSmilesCatalog.register(overrides);
        AmmoniumSmilesCatalog.register(overrides);
        OxideSmilesCatalog.register(overrides);
        OxosaltSmilesCatalog.register(overrides);

        return Map.copyOf(overrides);
    }
}
