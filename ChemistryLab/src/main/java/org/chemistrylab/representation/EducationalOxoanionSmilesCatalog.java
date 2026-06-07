package org.chemistrylab.representation;

import java.util.Map;
import java.util.Optional;

public final class EducationalOxoanionSmilesCatalog {

    private static final Map<String, String> OXOANION_SMILES = Map.ofEntries(
            Map.entry("NO3", "[O-][N+](=O)[O-]"),
            Map.entry("NO2", "O=[N+][O-]"),
            Map.entry("SO4", "[O-]S(=O)(=O)[O-]"),
            Map.entry("SO3", "[O-]S(=O)[O-]"),
            Map.entry("HSO4", "OS(=O)(=O)[O-]"),
            Map.entry("HSO3", "OS(=O)[O-]"),
            Map.entry("CO3", "[O-]C(=O)[O-]"),
            Map.entry("HCO3", "OC(=O)[O-]"),
            Map.entry("PO4", "[O-]P(=O)([O-])[O-]"),
            Map.entry("HPO4", "OP(=O)([O-])[O-]"),
            Map.entry("H2PO4", "OP(=O)(O)[O-]"),
            Map.entry("ClO", "[O-]Cl"),
            Map.entry("ClO2", "O=Cl[O-]"),
            Map.entry("ClO3", "O=Cl(=O)[O-]"),
            Map.entry("ClO4", "O=Cl(=O)(=O)[O-]"),
            Map.entry("BrO3", "O=Br(=O)[O-]"),
            Map.entry("IO3", "O=I(=O)[O-]"),
            Map.entry("MnO4", "[O-][Mn](=O)(=O)=O"),
            Map.entry("CrO4", "[O-][Cr](=O)(=O)[O-]"),
            Map.entry("Cr2O7", "[O-][Cr](=O)(=O)O[Cr](=O)(=O)[O-]"),
            Map.entry("Fe(CN)6", "N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N")
    );

    private static final Map<String, String> OXOACID_SMILES = Map.ofEntries(
            Map.entry("HNO3", "ON(=O)=O"),
            Map.entry("HNO2", "ON=O"),
            Map.entry("H2SO4", "OS(=O)(=O)O"),
            Map.entry("H2SO3", "OS(=O)O"),
            Map.entry("H2CO3", "OC(=O)O"),
            Map.entry("H3PO4", "OP(=O)(O)O"),
            Map.entry("H3BO3", "OB(O)O"),
            Map.entry("B(OH)3", "OB(O)O"),
            Map.entry("HClO", "OCl"),
            Map.entry("HClO2", "OCl=O"),
            Map.entry("HClO3", "OCl(=O)=O"),
            Map.entry("HClO4", "OCl(=O)(=O)=O"),
            Map.entry("HMnO4", "O[Mn](=O)(=O)=O"),
            Map.entry("H2CrO4", "O[Cr](=O)(=O)O"),
            Map.entry("H2Cr2O7", "O[Cr](=O)(=O)O[Cr](=O)(=O)O")
    );

    private EducationalOxoanionSmilesCatalog() {
    }

    public static Optional<String> find(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(OXOANION_SMILES.get(formula));
    }

    public static Optional<String> findNeutralOxoacid(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(OXOACID_SMILES.get(formula));
    }
}
