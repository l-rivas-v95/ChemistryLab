package org.chemistrylab.chemistry.analyzer.formula;

public record ElementNode(String symbol, int multiplier) implements FormulaNode {

    @Override
    public String toFormula() {
        if (multiplier <= 1) {
            return symbol;
        }

        return symbol + multiplier;
    }
}
