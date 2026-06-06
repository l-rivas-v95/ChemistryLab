package org.chemistrylab.chemistry.analyzer.formula;


public sealed interface FormulaNode permits ElementNode, GroupNode {

    int multiplier();

    String toFormula();
}
