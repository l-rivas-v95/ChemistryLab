package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.RepresentationSmilesResolution;
import org.chemistrylab.representation.RepresentationSmilesResolver;
import org.chemistrylab.representation.SmilesToSvgService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoleculeCardRepresentationService {

    private static final String CDK_SVG_SOURCE = "CDK_SVG";
    private static final String CDK_SVG_REASON = "Representacion 2D generada desde SMILES con CDK.";

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

        return representationSmilesResolver.resolve(molecula)
                .flatMap(resolved -> buildSvgRepresentation(formula, resolved))
                .orElseGet(() -> MoleculaRepresentacionDTO.formula(formula));
    }

    private Optional<MoleculaRepresentacionDTO> buildSvgRepresentation(
            String formula,
            RepresentationSmilesResolution resolved
    ) {
        return smilesToSvgService.renderSvg(resolved.smiles())
                .map(svg -> {
                    MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.svg(
                            formula,
                            svg,
                            CDK_SVG_SOURCE,
                            CDK_SVG_REASON
                    );
                    dto.setRepresentationInput(resolved.smiles());
                    dto.setRepresentationInputSource(resolved.source());
                    dto.setRepresentationInputReason(resolved.reason());
                    return dto;
                });
    }

    private String limpiar(String value) {
        return value == null ? "" : value.trim();
    }
}
