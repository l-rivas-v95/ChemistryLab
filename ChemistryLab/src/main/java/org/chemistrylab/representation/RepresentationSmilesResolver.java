package org.chemistrylab.representation;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RepresentationSmilesResolver {

    private final FormulaParserService formulaParserService;
    private final CuratedFormulaSmilesService curatedFormulaSmilesService;
    private final IonicSmilesBuilderService ionicSmilesBuilderService;

    public RepresentationSmilesResolver(
            FormulaParserService formulaParserService,
            CuratedFormulaSmilesService curatedFormulaSmilesService,
            IonicSmilesBuilderService ionicSmilesBuilderService
    ) {
        this.formulaParserService = formulaParserService;
        this.curatedFormulaSmilesService = curatedFormulaSmilesService;
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
        return formulaParserService.limpiarFormula(value);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
