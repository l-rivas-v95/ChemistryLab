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

    public IonicSmilesBuilderService(IonicFormulaResolver ionicFormulaResolver) {
        this.ionicFormulaResolver = ionicFormulaResolver;
    }

    public Optional<String> build(String formula) {
        return ionicFormulaResolver.resolver(formula)
                .flatMap(this::buildFromResolution);
    }

    private Optional<String> buildFromResolution(IonicFormulaResolution resolution) {
        IonMatch cationMatch = resolution.getCation();
        IonMatch anionMatch = resolution.getAnion();

        if (cationMatch == null || anionMatch == null || cationMatch.getIon() == null || anionMatch.getIon() == null) {
            return Optional.empty();
        }

        IonConfig cation = cationMatch.getIon();
        IonConfig anion = anionMatch.getIon();

        Optional<String> compact = buildCompactVisual(cationMatch, anionMatch);
        if (compact.isPresent()) {
            return compact;
        }

        String anionSmiles = OXOANION_SMILES.get(anion.getFormula());
        if (anionSmiles == null || anionSmiles.isBlank()) {
            return Optional.empty();
        }

        List<String> fragments = new ArrayList<>();
        fragments.add(anionSmiles);

        String cationSmiles = monoatomicVisualSmiles(cation);
        if (cationSmiles == null || cationSmiles.isBlank()) {
            return Optional.of(anionSmiles);
        }

        for (int i = 0; i < cationMatch.getCantidad(); i++) {
            fragments.add(cationSmiles);
        }

        return Optional.of(String.join(".", fragments));
    }

    private Optional<String> buildCompactVisual(IonMatch cationMatch, IonMatch anionMatch) {
        IonConfig cation = cationMatch.getIon();
        IonConfig anion = anionMatch.getIon();

        String cationFormula = cation.getFormula();
        String anionFormula = anion.getFormula();

        if (isMonoatomic(anionFormula)) {
            return buildMonoatomicSalt(cationMatch, anionMatch);
        }

        if ("OH".equals(anionFormula)) {
            return buildHydroxide(cationMatch);
        }

        if ("O".equals(anionFormula)) {
            return buildOxide(cationMatch, anionMatch);
        }

        if ("CN".equals(anionFormula)) {
            return buildCyanide(cationMatch);
        }

        return Optional.empty();
    }

    private Optional<String> buildMonoatomicSalt(IonMatch cationMatch, IonMatch anionMatch) {
        String cation = neutralAtom(cationMatch.getIon().getFormula());
        String anion = neutralAtom(anionMatch.getIon().getFormula());

        if (cation == null || anion == null) {
            return Optional.empty();
        }

        if (cationMatch.getCantidad() == 1 && anionMatch.getCantidad() == 1) {
            return Optional.of("[" + cation + "]" + anion);
        }

        List<String> chain = new ArrayList<>();
        int max = Math.max(cationMatch.getCantidad(), anionMatch.getCantidad());
        for (int i = 0; i < max; i++) {
            if (i < anionMatch.getCantidad()) {
                chain.add(anion);
            }
            if (i < cationMatch.getCantidad()) {
                chain.add("[" + cation + "]");
            }
        }

        if (chain.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(String.join("", chain));
    }

    private Optional<String> buildHydroxide(IonMatch cationMatch) {
        String cation = neutralAtom(cationMatch.getIon().getFormula());
        if (cation == null) {
            return Optional.empty();
        }

        int cantidadOH = Math.max(1, cationMatch.getIon().getCarga());

        if (cantidadOH == 1) {
            return Optional.of("[" + cation + "]O[H]");
        }
        if (cantidadOH == 2) {
            return Optional.of("[H]O[" + cation + "]O[H]");
        }
        if (cantidadOH == 3) {
            return Optional.of("O([" + cation + "])(O)O");
        }

        return Optional.of("[H]O[" + cation + "]O[H]");
    }

    private Optional<String> buildOxide(IonMatch cationMatch, IonMatch anionMatch) {
        String cation = neutralAtom(cationMatch.getIon().getFormula());
        if (cation == null) {
            return Optional.empty();
        }

        if (cationMatch.getCantidad() == 1 && anionMatch.getCantidad() == 1) {
            return Optional.of("[" + cation + "]=O");
        }

        if (cationMatch.getCantidad() == 2 && anionMatch.getCantidad() == 3) {
            return Optional.of("O[" + cation + "]O[" + cation + "]O");
        }

        if (cationMatch.getCantidad() == 2 && anionMatch.getCantidad() == 1) {
            return Optional.of("[" + cation + "]O[" + cation + "]");
        }

        return Optional.of("[" + cation + "]O");
    }

    private Optional<String> buildCyanide(IonMatch cationMatch) {
        String cation = neutralAtom(cationMatch.getIon().getFormula());
        if (cation == null) {
            return Optional.of("C#N");
        }
        return Optional.of("[" + cation + "]C#N");
    }

    private boolean isMonoatomic(String formula) {
        return formula != null && formula.matches("[A-Z][a-z]?") && !"O".equals(formula);
    }

    private String monoatomicVisualSmiles(IonConfig ion) {
        if (ion == null || ion.getFormula() == null) {
            return null;
        }
        String atom = neutralAtom(ion.getFormula());
        return atom == null ? null : "[" + atom + "+]";
    }

    private String neutralAtom(String formula) {
        if (formula == null || !formula.matches("[A-Z][a-z]?")) {
            return null;
        }
        return formula;
    }
}
