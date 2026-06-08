package org.chemistrylab.representation;

import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RepresentationSmilesResolver {

    private final CuratedFormulaSmilesService curatedFormulaSmilesService;
    private final HydroxideSmilesRule hydroxideSmilesRule;
    private final IonicSmilesBuilderService ionicSmilesBuilderService;

    public RepresentationSmilesResolver(
            CuratedFormulaSmilesService curatedFormulaSmilesService,
            HydroxideSmilesRule hydroxideSmilesRule,
            IonicSmilesBuilderService ionicSmilesBuilderService
    ) {
        this.curatedFormulaSmilesService = curatedFormulaSmilesService;
        this.hydroxideSmilesRule = hydroxideSmilesRule;
        this.ionicSmilesBuilderService = ionicSmilesBuilderService;
    }

    public Optional<RepresentationSmilesResolution> resolve(MoleculaEntity molecule) {
        if (molecule == null) {
            return Optional.empty();
        }

        String formula = cleanFormula(molecule.getFormula());

        Optional<String> oxoacidSmiles = OxoSpeciesSmilesCatalog.findNeutralOxoacid(formula);
        if (oxoacidSmiles.isPresent()) {
            return Optional.of(new RepresentationSmilesResolution(
                    oxoacidSmiles.get(),
                    "OXOACID_CATALOG",
                    "SMILES curado para oxoacido neutro a partir de la formula."
            ));
        }

        Optional<String> curatedSmiles = curatedFormulaSmilesService.findByFormula(formula);
        if (curatedSmiles.isPresent()) {
            return Optional.of(new RepresentationSmilesResolution(
                    curatedSmiles.get(),
                    "CURATED_FORMULA_CATALOG",
                    "SMILES explicito curado para la formula."
            ));
        }

        Optional<String> hydroxideSmiles = hydroxideSmilesRule.build(formula);
        if (hydroxideSmiles.isPresent()) {
            return Optional.of(new RepresentationSmilesResolution(
                    hydroxideSmiles.get(),
                    "HYDROXIDE_FORMULA_RULE",
                    "SMILES generado desde patron general de hidroxido metalico."
            ));
        }

        Optional<String> ionicSmiles = ionicSmilesBuilderService.build(formula);
        if (ionicSmiles.isPresent()) {
            return Optional.of(new RepresentationSmilesResolution(
                    ionicSmiles.get(),
                    "IONIC_FORMULA_RESOLVER",
                    "SMILES ionico construido desde catalogo de iones y formula."
            ));
        }

        Optional<String> canonicalSmiles = text(molecule.getCanonicalSmiles());
        if (canonicalSmiles.isPresent()) {
            return Optional.of(new RepresentationSmilesResolution(
                    canonicalSmiles.get(),
                    "DATABASE_CANONICAL_SMILES",
                    "SMILES canonical obtenido desde la base de datos."
            ));
        }

        Optional<String> isomericSmiles = text(molecule.getIsomericSmiles());
        if (isomericSmiles.isPresent()) {
            return Optional.of(new RepresentationSmilesResolution(
                    isomericSmiles.get(),
                    "DATABASE_ISOMERIC_SMILES",
                    "SMILES isomerico obtenido desde la base de datos."
            ));
        }

        return Optional.empty();
    }

    private Optional<String> text(String value) {
        if (hasText(value)) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    private String cleanFormula(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\s", "");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
