package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.RepresentationSmilesResolver;
import org.chemistrylab.representation.SmilesToSvgService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoleculeCardRepresentationService {

    private final MoleculaRepository moleculaRepository;
    private final SmilesToSvgService smilesToSvgService;
    private final RepresentationSmilesResolver representationSmilesResolver;

    public MoleculeCardRepresentationService(
            MoleculaRepository moleculaRepository,
            SmilesToSvgService smilesToSvgService,
            RepresentationSmilesResolver representationSmilesResolver
    ) {
        this.moleculaRepository = moleculaRepository;
        this.smilesToSvgService = smilesToSvgService;
        this.representationSmilesResolver = representationSmilesResolver;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molecula no encontrada"));

        return construirRepresentacion(molecula);
    }

    public MoleculaRepresentacionDTO construirRepresentacion(MoleculaEntity molecula) {
        String formula = limpiar(molecula.getFormula());
        Optional<String> smiles = representationSmilesResolver.resolve(molecula);

        if (smiles.isPresent()) {
            Optional<String> svg = smilesToSvgService.renderSvg(smiles.get());
            if (svg.isPresent()) {
                MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.svg(
                        formula,
                        svg.get(),
                        "CDK_SVG",
                        "Representacion 2D generada desde SMILES con CDK."
                );
                dto.setRepresentationInput(smiles.get());
                dto.setRepresentationInputSource("CARD_CURATED_OR_DATABASE_SMILES_CDK");
                dto.setRepresentationInputReason("Orden: oxoacido neutro, SMILES explicito curado, SMILES ionico por catalogo, SMILES de base de datos.");
                return dto;
            }
        }

        return MoleculaRepresentacionDTO.formula(formula);
    }

    private String limpiar(String value) {
        return value == null ? "" : value.trim();
    }
}
