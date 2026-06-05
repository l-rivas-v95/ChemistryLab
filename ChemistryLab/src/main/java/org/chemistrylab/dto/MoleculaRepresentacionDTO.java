package org.chemistrylab.dto;

import java.util.List;

public class MoleculaRepresentacionDTO {

    private String tipoRepresentacion; // SMILES, VSEPR, IONICA, FORMULA
    private String formulaVisual;

    private String texto;

    private String atomoCentral;
    private List<String> atomosTerminales;
    private Integer paresLibres;
    private String vsepr;
    private String geometria;
    private String polaridad;

    private String canonicalSmiles;
    private String isomericSmiles;
    private String imagen2d;

    private List<AtomoRepresentacionDTO> atomos2d;
    private List<EnlaceRepresentacionDTO> enlaces2d;

    public static MoleculaRepresentacionDTO estructura2d(
            String formulaVisual,
            List<AtomoRepresentacionDTO> atomos2d,
            List<EnlaceRepresentacionDTO> enlaces2d,
            String texto,
            String polaridad
    ) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();

        dto.setTipoRepresentacion("ESTRUCTURA_2D");
        dto.setFormulaVisual(formulaVisual);
        dto.setAtomos2d(atomos2d);
        dto.setEnlaces2d(enlaces2d);
        dto.setTexto(texto);
        dto.setPolaridad(polaridad);

        return dto;
    }

    public MoleculaRepresentacionDTO() {
    }

    public static MoleculaRepresentacionDTO smiles(String formulaVisual, String canonicalSmiles, String isomericSmiles, String imagen2d) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("SMILES");
        dto.setFormulaVisual(formulaVisual);
        dto.setCanonicalSmiles(canonicalSmiles);
        dto.setIsomericSmiles(isomericSmiles);
        dto.setImagen2d(imagen2d);
        return dto;
    }

    public static MoleculaRepresentacionDTO ionica(String formulaVisual, String texto) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("IONICA");
        dto.setFormulaVisual(formulaVisual);
        dto.setTexto(texto);
        return dto;
    }

    public static MoleculaRepresentacionDTO formula(String formulaVisual) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("FORMULA");
        dto.setFormulaVisual(formulaVisual);
        return dto;
    }

    public static MoleculaRepresentacionDTO vsepr(
            String formulaVisual,
            String atomoCentral,
            List<String> atomosTerminales,
            Integer paresLibres,
            String vsepr,
            String geometria,
            String polaridad
    ) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("VSEPR");
        dto.setFormulaVisual(formulaVisual);
        dto.setAtomoCentral(atomoCentral);
        dto.setAtomosTerminales(atomosTerminales);
        dto.setParesLibres(paresLibres);
        dto.setVsepr(vsepr);
        dto.setGeometria(geometria);
        dto.setPolaridad(polaridad);
        return dto;
    }

    public String getTipoRepresentacion() {
        return tipoRepresentacion;
    }

    public void setTipoRepresentacion(String tipoRepresentacion) {
        this.tipoRepresentacion = tipoRepresentacion;
    }

    public String getFormulaVisual() {
        return formulaVisual;
    }

    public void setFormulaVisual(String formulaVisual) {
        this.formulaVisual = formulaVisual;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getAtomoCentral() {
        return atomoCentral;
    }

    public void setAtomoCentral(String atomoCentral) {
        this.atomoCentral = atomoCentral;
    }

    public List<String> getAtomosTerminales() {
        return atomosTerminales;
    }

    public void setAtomosTerminales(List<String> atomosTerminales) {
        this.atomosTerminales = atomosTerminales;
    }

    public Integer getParesLibres() {
        return paresLibres;
    }

    public void setParesLibres(Integer paresLibres) {
        this.paresLibres = paresLibres;
    }

    public String getVsepr() {
        return vsepr;
    }

    public void setVsepr(String vsepr) {
        this.vsepr = vsepr;
    }

    public String getGeometria() {
        return geometria;
    }

    public void setGeometria(String geometria) {
        this.geometria = geometria;
    }

    public String getPolaridad() {
        return polaridad;
    }

    public void setPolaridad(String polaridad) {
        this.polaridad = polaridad;
    }

    public String getCanonicalSmiles() {
        return canonicalSmiles;
    }

    public void setCanonicalSmiles(String canonicalSmiles) {
        this.canonicalSmiles = canonicalSmiles;
    }

    public String getIsomericSmiles() {
        return isomericSmiles;
    }

    public void setIsomericSmiles(String isomericSmiles) {
        this.isomericSmiles = isomericSmiles;
    }

    public String getImagen2d() {
        return imagen2d;
    }

    public void setImagen2d(String imagen2d) {
        this.imagen2d = imagen2d;
    }
    public List<AtomoRepresentacionDTO> getAtomos2d() {
        return atomos2d;
    }

    public void setAtomos2d(List<AtomoRepresentacionDTO> atomos2d) {
        this.atomos2d = atomos2d;
    }

    public List<EnlaceRepresentacionDTO> getEnlaces2d() {
        return enlaces2d;
    }

    public void setEnlaces2d(List<EnlaceRepresentacionDTO> enlaces2d) {
        this.enlaces2d = enlaces2d;
    }
}