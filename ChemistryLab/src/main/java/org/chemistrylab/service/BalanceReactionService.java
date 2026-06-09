package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.BalanceReactionRequest;
import org.chemistrylab.dto.BalanceReactionResponse;
import org.chemistrylab.dto.ReactionTermDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BalanceReactionService {

    private static final int MAX_COEFFICIENT = 20;
    private static final int MAX_TERMS = 6;

    private final FormulaParserService formulaParserService;

    public BalanceReactionResponse balance(BalanceReactionRequest request) {
        List<String> reactants = cleanTerms(request == null ? null : request.getReactants());
        List<String> products = cleanTerms(request == null ? null : request.getProducts());

        if (reactants.isEmpty() || products.isEmpty()) {
            return notBalanced("Debes indicar al menos un reactivo y un producto.", reactants, products);
        }

        int totalTerms = reactants.size() + products.size();
        if (totalTerms > MAX_TERMS) {
            return notBalanced("Demasiados términos para el balanceador básico.", reactants, products);
        }

        List<Map<String, Integer>> reactantCompositions = reactants.stream()
                .map(formulaParserService::parsearFormula)
                .toList();
        List<Map<String, Integer>> productCompositions = products.stream()
                .map(formulaParserService::parsearFormula)
                .toList();

        int[] coefficients = searchCoefficients(reactantCompositions, productCompositions, totalTerms, new int[totalTerms], 0);

        if (coefficients == null) {
            return notBalanced("No se ha encontrado ajuste con coeficientes entre 1 y " + MAX_COEFFICIENT + ".", reactants, products);
        }

        coefficients = simplify(coefficients);

        List<ReactionTermDTO> balancedReactants = buildTerms(reactants, coefficients, 0);
        List<ReactionTermDTO> balancedProducts = buildTerms(products, coefficients, reactants.size());

        return BalanceReactionResponse.builder()
                .balanced(true)
                .equation(buildEquation(balancedReactants, balancedProducts))
                .reactants(balancedReactants)
                .products(balancedProducts)
                .message("Reacción ajustada correctamente.")
                .build();
    }

    private List<String> cleanTerms(List<String> terms) {
        if (terms == null) {
            return List.of();
        }

        return terms.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(term -> !term.isBlank())
                .toList();
    }

    private int[] searchCoefficients(
            List<Map<String, Integer>> reactants,
            List<Map<String, Integer>> products,
            int totalTerms,
            int[] current,
            int index
    ) {
        if (index == totalTerms) {
            return isBalanced(reactants, products, current) ? current.clone() : null;
        }

        for (int coefficient = 1; coefficient <= MAX_COEFFICIENT; coefficient++) {
            current[index] = coefficient;
            int[] found = searchCoefficients(reactants, products, totalTerms, current, index + 1);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private boolean isBalanced(List<Map<String, Integer>> reactants, List<Map<String, Integer>> products, int[] coefficients) {
        Map<String, Integer> left = new LinkedHashMap<>();
        Map<String, Integer> right = new LinkedHashMap<>();

        for (int i = 0; i < reactants.size(); i++) {
            addComposition(left, reactants.get(i), coefficients[i]);
        }

        for (int i = 0; i < products.size(); i++) {
            addComposition(right, products.get(i), coefficients[reactants.size() + i]);
        }

        return left.equals(right);
    }

    private void addComposition(Map<String, Integer> target, Map<String, Integer> composition, int coefficient) {
        for (Map.Entry<String, Integer> entry : composition.entrySet()) {
            target.put(entry.getKey(), target.getOrDefault(entry.getKey(), 0) + entry.getValue() * coefficient);
        }
    }

    private int[] simplify(int[] coefficients) {
        int gcd = 0;
        for (int coefficient : coefficients) {
            gcd = gcd == 0 ? coefficient : gcd(gcd, coefficient);
        }

        if (gcd <= 1) {
            return coefficients;
        }

        int[] simplified = new int[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            simplified[i] = coefficients[i] / gcd;
        }
        return simplified;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    private List<ReactionTermDTO> buildTerms(List<String> formulas, int[] coefficients, int offset) {
        List<ReactionTermDTO> terms = new ArrayList<>();
        for (int i = 0; i < formulas.size(); i++) {
            terms.add(ReactionTermDTO.builder()
                    .formula(formulas.get(i))
                    .coefficient(coefficients[offset + i])
                    .build());
        }
        return terms;
    }

    private String buildEquation(List<ReactionTermDTO> reactants, List<ReactionTermDTO> products) {
        return buildSide(reactants) + " -> " + buildSide(products);
    }

    private String buildSide(List<ReactionTermDTO> terms) {
        return terms.stream()
                .map(term -> formatTerm(term.getCoefficient(), term.getFormula()))
                .reduce((left, right) -> left + " + " + right)
                .orElse("");
    }

    private String formatTerm(Integer coefficient, String formula) {
        if (coefficient == null || coefficient <= 1) {
            return formula;
        }
        return coefficient + formula;
    }

    private BalanceReactionResponse notBalanced(String message, List<String> reactants, List<String> products) {
        return BalanceReactionResponse.builder()
                .balanced(false)
                .equation(buildEquation(buildTermsWithOne(reactants), buildTermsWithOne(products)))
                .reactants(buildTermsWithOne(reactants))
                .products(buildTermsWithOne(products))
                .message(message)
                .build();
    }

    private List<ReactionTermDTO> buildTermsWithOne(List<String> formulas) {
        return formulas.stream()
                .map(formula -> ReactionTermDTO.builder()
                        .formula(formula)
                        .coefficient(1)
                        .build())
                .toList();
    }
}
