package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.stereotype.Service;

@Service
public class MoleculeCardRepresentationService {

    private final MoleculaRepository moleculaRepository;

    public MoleculeCardRepresentationService(MoleculaRepository moleculaRepository) {
        this.moleculaRepository = moleculaRepository;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        String formula = limpiar(molecula.getFormula());

        if (tieneTexto(molecula.getImagen2d())) {
            return MoleculaRepresentacionDTO.imagenExterna(
                    formula,
                    molecula.getImagen2d(),
                    "Representación mínima: imagen 2D externa importada desde PubChem."
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
