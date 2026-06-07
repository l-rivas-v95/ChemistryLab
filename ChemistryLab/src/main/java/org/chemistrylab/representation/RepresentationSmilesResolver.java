package org.chemistrylab.representation;

import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RepresentationSmilesResolver {

    private final RepresentationSmilesOverrideService representationSmilesOverrideService;
    private final IonicSmilesBuilderService ionicSmilesBuilderService;

    public RepresentationSmilesResolver(
            RepresentationSmilesOverrideService representationSmilesOverrideService,
            IonicSmilesBuilderService ionicSmilesBuilderService
    ) {
        this.representationSmilesOverrideService = representationSmilesOverrideService;
        this.ionicSmilesBuilderService = ionicSmilesBuilderService;
    }

    public Optional<String> resolve(MoleculaEntity molecule) {
        if (molecule == null) {
            return Optional.empty();
        }

        String formula = clean(molecule.getFormula());

        return OxoSpeciesSmilesCatalog.findNeutralOxoacid(formula)
                .or(() -> representationSmilesOverrideService.findOverride(formula))
                .or(() -> ionicSmilesBuilderService.build(formula))
                .or(() -> firstText(molecule.getCanonicalSmiles(), molecule.getIsomericSmiles()));
    }

    private Optional<String> firstText(String first, String second) {
        if (hasText(first)) {
            return Optional.of(first);
        }
        if (hasText(second)) {
            return Optional.of(second);
        }
        return Optional.empty();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
