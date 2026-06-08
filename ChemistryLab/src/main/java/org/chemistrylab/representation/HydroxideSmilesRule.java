package org.chemistrylab.representation;

import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HydroxideSmilesRule {

    private static final Pattern SIMPLE_HYDROXIDE = Pattern.compile("^([A-Z][a-z]?)OH$");
    private static final Pattern GROUPED_HYDROXIDE = Pattern.compile("^([A-Z][a-z]?)\\(OH\\)([2-4])$");

    private final ElementoRepository elementoRepository;

    public HydroxideSmilesRule(ElementoRepository elementoRepository) {
        this.elementoRepository = elementoRepository;
    }

    public Optional<String> build(String formula) {
        if (formula == null || formula.isBlank()) {
            return Optional.empty();
        }

        Optional<HydroxideFormula> hydroxideFormula = parse(formula);
        if (hydroxideFormula.isEmpty()) {
            return Optional.empty();
        }

        HydroxideFormula hydroxide = hydroxideFormula.get();
        if (!isSupportedMetal(hydroxide.elementSymbol())) {
            return Optional.empty();
        }

        return Optional.of(smiles(hydroxide.elementSymbol(), hydroxide.hydroxideCount()));
    }

    private Optional<HydroxideFormula> parse(String formula) {
        Matcher simple = SIMPLE_HYDROXIDE.matcher(formula);
        if (simple.matches()) {
            return Optional.of(new HydroxideFormula(simple.group(1), 1));
        }

        Matcher grouped = GROUPED_HYDROXIDE.matcher(formula);
        if (grouped.matches()) {
            return Optional.of(new HydroxideFormula(
                    grouped.group(1),
                    Integer.parseInt(grouped.group(2))
            ));
        }

        return Optional.empty();
    }

    private boolean isSupportedMetal(String symbol) {
        return elementoRepository.findBySimboloIgnoreCase(symbol)
                .map(this::isSupportedMetalElement)
                .orElse(false);
    }

    private boolean isSupportedMetalElement(ElementoEntity element) {
        if (element == null || "H".equalsIgnoreCase(element.getSimbolo())) {
            return false;
        }

        Integer group = element.getGrupoPeriodico();
        if (group != null && group >= 1 && group <= 12) {
            return true;
        }

        return isMetalCategory(element.getCategoria());
    }

    private boolean isMetalCategory(String category) {
        if (category == null || category.isBlank()) {
            return false;
        }

        String normalized = category.toLowerCase();
        if (normalized.contains("nonmetal") || normalized.contains("metalloid")) {
            return false;
        }

        return normalized.contains("metal");
    }

    private String smiles(String elementSymbol, int hydroxideCount) {
        return switch (hydroxideCount) {
            case 1 -> "[" + elementSymbol + "]O[H]";
            case 2 -> "[H]O[" + elementSymbol + "]O[H]";
            case 3 -> "O[" + elementSymbol + "](O)O";
            case 4 -> "O[" + elementSymbol + "](O)(O)O";
            default -> "";
        };
    }

    private record HydroxideFormula(String elementSymbol, int hydroxideCount) {
    }
}
