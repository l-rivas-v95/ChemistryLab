package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.OxoSpeciesSmilesCatalog;
import org.chemistrylab.representation.IonicSmilesBuilderService;
import org.chemistrylab.representation.RepresentationSmilesOverrideService;
import org.chemistrylab.representation.SmilesToSvgService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoleculeCardRepresentationService {

    private final MoleculaRepository moleculaRepository;
    private final SmilesToSvgService smilesToSvgService;
    private final RepresentationSmilesOverrideService representationSmilesOverrideService;
    private final IonicSmilesBuilderService ionicSmilesBuilderService;

    public MoleculeCardRepresentationService(
            MoleculaRepository moleculaRepository,
            SmilesToSvgService smilesToSvgService,
            RepresentationSmilesOverrideService representationSmilesOverrideService,
            IonicSmilesBuilderService ionicSmilesBuilderService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.smilesToSvgService = smilesToSvgService;
        this.representationSmilesOverrideService = representationSmilesOverrideService;
        this.ionicSmilesBuilderService = ionicSmilesBuilderService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molecula no encontrada"));

        return construirRepresentacion(molecula);
    }

    public MoleculaRepresentacionDTO construirRepresentacion(MoleculaEntity molecula) {
        String formula = limpiar(molecula.getFormula());
        String smiles = OxoSpeciesSmilesCatalog.findNeutralOxoacid(formula)
                .or(() -> representationSmilesOverrideService.findOverride(formula))
                .or(() -> ionicSmilesBuilderService.build(formula))
                .orElseGet(() -> primerTexto(molecula.getCanonicalSmiles(), molecula.getIsomericSmiles()));

        if (tieneTexto(smiles)) {
            Optional<String> svg = smilesToSvgService.renderSvg(smiles);
            if (svg.isPresent()) {
                MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.svg(
                        formula,
                        svg.get(),
                        "CDK_SVG",
                        "Representacion 2D generada desde SMILES con CDK."
                );
                dto.setRepresentationInput(smiles);
                dto.setRepresentationInputSource("CARD_CURATED_OR_DATABASE_SMILES_CDK");
                dto.setRepresentationInputReason("Orden: oxoacido neutro, SMILES explicito curado, SMILES ionico por catalogo, SMILES de base de datos.");
                return dto;
            }
        }

        return MoleculaRepresentacionDTO.formula(formula);
    }

    private String primerTexto(String primero, String segundo) {
        if (tieneTexto(primero)) {
            return primero;
        }
        if (tieneTexto(segundo)) {
            return segundo;
        }
        return null;
    }

    private String limpiar(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean tieneTexto(String value) {
        return value != null && !value.isBlank();
    }
}
