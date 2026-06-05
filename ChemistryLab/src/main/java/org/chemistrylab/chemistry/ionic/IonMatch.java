package org.chemistrylab.chemistry.ionic;

import org.chemistrylab.chemistry.config.IonConfig;

public class IonMatch {

    private IonConfig ion;
    private int cantidad;

    public IonMatch() {
    }

    public IonMatch(IonConfig ion, int cantidad) {
        this.ion = ion;
        this.cantidad = cantidad;
    }

    public IonConfig getIon() {
        return ion;
    }

    public void setIon(IonConfig ion) {
        this.ion = ion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}