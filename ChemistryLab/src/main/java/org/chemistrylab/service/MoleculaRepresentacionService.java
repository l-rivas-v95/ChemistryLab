package org.chemistrylab.service;

import org.chemistrylab.chemistry.classification.CompoundFamily;
import org.chemistrylab.chemistry.classification.CompoundFamilyService;
import org.chemistrylab.chemistry.classification.CompoundTypeLabelService;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.chemistrylab.representation.RepresentationInputResult;
import org.chemistrylab.representation.RepresentationInputService;
import org.chemistrylab.representation.RepresentationInputSource;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;

@Service
public class MoleculaRepresentacionService {

    private final MoleculaRepository moleculaRepository;
    private final FormulaParserService formulaParserService;
    private final Estructura2DService estructura2DService;
    private final MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService;
    private final MoleculaRepresentacionVseprService moleculaRepresentacionVseprService;
    private final CompoundFamilyService compoundFamilyService;
    private final CompoundTypeLabelService compoundTypeLabelService;
    private final RepresentationInputService representationInputService;

    public MoleculaRepresentacionService(
            MoleculaRepository moleculaRepository,
            FormulaParserService formulaParserService,
            Estructura2DService estructura2DService,
            MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService,
            MoleculaRepresentacionVseprService moleculaRepresentacionVseprService,
            CompoundFamilyService compoundFamilyService,
            CompoundTypeLabelService compoundTypeLabelService,
            RepresentationInputService representationInputService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.formulaParserService = formulaParserService;
        this.estructura2DService = estructura2DService;
        this.moleculaRepresentacionIonicaService = moleculaRepresentacionIonicaService;
        this.moleculaRepresentacionVseprService = moleculaRepresentacionVseprService;
        this.compoundFamilyService = compoundFamilyService;
        this.compoundTypeLabelService = compoundTypeLabelService;
        this.representationInputService = representationInputService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return construirRepresentacion(molecula);
    }

    private MoleculaRepresentacionDTO construirRepresentacion(MoleculaEntity molecula) {
        String tipo = normalizar(compoundTypeLabelService.getLabel(molecula));
        String formulaVisual = limpiarFormula(molecula.getFormula());
        CompoundFamily family = compoundFamilyService.clasificar(molecula);

        if (family == CompoundFamily.ORGANIC) {
            MoleculaRepresentacionDTO dto = intentarConstruirDesdeEntradaQuimica(molecula, formulaVisual);
            if (dto != null) {
                return dto;
            }
        }

        if (family == CompoundFamily.METALLIC_OXIDE
                || family == CompoundFamily.SALT
                || family == CompoundFamily.HYDROXIDE
                || family == CompoundFamily.ACID) {
            MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.ionica(
                    formulaVisual,
                    moleculaRepresentacionIonicaService.construirTextoIonico(formulaVisual, tipo)
            );
            completarImagen2dExterna(dto, molecula);
            return dto;
        }

        MoleculaRepresentacionDTO desdeEntradaQuimica = intentarConstruirDesdeEntradaQuimica(molecula, formulaVisual);
        if (desdeEntradaQuimica != null) {
            return desdeEntradaQuimica;
        }

        Optional<MoleculaRepresentacionDTO> estructura2d = estructura2DService.intentarConstruir(formulaVisual);
        if (estructura2d.isPresent()) {
            return estructura2d.get();
        }

        MoleculaRepresentacionDTO vsepr = moleculaRepresentacionVseprService.intentarConstruir(formulaVisual);
        if (vsepr != null) {
            return vsepr;
        }

        if (family == CompoundFamily.UNKNOWN && moleculaRepresentacionIonicaService.esRepresentacionIonica(tipo)) {
            MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.ionica(
                    formulaVisual,
                    moleculaRepresentacionIonicaService.construirTextoIonico(formulaVisual, tipo)
            );
            completarImagen2dExterna(dto, molecula);
            return dto;
        }

        return MoleculaRepresentacionDTO.formula(formulaVisual);
    }

    private MoleculaRepresentacionDTO intentarConstruirDesdeEntradaQuimica(MoleculaEntity molecula, String formulaVisual) {
        RepresentationInputResult input = representationInputService.resolveInput(
                molecula.getCanonicalSmiles(),
                molecula.getIsomericSmiles(),
                molecula.getInchi()
        );

        if (!input.hasValue()) {
            return null;
        }

        MoleculaRepresentacionDTO dto = MoleculaRepresentacionDTO.smiles(
                formulaVisual,
                valorDibujable(input, molecula.getCanonicalSmiles()),
                molecula.getIsomericSmiles(),
                molecula.getImagen2d()
        );
        completarEntradaRepresentacion(dto, input);
        return dto;
    }

    private String valorDibujable(RepresentationInputResult input, String canonicalSmiles) {
        if (input.getSource() == RepresentationInputSource.CANONICAL_SMILES
                || input.getSource() == RepresentationInputSource.ISOMERIC_SMILES) {
            return input.getValue();
        }

        return canonicalSmiles;
    }

    private void completarEntradaRepresentacion(MoleculaRepresentacionDTO dto, RepresentationInputResult input) {
        dto.setRepresentationInput(input.getValue());
        dto.setRepresentationInputSource(input.getSource().name());
        dto.setRepresentationInputReason(input.getReason());
    }

    private void completarImagen2dExterna(MoleculaRepresentacionDTO dto, MoleculaEntity molecula) {
        if (molecula.getImagen2d() == null || molecula.getImagen2d().isBlank()) {
            return;
        }

        dto.setImagen2d(molecula.getImagen2d());
        dto.setImagenRepresentacionSource("PUBCHEM_IMAGE_2D");
        dto.setImagenRepresentacionReason("La representación iónica se mantiene como texto de tarjeta y la imagen molecular usa la imagen 2D externa.");
    }

    private String limpiarFormula(String formula) {
        return formulaParserService.limpiarFormula(formula);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
