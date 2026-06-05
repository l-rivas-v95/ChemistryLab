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

        return Optional.of(construirRepresentacion(formulaVisual, metal, cantidadMetal, cantidadOxigeno, cargaMetal));
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
            int cantidadOxigeno,
            int cargaMetal
    ) {
        List<IonVisual> iones = construirIonesOrdenados(metal, cantidadMetal, cantidadOxigeno, cargaMetal);
        List<AtomoRepresentacionDTO> atomos = new ArrayList<>();
        List<EnlaceRepresentacionDTO> enlaces = new ArrayList<>();

        int stepX = calcularSeparacion(iones.size());
        int startX = 130 - ((iones.size() - 1) * stepX) / 2;
        int y = 78;

        for (int i = 0; i < iones.size(); i++) {
            IonVisual ion = iones.get(i);
            int offsetY = calcularOffsetY(i, iones.size());
            String id = ion.simbolo() + i;

            atomos.add(new AtomoRepresentacionDTO(
                    id,
                    ion.simbolo(),
                    startX + i * stepX,
                    y + offsetY,
                    ion.carga(),
                    "O".equals(ion.simbolo()) ? 2 : 0
            ));

            if (i > 0) {
                enlaces.add(new EnlaceRepresentacionDTO(
                        iones.get(i - 1).simbolo() + (i - 1),
                        id,
                        1
                ));
            }
        }

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                "red iónica",
                null
        );
    }

    private List<IonVisual> construirIonesOrdenados(
            String metal,
            int cantidadMetal,
            int cantidadOxigeno,
            int cargaMetal
    ) {
        List<IonVisual> iones = new ArrayList<>();

        int max = Math.max(cantidadMetal, cantidadOxigeno);
        for (int i = 0; i < max; i++) {
            if (i < cantidadOxigeno) {
                iones.add(new IonVisual("O", -2));
            }
            if (i < cantidadMetal) {
                iones.add(new IonVisual(metal, cargaMetal));
            }
        }

        if (!iones.isEmpty() && !"O".equals(iones.get(iones.size() - 1).simbolo())) {
            iones.add(new IonVisual("O", -2));
        }

        return iones;
    }

    private int calcularSeparacion(int totalIones) {
        if (totalIones >= 5) {
            return 34;
        }

        if (totalIones == 4) {
            return 40;
        }

        return 48;
    }

    private int calcularOffsetY(int index, int totalIones) {
        if (totalIones <= 3) {
            return 0;
        }

        return index % 2 == 0 ? -10 : 10;
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

    private record IonVisual(String simbolo, int carga) {
    }
}
