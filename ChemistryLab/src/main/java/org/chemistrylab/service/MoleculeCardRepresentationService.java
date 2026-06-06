package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
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
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        String formula = limpiar(molecula.getFormula());
        String smiles = representationSmilesOverrideService.findOverride(formula)
                .or(() -> ionicSmilesBuilderService.build(formula))
                .orElseGet(() -> primerTexto(molecula.getCanonicalSmiles(), molecula.getIsomericSmiles()));

        if (tieneTexto(smiles)) {
            Optional<String> svg = smilesToSvgService.renderSvg(smiles);
            if (svg.isPresent()) {
                MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.svg(
                        formula,
                        svg.get(),
                        "CDK_SVG",
                        "Representación 2D aislada generada desde SMILES curado/iónico con CDK."
                );
                dto.setRepresentationInput(smiles);
                dto.setRepresentationInputSource("CARD_CURATED_OR_DATABASE_SMILES_CDK");
                dto.setRepresentationInputReason("Orden: SMILES explícito curado, SMILES iónico por catálogo, SMILES de base de datos.");
                return dto;
            }
        }

        if (tieneTexto(molecula.getImagen2d())) {
            return MoleculaRepresentacionDTO.imagenExterna(
                    formula,
                    molecula.getImagen2d(),
                    "Fallback: imagen 2D externa importada desde PubChem."
            );
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
