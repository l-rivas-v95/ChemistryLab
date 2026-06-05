package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.AtomoRepresentacionDTO;
import org.chemistrylab.dto.EnlaceRepresentacionDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OxidoIonico2DService {

    private final FormulaParserService formulaParserService;
    private final ElementoRepository elementoRepository;

    public Optional<MoleculaRepresentacionDTO> intentarConstruir(String formulaVisual) {
        Map<String, Integer> atomos = formulaParserService.parsearFormula(formulaVisual);

        if (!esOxidoIonicoBinario(atomos)) {
            return Optional.empty();
        }

        String metal = atomos.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .findFirst()
                .orElse(null);

        if (metal == null) {
            return Optional.empty();
        }

        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomos);
        if (!esMetal(elementos.get(metal))) {
            return Optional.empty();
        }

        int cantidadMetal = atomos.getOrDefault(metal, 1);
        int cantidadOxigeno = atomos.getOrDefault("O", 1);
        int cargaMetal = calcularCargaMetal(cantidadMetal, cantidadOxigeno);

        if (cargaMetal <= 0) {
            return Optional.empty();
        }

        return Optional.of(construirRepresentacion(formulaVisual, metal, cantidadMetal, cantidadOxigeno));
    }

    private boolean esOxidoIonicoBinario(Map<String, Integer> atomos) {
        return atomos.size() == 2
                && atomos.containsKey("O")
                && atomos.getOrDefault("O", 0) > 0;
    }

    private int calcularCargaMetal(int cantidadMetal, int cantidadOxigeno) {
        int cargaTotalOxigeno = cantidadOxigeno * -2;
        int cargaPositivaNecesaria = -cargaTotalOxigeno;

        if (cantidadMetal <= 0 || cargaPositivaNecesaria % cantidadMetal != 0) {
            return 0;
        }

        return cargaPositivaNecesaria / cantidadMetal;
    }

    private MoleculaRepresentacionDTO construirRepresentacion(
            String formulaVisual,
            String metal,
            int cantidadMetal,
            int cantidadOxigeno
    ) {
        List<AtomoRepresentacionDTO> atomos = new ArrayList<>();
        List<EnlaceRepresentacionDTO> enlaces = new ArrayList<>();

        if (cantidadMetal == 1 && cantidadOxigeno == 1) {
            agregarIon(atomos, metal + "0", metal, 94, 78, 0);
            agregarIon(atomos, "O0", "O", 166, 78, 0);
            agregarEnlace(enlaces, metal + "0", "O0");
        } else if (cantidadMetal == 2 && cantidadOxigeno == 1) {
            agregarIon(atomos, metal + "0", metal, 58, 78, 0);
            agregarIon(atomos, "O0", "O", 130, 78, 0);
            agregarIon(atomos, metal + "1", metal, 202, 78, 0);
            agregarEnlace(enlaces, metal + "0", "O0");
            agregarEnlace(enlaces, "O0", metal + "1");
        } else if (cantidadMetal == 1 && cantidadOxigeno == 2) {
            agregarIon(atomos, "O0", "O", 58, 78, 0);
            agregarIon(atomos, metal + "0", metal, 130, 78, 0);
            agregarIon(atomos, "O1", "O", 202, 78, 0);
            agregarEnlace(enlaces, "O0", metal + "0");
            agregarEnlace(enlaces, metal + "0", "O1");
        } else if (cantidadMetal == 2 && cantidadOxigeno == 3) {
            agregarIon(atomos, "O0", "O", 130, 30, 0);
            agregarIon(atomos, metal + "0", metal, 66, 88, 0);
            agregarIon(atomos, "O1", "O", 130, 88, 0);
            agregarIon(atomos, metal + "1", metal, 194, 88, 0);
            agregarIon(atomos, "O2", "O", 130, 140, 0);
            agregarEnlace(enlaces, "O0", metal + "0");
            agregarEnlace(enlaces, "O0", metal + "1");
            agregarEnlace(enlaces, metal + "0", "O1");
            agregarEnlace(enlaces, "O1", metal + "1");
            agregarEnlace(enlaces, "O2", metal + "0");
            agregarEnlace(enlaces, "O2", metal + "1");
        } else {
            construirLayoutLineal(atomos, enlaces, metal, cantidadMetal, cantidadOxigeno);
        }

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                "red iónica",
                null
        );
    }

    private void construirLayoutLineal(
            List<AtomoRepresentacionDTO> atomos,
            List<EnlaceRepresentacionDTO> enlaces,
            String metal,
            int cantidadMetal,
            int cantidadOxigeno
    ) {
        List<String> simbolos = construirSimbolosOrdenados(metal, cantidadMetal, cantidadOxigeno);
        int stepX = 62;
        int startX = 130 - ((simbolos.size() - 1) * stepX) / 2;

        for (int i = 0; i < simbolos.size(); i++) {
            String simbolo = simbolos.get(i);
            String id = simbolo + i;

            agregarIon(atomos, id, simbolo, startX + i * stepX, 78, 0);

            if (i > 0) {
                agregarEnlace(enlaces, simbolos.get(i - 1) + (i - 1), id);
            }
        }
    }

    private void agregarIon(
            List<AtomoRepresentacionDTO> atomos,
            String id,
            String simbolo,
            int x,
            int y,
            int paresLibres
    ) {
        atomos.add(new AtomoRepresentacionDTO(id, simbolo, x, y, null, paresLibres));
    }

    private void agregarEnlace(List<EnlaceRepresentacionDTO> enlaces, String origen, String destino) {
        enlaces.add(new EnlaceRepresentacionDTO(origen, destino, 1));
    }

    private List<String> construirSimbolosOrdenados(
            String metal,
            int cantidadMetal,
            int cantidadOxigeno
    ) {
        List<String> simbolos = new ArrayList<>();

        int max = Math.max(cantidadMetal, cantidadOxigeno);
        for (int i = 0; i < max; i++) {
            if (i < cantidadOxigeno) {
                simbolos.add("O");
            }
            if (i < cantidadMetal) {
                simbolos.add(metal);
            }
        }

        return simbolos;
    }

    private Map<String, ElementoEntity> cargarElementosPorSimbolo(Map<String, Integer> atomosFormula) {
        List<ElementoEntity> elementos = elementoRepository.findBySimboloIn(atomosFormula.keySet());

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
