package org.chemistrylab.chemistry.analyzer.formula;

import java.util.List;

public record GroupNode(List<FormulaNode> children, int multiplier) implements FormulaNode {

    @Override
    public String toFormula() {
        String contenido = children.stream()
                .map(FormulaNode::toFormula)
                .reduce("", String::concat);

        if (multiplier <= 1) {
            return contenido;
        }

        return "(" + contenido + ")" + multiplier;
    }
}
