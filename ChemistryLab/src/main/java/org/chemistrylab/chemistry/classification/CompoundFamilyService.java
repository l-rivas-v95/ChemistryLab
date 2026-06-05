package org.chemistrylab.chemistry.classification;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompoundFamilyService {

    private final FormulaParserService formulaParserService;
    private final ElementoRepository elementoRepository;

    public CompoundFamily clasificar(MoleculaEntity molecula) {
        String tipo = normalizar(molecula.getTipoCompuesto());
        Map<String, Integer> atomos = formulaParserService.parsearFormula(molecula.getFormula());
        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomos);

        if (esOrganica(tipo)) {
            return CompoundFamily.ORGANIC;
        }

        if (esPeroxido(tipo, atomos)) {
            return CompoundFamily.PEROXIDE;
        }

        if (tipo.contains("hidroxido") || tipo.contains("hydroxide") || tipo.contains("base")) {
            return CompoundFamily.HYDROXIDE;
        }

        if (tipo.contains("acido") || tipo.contains("acid")) {
            return CompoundFamily.ACID;
        }

        if (tipo.contains("sal") || tipo.contains("salt")) {
            return CompoundFamily.SALT;
        }

        if (tipo.contains("oxido") || tipo.contains("oxide")) {
            return esOxidoMetalico(atomos, elementos)
                    ? CompoundFamily.METALLIC_OXIDE
                    : CompoundFamily.COVALENT_OXIDE;
        }

        if (esCovalente(atomos, elementos)) {
            return CompoundFamily.COVALENT;
        }

        return CompoundFamily.UNKNOWN;
    }

    private boolean esPeroxido(String tipo, Map<String, Integer> atomos) {
        if (tipo.contains("peroxido") || tipo.contains("peroxide")) {
            return true;
        }

        return atomos.size() == 2
                && atomos.getOrDefault("O", 0) >= 2
                && atomos.containsKey("H");
    }

    private boolean esOxidoMetalico(Map<String, Integer> atomos, Map<String, ElementoEntity> elementos) {
        if (!atomos.containsKey("O") || atomos.size() < 2) {
            return false;
        }

        return atomos.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .map(elementos::get)
                .anyMatch(this::esMetal);
    }

    private boolean esCovalente(Map<String, Integer> atomos, Map<String, ElementoEntity> elementos) {
        if (atomos.size() < 2) {
            return false;
        }

        return atomos.keySet().stream()
                .map(elementos::get)
                .allMatch(this::esNoMetalOMetaloide);
    }

    private Map<String, ElementoEntity> cargarElementosPorSimbolo(Map<String, Integer> atomos) {
        List<ElementoEntity> elementos = elementoRepository.findBySimboloIn(atomos.keySet());
        Map<String, ElementoEntity> mapa = new HashMap<>();

        for (ElementoEntity elemento : elementos) {
            mapa.put(elemento.getSimbolo(), elemento);
        }

        return mapa;
    }

    private boolean esMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(elemento.getCategoria());

        return categoria.contains("metal")
                && !categoria.contains("nonmetal")
                && !categoria.contains("no metal")
                && !categoria.contains("metalloid")
                && !categoria.contains("metaloide");
    }

    private boolean esNoMetalOMetaloide(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(elemento.getCategoria());

        return categoria.contains("nonmetal")
                || categoria.contains("halogen")
                || categoria.contains("noble gas")
                || categoria.contains("chalcogen")
                || categoria.contains("pnictogen")
                || categoria.contains("no metal")
                || categoria.contains("metaloide")
                || categoria.contains("metalloid");
    }

    private boolean esOrganica(String tipo) {
        return (tipo.contains("organica") || tipo.contains("organico") || tipo.contains("organic"))
                && !tipo.contains("inorganica")
                && !tipo.contains("inorganico")
                && !tipo.contains("inorganic");
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
