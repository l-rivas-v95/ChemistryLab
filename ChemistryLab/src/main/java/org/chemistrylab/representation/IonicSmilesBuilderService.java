package org.chemistrylab.representation;

import org.chemistrylab.chemistry.config.IonConfig;
import org.chemistrylab.chemistry.ionic.IonMatch;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolution;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IonicSmilesBuilderService {

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
        return resolution.cation().ion().getCarga() > 0 && resolution.anion().ion().getCarga() < 0;
    }

    private Optional<String> buildFromResolution(IonicFormulaResolution resolution) {
        IonMatch cationMatch = resolution.cation();
        IonMatch anionMatch = resolution.anion();

        Optional<String> ionicFragments = buildIonicFragments(cationMatch, anionMatch);
        if (ionicFragments.isPresent()) {
            return ionicFragments;
        }

        Optional<String> anionSmilesOptional = EducationalOxoanionSmilesCatalog.find(anionMatch.ion().getFormula());
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

        if ("NH4".equals(formula)) {
            return "[NH4+]";
        }

        if ("OH".equals(formula)) {
            return "[OH-]";
        }

        if ("CN".equals(formula)) {
            return "[C-]#N";
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
        if (formula == null || !formula.matches("[A-Z][a-z]?")) {
            return null;
        }
        return formula;
    }
}
