package org.chemistrylab.representation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class OxoSpeciesSmilesCatalog {

    private static final Map<String, String> OXOANION_SMILES = buildOxoanionSmiles();
    private static final Map<String, String> OXOACID_SMILES = buildOxoacidSmiles();

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

    private static Map<String, String> buildOxoanionSmiles() {
        Map<String, String> smilesByFormula = new LinkedHashMap<>();

        add(smilesByFormula, "NO3", "[O-][N+](=O)[O-]");
        add(smilesByFormula, "NO2", "O=[N+][O-]");
        add(smilesByFormula, "SO4", "[O-]S(=O)(=O)[O-]");
        add(smilesByFormula, "SO3", "[O-]S(=O)[O-]");
        add(smilesByFormula, "HSO4", "OS(=O)(=O)[O-]");
        add(smilesByFormula, "HSO3", "OS(=O)[O-]");
        add(smilesByFormula, "CO3", "[O-]C(=O)[O-]");
        add(smilesByFormula, "HCO3", "OC(=O)[O-]");
        add(smilesByFormula, "PO4", "[O-]P(=O)([O-])[O-]");
        add(smilesByFormula, "HPO4", "OP(=O)([O-])[O-]");
        add(smilesByFormula, "H2PO4", "OP(=O)(O)[O-]");
        add(smilesByFormula, "ClO", "[O-]Cl");
        add(smilesByFormula, "ClO2", "O=Cl[O-]");
        add(smilesByFormula, "ClO3", "O=Cl(=O)[O-]");
        add(smilesByFormula, "ClO4", "O=Cl(=O)(=O)[O-]");
        add(smilesByFormula, "BrO", "[O-]Br");
        add(smilesByFormula, "BrO2", "O=Br[O-]");
        add(smilesByFormula, "BrO3", "O=Br(=O)[O-]");
        add(smilesByFormula, "BrO4", "O=Br(=O)(=O)[O-]");
        add(smilesByFormula, "IO", "[O-]I");
        add(smilesByFormula, "IO2", "O=I[O-]");
        add(smilesByFormula, "IO3", "O=I(=O)[O-]");
        add(smilesByFormula, "IO4", "O=I(=O)(=O)[O-]");
        add(smilesByFormula, "SeO4", "[O-][Se](=O)(=O)[O-]");
        add(smilesByFormula, "SeO3", "[O-][Se](=O)[O-]");
        add(smilesByFormula, "AsO4", "[O-][As](=O)([O-])[O-]");
        add(smilesByFormula, "AsO3", "[O-][As]([O-])[O-]");
        add(smilesByFormula, "SiO3", "[O-][Si](=O)[O-]");
        add(smilesByFormula, "MnO4", "[O-][Mn](=O)(=O)=O");
        add(smilesByFormula, "MnO4H", "O[Mn](=O)(=O)[O-]");
        add(smilesByFormula, "CrO4", "[O-][Cr](=O)(=O)[O-]");
        add(smilesByFormula, "Cr2O7", "[O-][Cr](=O)(=O)O[Cr](=O)(=O)[O-]");
        add(smilesByFormula, "Fe(CN)6", "N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N");

        return Map.copyOf(smilesByFormula);
    }

    private static Map<String, String> buildOxoacidSmiles() {
        Map<String, String> smilesByFormula = new LinkedHashMap<>();

        add(smilesByFormula, "HNO3", "ON(=O)=O");
        add(smilesByFormula, "HNO2", "ON=O");
        add(smilesByFormula, "H2SO4", "OS(=O)(=O)O");
        add(smilesByFormula, "H2SO3", "OS(=O)O");
        add(smilesByFormula, "H2SeO4", "O[Se](=O)(=O)O");
        add(smilesByFormula, "H2SeO3", "O[Se](=O)O");
        add(smilesByFormula, "H2CO3", "OC(=O)O");
        add(smilesByFormula, "H3PO4", "OP(=O)(O)O");
        add(smilesByFormula, "H3BO3", "OB(O)O");
        add(smilesByFormula, "B(OH)3", "OB(O)O");
        add(smilesByFormula, "H3AsO4", "O[As](=O)(O)O");
        add(smilesByFormula, "H3AsO3", "O[As](O)O");
        add(smilesByFormula, "H2SiO3", "O[Si](=O)O");
        add(smilesByFormula, "H4SiO4", "O[Si](O)(O)O");
        add(smilesByFormula, "HClO", "OCl");
        add(smilesByFormula, "HClO2", "OCl=O");
        add(smilesByFormula, "HClO3", "OCl(=O)=O");
        add(smilesByFormula, "HClO4", "OCl(=O)(=O)=O");
        add(smilesByFormula, "HBrO", "OBr");
        add(smilesByFormula, "HBrO2", "OBr=O");
        add(smilesByFormula, "HBrO3", "OBr(=O)=O");
        add(smilesByFormula, "HBrO4", "OBr(=O)(=O)=O");
        add(smilesByFormula, "HIO", "OI");
        add(smilesByFormula, "HIO2", "OI=O");
        add(smilesByFormula, "HIO3", "OI(=O)=O");
        add(smilesByFormula, "HIO4", "OI(=O)(=O)=O");
        add(smilesByFormula, "HMnO4", "O[Mn](=O)(=O)=O");
        add(smilesByFormula, "H2MnO4", "O[Mn](=O)(=O)O");
        add(smilesByFormula, "H2CrO4", "O[Cr](=O)(=O)O");
        add(smilesByFormula, "H2Cr2O7", "O[Cr](=O)(=O)O[Cr](=O)(=O)O");
        add(smilesByFormula, "H2O4S", "OS(=O)(=O)O");
        add(smilesByFormula, "H2O3S", "OS(=O)O");
        add(smilesByFormula, "H2O4Se", "O[Se](=O)(=O)O");
        add(smilesByFormula, "H2O3Se", "O[Se](=O)O");
        add(smilesByFormula, "CH2O3", "OC(=O)O");
        add(smilesByFormula, "H3O4P", "OP(=O)(O)O");
        add(smilesByFormula, "BH3O3", "OB(O)O");
        add(smilesByFormula, "AsH3O4", "O[As](=O)(O)O");
        add(smilesByFormula, "AsH3O3", "O[As](O)O");
        add(smilesByFormula, "H2O3Si", "O[Si](=O)O");
        add(smilesByFormula, "H4O4Si", "O[Si](O)(O)O");
        add(smilesByFormula, "ClHO", "OCl");
        add(smilesByFormula, "ClHO2", "OCl=O");
        add(smilesByFormula, "ClHO3", "OCl(=O)=O");
        add(smilesByFormula, "ClHO4", "OCl(=O)(=O)=O");
        add(smilesByFormula, "BrHO", "OBr");
        add(smilesByFormula, "BrHO2", "OBr=O");
        add(smilesByFormula, "BrHO3", "OBr(=O)=O");
        add(smilesByFormula, "BrHO4", "OBr(=O)(=O)=O");
        add(smilesByFormula, "CrH2O4", "O[Cr](=O)(=O)O");
        add(smilesByFormula, "Cr2H2O7", "O[Cr](=O)(=O)O[Cr](=O)(=O)O");

        return Map.copyOf(smilesByFormula);
    }

    private static void add(Map<String, String> smilesByFormula, String formula, String smiles) {
        smilesByFormula.put(formula, smiles);
    }
}
