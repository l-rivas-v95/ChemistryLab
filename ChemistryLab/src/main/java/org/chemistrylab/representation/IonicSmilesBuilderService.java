package org.chemistrylab.representation;

import org.chemistrylab.chemistry.config.IonConfig;
import org.chemistrylab.chemistry.ionic.IonMatch;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolution;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IonicSmilesBuilderService {

    private final IonicFormulaResolver ionicFormulaResolver;

    private static final Map<String, String> ION_SMILES = Map.ofEntries(
            Map.entry("H", "[H+]"),
            Map.entry("Li", "[Li+]"),
            Map.entry("Na", "[Na+]"),
            Map.entry("K", "[K+]"),
            Map.entry("Rb", "[Rb+]"),
            Map.entry("Cs", "[Cs+]"),
            Map.entry("Be", "[Be+2]"),
            Map.entry("Mg", "[Mg+2]"),
            Map.entry("Ca", "[Ca+2]"),
            Map.entry("Sr", "[Sr+2]"),
            Map.entry("Ba", "[Ba+2]"),
            Map.entry("Al", "[Al+3]"),
            Map.entry("Ag", "[Ag+]"),
            Map.entry("Zn", "[Zn+2]"),
            Map.entry("Cu", "[Cu+2]"),
            Map.entry("Fe", "[Fe+3]"),
            Map.entry("NH4", "[NH4+]"),
            Map.entry("H3O", "[OH3+]"),
            Map.entry("F", "[F-]"),
            Map.entry("Cl", "[Cl-]"),
            Map.entry("Br", "[Br-]"),
            Map.entry("I", "[I-]"),
            Map.entry("O", "[O-2]"),
            Map.entry("S", "[S-2]"),
            Map.entry("N", "[N-3]"),
            Map.entry("P", "[P-3]"),
            Map.entry("OH", "[OH-]"),
            Map.entry("CN", "[C-]#N"),
            Map.entry("OCN", "[O-]C#N"),
            Map.entry("SCN", "[S-]C#N"),
            Map.entry("O2", "[O-]O[O-]"),
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

    public IonicSmilesBuilderService(IonicFormulaResolver ionicFormulaResolver) {
        this.ionicFormulaResolver = ionicFormulaResolver;
    }

    public Optional<String> build(String formula) {
        return ionicFormulaResolver.resolver(formula)
                .flatMap(this::buildFromResolution);
    }

    private Optional<String> buildFromResolution(IonicFormulaResolution resolution) {
        List<String> fragments = new ArrayList<>();

        if (!addFragments(fragments, resolution.getCation(), true)) {
            return Optional.empty();
        }

        if (!addFragments(fragments, resolution.getAnion(), false)) {
            return Optional.empty();
        }

        if (fragments.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(String.join(".", fragments));
    }

    private boolean addFragments(List<String> fragments, IonMatch match, boolean cation) {
        if (match == null || match.getIon() == null || match.getCantidad() <= 0) {
            return false;
        }

        IonConfig ion = match.getIon();
        String smiles = ION_SMILES.get(ion.getFormula());
        if (smiles == null || smiles.isBlank()) {
            smiles = monoatomicIonSmiles(ion);
        }

        if (smiles == null || smiles.isBlank()) {
            return false;
        }

        for (int i = 0; i < match.getCantidad(); i++) {
            fragments.add(smiles);
        }

        return true;
    }

    private String monoatomicIonSmiles(IonConfig ion) {
        if (ion == null || ion.getFormula() == null || ion.getCarga() == null) {
            return null;
        }

        String formula = ion.getFormula();
        int carga = ion.getCarga();

        if (carga == 0) {
            return null;
        }

        if (carga == 1) {
            return "[" + formula + "+]";
        }
        if (carga == -1) {
            return "[" + formula + "-]";
        }
        if (carga > 1) {
            return "[" + formula + "+" + carga + "]";
        }
        return "[" + formula + carga + "]";
    }
}
