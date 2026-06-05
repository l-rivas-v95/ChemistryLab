package org.chemistrylab.dto;

public class EnlaceRepresentacionDTO {

    private String origen;
    private String destino;
    private int orden;

    public EnlaceRepresentacionDTO() {
    }

    public EnlaceRepresentacionDTO(String origen, String destino, int orden) {
        this.origen = origen;
        this.destino = destino;
        this.orden = orden;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}