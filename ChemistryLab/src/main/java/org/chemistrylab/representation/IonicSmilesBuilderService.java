package org.chemistrylab.representation;

import org.chemistrylab.chemistry.config.IonConfig;
import org.chemistrylab.chemistry.ionic.IonMatch;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolution;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class IonicSmilesBuilderService {

    private static final Pattern SINGLE_ATOM_FORMULA = Pattern.compile("[A-Z][a-z]?");

    private static final String AMMONIUM_FORMULA = "NH4";
    private static final String AMMONIUM_SMILES = "[NH4+]";
    private static final String HYDROXIDE_FORMULA = "OH";
    private static final String HYDROXIDE_SMILES = "[OH-]";
    private static final String CYANIDE_FORMULA = "CN";
    private static final String CYANIDE_SMILES = "[C-]#N";

    private final IonicFormulaResolver ionicFormulaResolver;

    public IonicSmilesBuilderService(IonicFormulaResolver ionicFormulaResolver) {
        this.ionicFormulaResolver = ionicFormulaResolver;
    }

    public Optional<String> build(String formula) {
        return ionicFormulaResolver.resolver(formula)
                .filter(this::isRealIonicResolution)
                .flatMap(this::buildFromResolution);
    }

    private boolean isRealIonicResolution(IonicFormulaResolution resolution) {
        if (resolution == null || resolution.cation() == null || resolution.anion() == null) {
            return false;
        }
        if (resolution.cation().ion() == null || resolution.anion().ion() == null) {
            return false;
        }

        String cationFormula = resolution.cation().ion().getFormula();
        String anionFormula = resolution.anion().ion().getFormula();
        if ("H".equals(cationFormula) && HYDROXIDE_FORMULA.equals(anionFormula)) {
            return false;
        }

        return resolution.cation().ion().getCarga() > 0 && resolution.anion().ion().getCarga() < 0;
    }

    private Optional<String> buildFromResolution(IonicFormulaResolution resolution) {
        IonMatch cationMatch = resolution.cation();
        IonMatch anionMatch = resolution.anion();

        Optional<String> ammoniumHydroxide = buildAmmoniumHydroxide(cationMatch, anionMatch);
        if (ammoniumHydroxide.isPresent()) {
            return ammoniumHydroxide;
        }

        Optional<String> metalHydroxide = buildMetalHydroxide(cationMatch, anionMatch);
        if (metalHydroxide.isPresent()) {
            return metalHydroxide;
        }

        Optional<String> ionicFragments = buildIonicFragments(cationMatch, anionMatch);
        if (ionicFragments.isPresent()) {
            return ionicFragments;
        }

        Optional<String> anionSmilesOptional = OxoSpeciesSmilesCatalog.find(anionMatch.ion().getFormula());
        if (anionSmilesOptional.isEmpty()) {
            return Optional.empty();
        }

        String cationSmiles = ionicFragment(cationMatch.ion());
        if (cationSmiles == null || cationSmiles.isBlank()) {
            return Optional.of(anionSmilesOptional.get());
        }

        return Optional.of(arrangeFragmentsAroundCenter(
                anionSmilesOptional.get(),
                anionMatch.cantidad(),
                cationSmiles,
                cationMatch.cantidad()
        ));
    }

    private Optional<String> buildAmmoniumHydroxide(IonMatch cationMatch, IonMatch anionMatch) {
        if (!AMMONIUM_FORMULA.equals(cationMatch.ion().getFormula())) {
            return Optional.empty();
        }
        if (!HYDROXIDE_FORMULA.equals(anionMatch.ion().getFormula())) {
            return Optional.empty();
        }
        if (cationMatch.cantidad() != 1 || anionMatch.cantidad() != 1) {
            return Optional.empty();
        }
        return Optional.of(AMMONIUM_SMILES + ".[H]O");
    }

    private Optional<String> buildMetalHydroxide(IonMatch cationMatch, IonMatch anionMatch) {
        if (!HYDROXIDE_FORMULA.equals(anionMatch.ion().getFormula())) {
            return Optional.empty();
        }

        String metal = neutralAtom(cationMatch.ion().getFormula());
        if (metal == null || cationMatch.cantidad() != 1) {
            return Optional.empty();
        }

        return switch (anionMatch.cantidad()) {
            case 1 -> Optional.of("[" + metal + "]O[H]");
            case 2 -> Optional.of("[H]O[" + metal + "]O[H]");
            case 3 -> Optional.of("[H]O[" + metal + "](O[H])O[H]");
            case 4 -> Optional.of("[H]O[" + metal + "](O[H])(O[H])O[H]");
            default -> Optional.empty();
        };
    }

    private Optional<String> buildIonicFragments(IonMatch cationMatch, IonMatch anionMatch) {
        String cation = ionicFragment(cationMatch.ion());
        String anion = ionicFragment(anionMatch.ion());

        if (cation == null || anion == null) {
            return Optional.empty();
        }

        return Optional.of(arrangeFragmentsAroundCenter(
                anion,
                anionMatch.cantidad(),
                cation,
                cationMatch.cantidad()
        ));
    }

    private String arrangeFragmentsAroundCenter(
            String negativeFragment,
            int negativeCount,
            String positiveFragment,
            int positiveCount
    ) {
        List<String> fragments = new ArrayList<>();

        int leftNegativeCount = negativeCount / 2;
        int rightNegativeCount = negativeCount - leftNegativeCount;

        for (int i = 0; i < leftNegativeCount; i++) {
            fragments.add(negativeFragment);
        }

        for (int i = 0; i < positiveCount; i++) {
            fragments.add(positiveFragment);
        }

        for (int i = 0; i < rightNegativeCount; i++) {
            fragments.add(negativeFragment);
        }

        return String.join(".", fragments);
    }

    private String ionicFragment(IonConfig ion) {
        if (ion == null || ion.getFormula() == null || ion.getFormula().isBlank()) {
            return null;
        }

        String formula = ion.getFormula();
        int charge = ion.getCarga();

        if (AMMONIUM_FORMULA.equals(formula)) {
            return AMMONIUM_SMILES;
        }

        if (HYDROXIDE_FORMULA.equals(formula)) {
            return HYDROXIDE_SMILES;
        }

        if (CYANIDE_FORMULA.equals(formula)) {
            return CYANIDE_SMILES;
        }

        String atom = neutralAtom(formula);
        if (atom == null) {
            return null;
        }

        String sign = charge > 0 ? "+" : "-";
        int magnitude = Math.abs(charge);
        return "[" + atom + sign + (magnitude == 1 ? "" : magnitude) + "]";
    }

    private String neutralAtom(String formula) {
        if (formula == null || !SINGLE_ATOM_FORMULA.matcher(formula).matches()) {
            return null;
        }
        return formula;
    }
}
