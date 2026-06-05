package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.formula.FormulaParserService;
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

@Service
@RequiredArgsConstructor
public class MoleculaRepresentacionVseprService {

    private final ElementoRepository elementoRepository;
    private final FormulaParserService formulaParserService;

    public MoleculaRepresentacionDTO intentarConstruir(String formulaVisual) {
        Map<String, Integer> atomosFormula = parsearFormula(formulaVisual);

        if (atomosFormula.isEmpty()) {
            return null;
        }

        MoleculaRepresentacionDTO diatomica = intentarDiatomica(formulaVisual, atomosFormula);
        if (diatomica != null) {
            return diatomica;
        }

        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomosFormula);

        if (!esCandidataCovalentePequena(atomosFormula, elementos)) {
            return null;
        }

        String central = elegirAtomoCentral(atomosFormula, elementos);

        if (central == null) {
            return null;
        }

        List<String> terminales = obtenerTerminales(atomosFormula, central);

        if (terminales.isEmpty() || terminales.size() > 4) {
            return null;
        }

        ElementoEntity elementoCentral = elementos.get(central);

        int paresLibres = estimarParesLibres(elementoCentral, terminales.size());
        String vsepr = construirCodigoVsepr(terminales.size(), paresLibres);

        String geometria = obtenerGeometria(vsepr);
        if (geometria == null) {
            return null;
        }

        String polaridad = estimarPolaridad(terminales, paresLibres, vsepr);

        return MoleculaRepresentacionDTO.vsepr(
                formulaVisual,
                central,
                terminales,
                paresLibres,
                vsepr,
                geometria,
                polaridad
        );
    }

    private MoleculaRepresentacionDTO intentarDiatomica(String formulaVisual, Map<String, Integer> atomosFormula) {
        int totalAtomos = atomosFormula.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalAtomos != 2) {
            return null;
        }

        List<String> atomosExpandidos = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : atomosFormula.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                atomosExpandidos.add(entry.getKey());
            }
        }

        if (atomosExpandidos.size() != 2) {
            return null;
        }

        String atomoCentral = atomosExpandidos.get(0);
        String atomoTerminal = atomosExpandidos.get(1);

        String polaridad = atomoCentral.equals(atomoTerminal)
                ? "No polar"
                : "Polar";

        return MoleculaRepresentacionDTO.vsepr(
                formulaVisual,
                atomoCentral,
                List.of(atomoTerminal),
                0,
                "AX1",
                "Lineal",
                polaridad
        );
    }

    private Map<String, ElementoEntity> cargarElementosPorSimbolo(Map<String, Integer> atomosFormula) {
        List<ElementoEntity> elementos = elementoRepository.findBySimboloIn(atomosFormula.keySet());

        Map<String, ElementoEntity> mapa = new HashMap<>();

        for (ElementoEntity elemento : elementos) {
            mapa.put(elemento.getSimbolo(), elemento);
        }

        return mapa;
    }

    private boolean esCandidataCovalentePequena(
            Map<String, Integer> atomosFormula,
            Map<String, ElementoEntity> elementos
    ) {
        if (atomosFormula.size() < 2 || atomosFormula.size() > 3) {
            return false;
        }

        int totalAtomos = atomosFormula.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalAtomos > 6) {
            return false;
        }

        for (String simbolo : atomosFormula.keySet()) {
            ElementoEntity elemento = elementos.get(simbolo);

            if (elemento == null) {
                return false;
            }

            if (!esNoMetal(elemento)) {
                return false;
            }
        }

        return true;
    }

    private String elegirAtomoCentral(
            Map<String, Integer> atomosFormula,
            Map<String, ElementoEntity> elementos
    ) {
        List<String> candidatos = atomosFormula.keySet().stream()
                .filter(simbolo -> !"H".equals(simbolo))
                .filter(simbolo -> elementos.containsKey(simbolo))
                .filter(simbolo -> esNoMetal(elementos.get(simbolo)))
                .toList();

        if (candidatos.isEmpty()) {
            return null;
        }

        if (candidatos.contains("C")) return "C";
        if (candidatos.contains("N")) return "N";
        if (candidatos.contains("S")) return "S";
        if (candidatos.contains("P")) return "P";
        if (candidatos.contains("B")) return "B";

        return candidatos.stream()
                .min((a, b) -> Double.compare(
                        getElectronegatividad(elementos.get(a)).doubleValue(),
                        getElectronegatividad(elementos.get(b)).doubleValue()
                ))
                .orElse(null);
    }

    private List<String> obtenerTerminales(Map<String, Integer> atomosFormula, String central) {
        List<String> terminales = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : atomosFormula.entrySet()) {
            String simbolo = entry.getKey();

            if (simbolo.equals(central)) {
                continue;
            }

            for (int i = 0; i < entry.getValue(); i++) {
                terminales.add(simbolo);
            }
        }

        return terminales;
    }

    private int estimarParesLibres(ElementoEntity central, int enlaces) {
        Integer grupo = getGrupo(central);

        if (grupo == null) {
            return 0;
        }

        int electronesValencia = obtenerElectronesValenciaPorGrupo(grupo);

        if (electronesValencia <= 0) {
            return 0;
        }

        int electronesRestantes = electronesValencia - enlaces;

        if (electronesRestantes <= 0) {
            return 0;
        }

        return electronesRestantes / 2;
    }

    private int obtenerElectronesValenciaPorGrupo(int grupo) {
        if (grupo >= 1 && grupo <= 2) {
            return grupo;
        }

        if (grupo >= 13 && grupo <= 18) {
            return grupo - 10;
        }

        return 0;
    }

    private String construirCodigoVsepr(int enlaces, int paresLibres) {
        if (paresLibres <= 0) {
            return "AX" + enlaces;
        }

        if (paresLibres == 1) {
            return "AX" + enlaces + "E";
        }

        return "AX" + enlaces + "E" + paresLibres;
    }

    private String obtenerGeometria(String vsepr) {
        return switch (vsepr) {
            case "AX1", "AX2" -> "Lineal";
            case "AX2E", "AX2E2" -> "Angular";
            case "AX3" -> "Trigonal plana";
            case "AX3E" -> "Piramidal trigonal";
            case "AX4" -> "Tetraédrica";
            default -> null;
        };
    }

    private String estimarPolaridad(List<String> terminales, int paresLibres, String vsepr) {
        boolean terminalesIguales = terminales.stream().distinct().count() == 1;

        if (paresLibres > 0) {
            return "Polar";
        }

        if (terminalesIguales && ("AX2".equals(vsepr) || "AX3".equals(vsepr) || "AX4".equals(vsepr))) {
            return "No polar";
        }

        return "Polar";
    }

    private boolean esNoMetal(ElementoEntity elemento) {
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

    private Map<String, Integer> parsearFormula(String formula) {
        return formulaParserService.parsearFormula(formula);
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

    private Integer getGrupo(ElementoEntity elemento) {
        return elemento.getGrupoPeriodico();
    }

    private Number getElectronegatividad(ElementoEntity elemento) {
        return elemento.getElectronegatividad() != null
                ? elemento.getElectronegatividad()
                : 99.0;
    }
}
