package org.chemistrylab.representation;

import java.util.Map;
import java.util.Optional;

public final class OxoSpeciesSmilesCatalog {

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
            Map.entry("BrO", "[O-]Br"),
            Map.entry("BrO2", "O=Br[O-]"),
            Map.entry("BrO3", "O=Br(=O)[O-]"),
            Map.entry("BrO4", "O=Br(=O)(=O)[O-]"),
            Map.entry("IO", "[O-]I"),
            Map.entry("IO2", "O=I[O-]"),
            Map.entry("IO3", "O=I(=O)[O-]"),
            Map.entry("IO4", "O=I(=O)(=O)[O-]"),
            Map.entry("SeO4", "[O-][Se](=O)(=O)[O-]"),
            Map.entry("SeO3", "[O-][Se](=O)[O-]"),
            Map.entry("AsO4", "[O-][As](=O)([O-])[O-]"),
            Map.entry("AsO3", "[O-][As]([O-])[O-]"),
            Map.entry("SiO3", "[O-][Si](=O)[O-]"),
            Map.entry("MnO4", "[O-][Mn](=O)(=O)=O"),
            Map.entry("MnO4H", "O[Mn](=O)(=O)[O-]"),
            Map.entry("CrO4", "[O-][Cr](=O)(=O)[O-]"),
            Map.entry("Cr2O7", "[O-][Cr](=O)(=O)O[Cr](=O)(=O)[O-]"),
            Map.entry("Fe(CN)6", "N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N")
    );

    private static final Map<String, String> OXOACID_SMILES = Map.ofEntries(
            Map.entry("HNO3", "ON(=O)=O"),
            Map.entry("HNO2", "ON=O"),
            Map.entry("H2SO4", "OS(=O)(=O)O"),
            Map.entry("H2SO3", "OS(=O)O"),
            Map.entry("H2SeO4", "O[Se](=O)(=O)O"),
            Map.entry("H2SeO3", "O[Se](=O)O"),
            Map.entry("H2CO3", "OC(=O)O"),
            Map.entry("H3PO4", "OP(=O)(O)O"),
            Map.entry("H3BO3", "OB(O)O"),
            Map.entry("B(OH)3", "OB(O)O"),
            Map.entry("H3AsO4", "O[As](=O)(O)O"),
            Map.entry("H3AsO3", "O[As](O)O"),
            Map.entry("H2SiO3", "O[Si](=O)O"),
            Map.entry("H4SiO4", "O[Si](O)(O)O"),
            Map.entry("HClO", "OCl"),
            Map.entry("HClO2", "OCl=O"),
            Map.entry("HClO3", "OCl(=O)=O"),
            Map.entry("HClO4", "OCl(=O)(=O)=O"),
            Map.entry("HBrO", "OBr"),
            Map.entry("HBrO2", "OBr=O"),
            Map.entry("HBrO3", "OBr(=O)=O"),
            Map.entry("HBrO4", "OBr(=O)(=O)=O"),
            Map.entry("HIO", "OI"),
            Map.entry("HIO2", "OI=O"),
            Map.entry("HIO3", "OI(=O)=O"),
            Map.entry("HIO4", "OI(=O)(=O)=O"),
            Map.entry("HMnO4", "O[Mn](=O)(=O)=O"),
            Map.entry("H2MnO4", "O[Mn](=O)(=O)O"),
            Map.entry("H2CrO4", "O[Cr](=O)(=O)O"),
            Map.entry("H2Cr2O7", "O[Cr](=O)(=O)O[Cr](=O)(=O)O"),
            Map.entry("H2O4S", "OS(=O)(=O)O"),
            Map.entry("H2O3S", "OS(=O)O"),
            Map.entry("H2O4Se", "O[Se](=O)(=O)O"),
            Map.entry("H2O3Se", "O[Se](=O)O"),
            Map.entry("CH2O3", "OC(=O)O"),
            Map.entry("H3O4P", "OP(=O)(O)O"),
            Map.entry("BH3O3", "OB(O)O"),
            Map.entry("AsH3O4", "O[As](=O)(O)O"),
            Map.entry("AsH3O3", "O[As](O)O"),
            Map.entry("H2O3Si", "O[Si](=O)O"),
            Map.entry("H4O4Si", "O[Si](O)(O)O"),
            Map.entry("ClHO", "OCl"),
            Map.entry("ClHO2", "OCl=O"),
            Map.entry("ClHO3", "OCl(=O)=O"),
            Map.entry("ClHO4", "OCl(=O)(=O)=O"),
            Map.entry("BrHO", "OBr"),
            Map.entry("BrHO2", "OBr=O"),
            Map.entry("BrHO3", "OBr(=O)=O"),
            Map.entry("BrHO4", "OBr(=O)(=O)=O"),
            Map.entry("CrH2O4", "O[Cr](=O)(=O)O"),
            Map.entry("Cr2H2O7", "O[Cr](=O)(=O)O[Cr](=O)(=O)O")
    );

    private OxoSpeciesSmilesCatalog() {
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
