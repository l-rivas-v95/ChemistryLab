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
        Map<String, Integer> atomos = formulaParserService.parsearFormula(molecula.getFormula());
        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomos);

        if (esPeroxido(atomos)) {
            return CompoundFamily.PEROXIDE;
        }

        if (esHidroxido(atomos, elementos)) {
            return CompoundFamily.HYDROXIDE;
        }

        if (esAcido(atomos, elementos)) {
            return CompoundFamily.ACID;
        }

        if (esOxido(atomos)) {
            return esOxidoMetalico(atomos, elementos)
                    ? CompoundFamily.METALLIC_OXIDE
                    : CompoundFamily.COVALENT_OXIDE;
        }

        if (esSal(atomos, elementos)) {
            return CompoundFamily.SALT;
        }

        if (esOrganica(atomos)) {
            return CompoundFamily.ORGANIC;
        }

        if (esCovalente(atomos, elementos)) {
            return CompoundFamily.COVALENT;
        }

        return CompoundFamily.UNKNOWN;
    }

    private boolean esOrganica(Map<String, Integer> atomos) {
        return atomos.containsKey("C") && atomos.containsKey("H");
    }

    private boolean esPeroxido(Map<String, Integer> atomos) {
        return atomos.size() == 2
                && atomos.getOrDefault("O", 0) >= 2
                && atomos.containsKey("H");
    }

    private boolean esHidroxido(Map<String, Integer> atomos, Map<String, ElementoEntity> elementos) {
        if (!atomos.containsKey("O") || !atomos.containsKey("H") || atomos.size() < 3) {
            return false;
        }

        return atomos.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .filter(simbolo -> !"H".equals(simbolo))
                .map(elementos::get)
                .anyMatch(this::esMetal);
    }

    private boolean esAcido(Map<String, Integer> atomos, Map<String, ElementoEntity> elementos) {
        if (!atomos.containsKey("H") || atomos.size() < 2) {
            return false;
        }

        if (atomos.containsKey("C") && atomos.containsKey("H")) {
            return false;
        }

        return atomos.keySet().stream()
                .filter(simbolo -> !"H".equals(simbolo))
                .map(elementos::get)
                .noneMatch(this::esMetal);
    }

    private boolean esOxido(Map<String, Integer> atomos) {
        return atomos.containsKey("O") && atomos.size() == 2;
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

    private boolean esSal(Map<String, Integer> atomos, Map<String, ElementoEntity> elementos) {
        boolean tieneMetalOAmonio = atomos.keySet().stream()
                .map(elementos::get)
                .anyMatch(this::esMetal)
                || (atomos.getOrDefault("N", 0) > 0 && atomos.getOrDefault("H", 0) >= atomos.getOrDefault("N", 0) * 4);

        boolean tieneNoMetal = atomos.keySet().stream()
                .filter(simbolo -> !"H".equals(simbolo))
                .map(elementos::get)
                .anyMatch(this::esNoMetalOMetaloide);

        return tieneMetalOAmonio && tieneNoMetal;
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
