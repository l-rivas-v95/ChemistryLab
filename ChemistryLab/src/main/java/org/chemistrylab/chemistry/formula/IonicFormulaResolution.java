package org.chemistrylab.chemistry.formula;

import org.chemistrylab.chemistry.ionic.IonMatch;

public class IonicFormulaResolution {

    private IonMatch cation;
    private IonMatch anion;

    public IonicFormulaResolution() {
    }

    public IonicFormulaResolution(IonMatch cation, IonMatch anion) {
        this.cation = cation;
        this.anion = anion;
    }

    public IonMatch getCation() {
        return cation;
    }

    public void setCation(IonMatch cation) {
        this.cation = cation;
    }

    public IonMatch getAnion() {
        return anion;
    }

    public void setAnion(IonMatch anion) {
        this.anion = anion;
    }
}