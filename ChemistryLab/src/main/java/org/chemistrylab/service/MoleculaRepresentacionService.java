package org.chemistrylab.service;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
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

    public MoleculaRepresentacionService(
            MoleculaRepository moleculaRepository,
            FormulaParserService formulaParserService,
            Estructura2DService estructura2DService,
            MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService,
            MoleculaRepresentacionVseprService moleculaRepresentacionVseprService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.formulaParserService = formulaParserService;
        this.estructura2DService = estructura2DService;
        this.moleculaRepresentacionIonicaService = moleculaRepresentacionIonicaService;
        this.moleculaRepresentacionVseprService = moleculaRepresentacionVseprService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return construirRepresentacion(molecula);
    }

    private MoleculaRepresentacionDTO construirRepresentacion(MoleculaEntity molecula) {
        String tipo = normalizar(molecula.getTipoCompuesto());
        String formulaVisual = limpiarFormula(molecula.getFormula());

        if (esOrganica(tipo) && tieneTexto(molecula.getCanonicalSmiles())) {
            return MoleculaRepresentacionDTO.smiles(
                    formulaVisual,
                    molecula.getCanonicalSmiles(),
                    molecula.getIsomericSmiles(),
                    molecula.getImagen2d()
            );
        }

        if (moleculaRepresentacionIonicaService.esRepresentacionIonicaPreferente(tipo, formulaVisual)) {
            return MoleculaRepresentacionDTO.ionica(
                    formulaVisual,
                    moleculaRepresentacionIonicaService.construirTextoIonico(formulaVisual, tipo)
            );
        }

        MoleculaRepresentacionDTO vsepr = moleculaRepresentacionVseprService.intentarConstruir(formulaVisual);
        if (vsepr != null) {
            return vsepr;
        }

        Optional<MoleculaRepresentacionDTO> estructura2d = estructura2DService.intentarConstruir(formulaVisual);
        if (estructura2d.isPresent()) {
            return estructura2d.get();
        }

        if (moleculaRepresentacionIonicaService.esRepresentacionIonica(tipo)) {
            return MoleculaRepresentacionDTO.ionica(
                    formulaVisual,
                    moleculaRepresentacionIonicaService.construirTextoIonico(formulaVisual, tipo)
            );
        }

        return MoleculaRepresentacionDTO.formula(formulaVisual);
    }

    private boolean esOrganica(String tipo) {
        return (tipo.contains("organica") || tipo.contains("organico"))
                && !tipo.contains("inorganica")
                && !tipo.contains("inorganico");
    }

    private String limpiarFormula(String formula) {
        return formulaParserService.limpiarFormula(formula);
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
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
