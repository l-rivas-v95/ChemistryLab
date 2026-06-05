package org.chemistrylab.service;

import org.chemistrylab.dto.AtomoRepresentacionDTO;
import org.chemistrylab.dto.EnlaceRepresentacionDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Estructura2DService {

    public Optional<MoleculaRepresentacionDTO> intentarConstruir(String formulaVisual) {
        Map<String, Integer> composicion = new java.util.HashMap<>();
        composicion.putAll(parsearFormulaSimple(formulaVisual));

        if (esComposicion(composicion, "H", 2, "O", 2)) {
            return Optional.of(peroxidoHidrogeno(formulaVisual));
        }

        return Optional.empty();
    }

    private MoleculaRepresentacionDTO peroxidoHidrogeno(String formulaVisual) {
        List<AtomoRepresentacionDTO> atomos = List.of(
                new AtomoRepresentacionDTO("H1", "H", 45, 95, null, 0),
                new AtomoRepresentacionDTO("O1", "O", 95, 70, null, 2),
                new AtomoRepresentacionDTO("O2", "O", 165, 70, null, 2),
                new AtomoRepresentacionDTO("H2", "H", 215, 95, null, 0)
        );

        List<EnlaceRepresentacionDTO> enlaces = List.of(
                new EnlaceRepresentacionDTO("H1", "O1", 1),
                new EnlaceRepresentacionDTO("O1", "O2", 1),
                new EnlaceRepresentacionDTO("O2", "H2", 1)
        );

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                "H—O—O—H",
                "Polar"
        );
    }

    private Map<String, Integer> parsearFormulaSimple(String formula) {
        Map<String, Integer> resultado = new java.util.LinkedHashMap<>();

        if (formula == null || formula.isBlank()) {
            return resultado;
        }

        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("([A-Z][a-z]?)(\\d*)")
                .matcher(formula);

        while (matcher.find()) {
            String simbolo = matcher.group(1);
            String cantidadTexto = matcher.group(2);

            int cantidad = cantidadTexto == null || cantidadTexto.isBlank()
                    ? 1
                    : Integer.parseInt(cantidadTexto);

            resultado.put(simbolo, resultado.getOrDefault(simbolo, 0) + cantidad);
        }

        return resultado;
    }

    private boolean esComposicion(Map<String, Integer> atomos, Object... pares) {
        if (atomos == null || pares == null || pares.length % 2 != 0) {
            return false;
        }

        Map<String, Integer> esperada = new java.util.HashMap<>();

        for (int i = 0; i < pares.length; i += 2) {
            String simbolo = (String) pares[i];
            Integer cantidad = (Integer) pares[i + 1];
            esperada.put(simbolo, cantidad);
        }

        return atomos.equals(esperada);
    }
}