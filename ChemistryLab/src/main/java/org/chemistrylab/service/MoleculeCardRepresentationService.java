package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.RepresentationSmilesOverrideService;
import org.chemistrylab.representation.SmilesToSvgService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoleculeCardRepresentationService {

    private final MoleculaRepository moleculaRepository;
    private final RepresentationSmilesOverrideService representationSmilesOverrideService;
    private final SmilesToSvgService smilesToSvgService;

    public MoleculeCardRepresentationService(
            MoleculaRepository moleculaRepository,
            RepresentationSmilesOverrideService representationSmilesOverrideService,
            SmilesToSvgService smilesToSvgService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.representationSmilesOverrideService = representationSmilesOverrideService;
        this.smilesToSvgService = smilesToSvgService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        String formula = limpiar(molecula.getFormula());

        Optional<String> override = representationSmilesOverrideService.findOverride(formula);
        if (override.isPresent()) {
            return crearDesdeSmiles(
                    formula,
                    override.get(),
                    "CARD_CURATED_SMILES_OVERRIDE",
                    "Representación aislada de tarjeta: SVG CDK generado desde SMILES curado por fórmula."
            );
        }

        if (tieneTexto(molecula.getCanonicalSmiles())) {
            return crearDesdeSmiles(
                    formula,
                    molecula.getCanonicalSmiles(),
                    "CARD_CANONICAL_SMILES",
                    "Representación aislada de tarjeta: SVG CDK generado desde canonicalSmiles de la base de datos."
            );
        }

        if (tieneTexto(molecula.getIsomericSmiles())) {
            return crearDesdeSmiles(
                    formula,
                    molecula.getIsomericSmiles(),
                    "CARD_ISOMERIC_SMILES",
                    "Representación aislada de tarjeta: SVG CDK generado desde isomericSmiles de la base de datos."
            );
        }

        if (tieneTexto(molecula.getImagen2d())) {
            return MoleculaRepresentacionDTO.imagenExterna(
                    formula,
                    molecula.getImagen2d(),
                    "Representación aislada de tarjeta: imagen 2D externa como fallback visual."
            );
        }

        return MoleculaRepresentacionDTO.formula(formula);
    }

    private MoleculaRepresentacionDTO crearDesdeSmiles(
            String formula,
            String smiles,
            String source,
            String reason
    ) {
        Optional<String> svg = smilesToSvgService.renderSvg(smiles);
        if (svg.isPresent()) {
            MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.svg(
                    formula,
                    svg.get(),
                    "CDK_SVG",
                    reason
            );
            dto.setRepresentationInput(smiles);
            dto.setRepresentationInputSource(source);
            dto.setRepresentationInputReason(reason);
            return dto;
        }

        MoleculaRepresentacionDTO fallback = MoleculaRepresentacionDTO.smiles(
                formula,
                smiles,
                smiles,
                null
        );
        fallback.setRepresentationInput(smiles);
        fallback.setRepresentationInputSource(source + "_FALLBACK_SMILES");
        fallback.setRepresentationInputReason("CDK no pudo generar SVG. Se conserva SMILES como fallback temporal.");
        return fallback;
    }

    private String limpiar(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean tieneTexto(String value) {
        return value != null && !value.isBlank();
    }
}
