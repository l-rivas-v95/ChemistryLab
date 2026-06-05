package org.chemistrylab.dto;

public class AtomoRepresentacionDTO {

    private String id;
    private String simbolo;
    private int x;
    private int y;
    private Integer carga;
    private Integer paresLibres;

    public AtomoRepresentacionDTO() {
    }

    public AtomoRepresentacionDTO(String id, String simbolo, int x, int y, Integer carga, Integer paresLibres) {
        this.id = id;
        this.simbolo = simbolo;
        this.x = x;
        this.y = y;
        this.carga = carga;
        this.paresLibres = paresLibres;
    }

    public String getId() {
        return id;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Integer getCarga() {
        return carga;
    }

    public Integer getParesLibres() {
        return paresLibres;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCarga(Integer carga) {
        this.carga = carga;
    }

    public void setParesLibres(Integer paresLibres) {
        this.paresLibres = paresLibres;
    }
}