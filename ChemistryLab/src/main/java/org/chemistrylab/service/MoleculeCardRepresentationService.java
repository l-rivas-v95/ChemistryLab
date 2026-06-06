package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.RepresentationSmilesOverrideService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoleculeCardRepresentationService {

    private final MoleculaRepository moleculaRepository;
    private final RepresentationSmilesOverrideService representationSmilesOverrideService;

    public MoleculeCardRepresentationService(
            MoleculaRepository moleculaRepository,
            RepresentationSmilesOverrideService representationSmilesOverrideService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.representationSmilesOverrideService = representationSmilesOverrideService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        String formula = limpiar(molecula.getFormula());

        Optional<String> override = representationSmilesOverrideService.findOverride(formula);
        if (override.isPresent()) {
            MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.smiles(
                    formula,
                    override.get(),
                    override.get(),
                    null
            );
            dto.setRepresentationInput(override.get());
            dto.setRepresentationInputSource("CARD_CURATED_SMILES_OVERRIDE");
            dto.setRepresentationInputReason("Representación aislada de tarjeta: SMILES curado por fórmula.");
            return dto;
        }

        if (tieneTexto(molecula.getCanonicalSmiles())) {
            MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.smiles(
                    formula,
                    molecula.getCanonicalSmiles(),
                    molecula.getIsomericSmiles(),
                    null
            );
            dto.setRepresentationInput(molecula.getCanonicalSmiles());
            dto.setRepresentationInputSource("CARD_CANONICAL_SMILES");
            dto.setRepresentationInputReason("Representación aislada de tarjeta: canonicalSmiles de la base de datos.");
            return dto;
        }

        if (tieneTexto(molecula.getIsomericSmiles())) {
            MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.smiles(
                    formula,
                    molecula.getIsomericSmiles(),
                    molecula.getIsomericSmiles(),
                    null
            );
            dto.setRepresentationInput(molecula.getIsomericSmiles());
            dto.setRepresentationInputSource("CARD_ISOMERIC_SMILES");
            dto.setRepresentationInputReason("Representación aislada de tarjeta: isomericSmiles de la base de datos.");
            return dto;
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

    private String limpiar(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean tieneTexto(String value) {
        return value != null && !value.isBlank();
    }
}
