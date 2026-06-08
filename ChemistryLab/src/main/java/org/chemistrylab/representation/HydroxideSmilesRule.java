package org.chemistrylab.representation;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HydroxideSmilesRule {

    private static final Pattern SIMPLE_HYDROXIDE = Pattern.compile("^([A-Z][a-z]?)OH$");
    private static final Pattern GROUPED_HYDROXIDE = Pattern.compile("^([A-Z][a-z]?)\\(OH\\)([2-4])$");

    private static final Set<String> METAL_SYMBOLS = Set.of(
            "Li", "Be", "Na", "Mg", "Al", "K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn",
            "Ga", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn",
            "Cs", "Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu",
            "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi",
            "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr",
            "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn", "Nh", "Fl", "Mc", "Lv"
    );

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
        return METAL_SYMBOLS.contains(symbol);
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
