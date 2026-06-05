package org.chemistrylab.service;

import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class MoleculaRepresentacionService {

    private final MoleculaRepository moleculaRepository;
    private final ElementoRepository elementoRepository;
    private final FormulaParserService formulaParserService;

    private final Estructura2DService estructura2DService;
    private final MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService;

    private final MoleculaRepresentacionVseprService moleculaRepresentacionVseprService;

    public MoleculaRepresentacionService(
            MoleculaRepository moleculaRepository,
            ElementoRepository elementoRepository,
            FormulaParserService formulaParserService,
            Estructura2DService estructura2DService,
            MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService, MoleculaRepresentacionVseprService moleculaRepresentacionVseprService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.elementoRepository = elementoRepository;
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

    private Map<String, Integer> parsearFormula(String formula) {
        return formulaParserService.parsearFormula(formula);
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

    private boolean esNoMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(getCategoria(elemento));

        return categoria.contains("nonmetal")
                || categoria.contains("halogen")
                || categoria.contains("noble gas")
                || categoria.contains("chalcogen")
                || categoria.contains("pnictogen")
                || categoria.contains("no metal")
                || categoria.contains("metaloide")
                || categoria.contains("metalloid");
    }

    private boolean esMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(getCategoria(elemento));

        return categoria.contains("metal")
                && !categoria.contains("nonmetal")
                && !categoria.contains("no metal")
                && !categoria.contains("metalloid")
                && !categoria.contains("metaloide");
    }

    private boolean esComposicion(Map<String, Integer> atomos, Object... pares) {
        if (atomos == null || pares == null || pares.length % 2 != 0) {
            return false;
        }

        Map<String, Integer> esperada = new HashMap<>();

        for (int i = 0; i < pares.length; i += 2) {
            String simbolo = (String) pares[i];
            Integer cantidad = (Integer) pares[i + 1];
            esperada.put(simbolo, cantidad);
        }

        return atomos.equals(esperada);
    }

    private String getCategoria(ElementoEntity elemento) {
        return elemento.getCategoria();
    }

    private Integer getGrupo(ElementoEntity elemento) {
        return elemento.getGrupoPeriodico();
    }

    private Number getElectronegatividad(ElementoEntity elemento) {
        return elemento.getElectronegatividad() != null
                ? elemento.getElectronegatividad()
                : 99.0;
    }
}
