package org.chemistrylab.representation;

public class IonTemplate {

    private final String symbol;
    private final int charge;
    private final String smiles;

    public IonTemplate(String symbol, int charge, String smiles) {
        this.symbol = symbol;
        this.charge = charge;
        this.smiles = smiles;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getCharge() {
        return charge;
    }

    public String getSmiles() {
        return smiles;
    }
}
