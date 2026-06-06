package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.SmilesToSvgService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoleculeCardRepresentationService {

    private final MoleculaRepository moleculaRepository;
    private final SmilesToSvgService smilesToSvgService;

    public MoleculeCardRepresentationService(
            MoleculaRepository moleculaRepository,
            SmilesToSvgService smilesToSvgService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.smilesToSvgService = smilesToSvgService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        String formula = limpiar(molecula.getFormula());
        String smiles = primerTexto(molecula.getCanonicalSmiles(), molecula.getIsomericSmiles());

        if (tieneTexto(smiles)) {
            Optional<String> svg = smilesToSvgService.renderSvg(smiles);
            if (svg.isPresent()) {
                MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.svg(
                        formula,
                        svg.get(),
                        "CDK_SVG",
                        "Representación 2D aislada generada desde SMILES con CDK."
                );
                dto.setRepresentationInput(smiles);
                dto.setRepresentationInputSource("CARD_SMILES_CDK");
                dto.setRepresentationInputReason("Se usa el primer SMILES disponible de la base de datos para generar SVG 2D.");
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
