package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.classification.CompoundFamily;
import org.chemistrylab.chemistry.classification.CompoundFamilyService;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.SandboxElementDTO;
import org.chemistrylab.dto.SandboxProductSuggestionDTO;
import org.chemistrylab.dto.SandboxSuggestRequest;
import org.chemistrylab.dto.SandboxSuggestResponse;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SandboxSuggestionService {

    private final MoleculaRepository moleculaRepository;
    private final FormulaParserService formulaParserService;
    private final CompoundFamilyService compoundFamilyService;

    public SandboxSuggestResponse suggest(SandboxSuggestRequest request) {
        Map<String, Integer> entrada = construirComposicionEntrada(request);
        String formulaEntrada = construirFormulaEntrada(entrada);

        if (entrada.isEmpty()) {
            return SandboxSuggestResponse.builder()
                    .formulaEntrada("")
                    .exactMatchFound(false)
                    .suggestions(List.of())
                    .build();
        }

        Set<String> elementosEntrada = entrada.keySet();

        List<SandboxProductSuggestionDTO> suggestions = moleculaRepository.findAll()
                .stream()
                .map(molecula -> toSuggestionIfMatches(molecula, entrada, elementosEntrada))
                .filter(suggestion -> suggestion != null)
                .sorted(Comparator
                        .comparing(SandboxProductSuggestionDTO::isExactMatch).reversed()
                        .thenComparing(SandboxProductSuggestionDTO::getFormula, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(SandboxProductSuggestionDTO::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        boolean exactMatchFound = suggestions.stream().anyMatch(SandboxProductSuggestionDTO::isExactMatch);

        return SandboxSuggestResponse.builder()
                .formulaEntrada(formulaEntrada)
                .exactMatchFound(exactMatchFound)
                .suggestions(suggestions)
                .build();
    }

    private SandboxProductSuggestionDTO toSuggestionIfMatches(
            MoleculaEntity molecula,
            Map<String, Integer> entrada,
            Set<String> elementosEntrada
    ) {
        Map<String, Integer> composicionMolecula = formulaParserService.parsearFormula(molecula.getFormula());

        if (composicionMolecula.isEmpty() || !composicionMolecula.keySet().equals(elementosEntrada)) {
            return null;
        }

        boolean exactMatch = composicionMolecula.equals(entrada);
        String compoundFamily = obtenerFamilia(molecula);

        return SandboxProductSuggestionDTO.builder()
                .id(molecula.getId())
                .pubchemCid(molecula.getPubchemCid())
                .nombre(molecula.getNombre())
                .formula(molecula.getFormula())
                .compoundFamily(compoundFamily)
                .exactMatch(exactMatch)
                .build();
    }

    private Map<String, Integer> construirComposicionEntrada(SandboxSuggestRequest request) {
        if (request == null || request.getElementos() == null) {
            return Map.of();
        }

        Map<String, Integer> resultado = new LinkedHashMap<>();

        for (SandboxElementDTO elemento : request.getElementos()) {
            if (elemento == null || elemento.getSimbolo() == null || elemento.getSimbolo().isBlank()) {
                continue;
            }

            String simbolo = normalizarSimbolo(elemento.getSimbolo());
            int cantidad = elemento.getCantidad() == null || elemento.getCantidad() <= 0
                    ? 1
                    : elemento.getCantidad();

            resultado.put(simbolo, resultado.getOrDefault(simbolo, 0) + cantidad);
        }

        return resultado;
    }

    private String construirFormulaEntrada(Map<String, Integer> composicion) {
        return composicion.entrySet()
                .stream()
                .map(entry -> entry.getKey() + (entry.getValue() > 1 ? entry.getValue() : ""))
                .collect(Collectors.joining());
    }

    private String obtenerFamilia(MoleculaEntity molecula) {
        try {
            CompoundFamily family = compoundFamilyService.clasificar(molecula);
            return family != null ? family.name().toLowerCase(Locale.ROOT) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String normalizarSimbolo(String simbolo) {
        String limpio = simbolo.trim();

        if (limpio.length() == 1) {
            return limpio.toUpperCase(Locale.ROOT);
        }

        return limpio.substring(0, 1).toUpperCase(Locale.ROOT)
                + limpio.substring(1).toLowerCase(Locale.ROOT);
    }
}
