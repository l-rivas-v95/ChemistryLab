package org.chemistrylab.chemistry.config;

public class IonConfig {

    private String formula;
    private String nombre;
    private Integer carga;
    private String tipo;
    private String categoria;

    public IonConfig() {
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCarga() {
        return carga;
    }

    public void setCarga(Integer carga) {
        this.carga = carga;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean esCation() {
        return carga != null && carga > 0;
    }

    public boolean esAnion() {
        return carga != null && carga < 0;
    }
}
