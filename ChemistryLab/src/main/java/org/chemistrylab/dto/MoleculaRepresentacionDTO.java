package org.chemistrylab.dto;

import java.util.List;

public class MoleculaRepresentacionDTO {

    private String tipoRepresentacion; // SMILES, VSEPR, IONICA, FORMULA
    private String formulaVisual;

    private String texto;

    private String atomoCentral;
    private List<String> atomosTerminales;
    private List<EnlaceRepresentacionDTO> enlaces;
    private Integer paresLibres;
    private String vsepr;
    private String geometria;
    private String polaridad;

    private String canonicalSmiles;
    private String isomericSmiles;
    private String imagen2d;
    private String imagenRepresentacionSource;
    private String imagenRepresentacionReason;
    private String representationInput;
    private String representationInputSource;
    private String representationInputReason;

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
        dto.setImagenRepresentacionSource("STRUCTURE_2D");
        dto.setImagenRepresentacionReason("Estructura 2D construida por reglas internas de la aplicación.");

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
        dto.setImagenRepresentacionSource(imagen2d == null || imagen2d.isBlank() ? "SMILES" : "PUBCHEM_IMAGE_2D");
        dto.setImagenRepresentacionReason(imagen2d == null || imagen2d.isBlank()
                ? "No hay imagen 2D externa. La representación visual debe generarse desde SMILES."
                : "Se usa imagen 2D externa como representación visual principal.");
        return dto;
    }

    public static MoleculaRepresentacionDTO imagenExterna(String formulaVisual, String imagen2d, String reason) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("IMAGEN_2D");
        dto.setFormulaVisual(formulaVisual);
        dto.setImagen2d(imagen2d);
        dto.setImagenRepresentacionSource("PUBCHEM_IMAGE_2D");
        dto.setImagenRepresentacionReason(reason);
        return dto;
    }

    public static MoleculaRepresentacionDTO ionica(String formulaVisual, String texto) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("IONICA");
        dto.setFormulaVisual(formulaVisual);
        dto.setTexto(texto);
        dto.setImagenRepresentacionSource("CARD_TEXT_ONLY");
        dto.setImagenRepresentacionReason("La representación iónica se usa como texto de tarjeta, no como imagen molecular.");
        return dto;
    }

    public static MoleculaRepresentacionDTO formula(String formulaVisual) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("FORMULA");
        dto.setFormulaVisual(formulaVisual);
        dto.setImagenRepresentacionSource("FORMULA_ONLY");
        dto.setImagenRepresentacionReason("No hay imagen molecular disponible. Se muestra solo la fórmula.");
        return dto;
    }

    public static MoleculaRepresentacionDTO vsepr(
            String formulaVisual,
            String atomoCentral,
            List<String> atomosTerminales,
            List<EnlaceRepresentacionDTO> enlaces,
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
        dto.setEnlaces(enlaces);
        dto.setParesLibres(paresLibres);
        dto.setVsepr(vsepr);
        dto.setGeometria(geometria);
        dto.setPolaridad(polaridad);
        dto.setImagenRepresentacionSource("VSEPR");
        dto.setImagenRepresentacionReason("Representación visual construida a partir de geometría VSEPR.");
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

    public List<EnlaceRepresentacionDTO> getEnlaces() {
        return enlaces;
    }

    public void setEnlaces(List<EnlaceRepresentacionDTO> enlaces) {
        this.enlaces = enlaces;
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

    public String getImagenRepresentacionSource() {
        return imagenRepresentacionSource;
    }

    public void setImagenRepresentacionSource(String imagenRepresentacionSource) {
        this.imagenRepresentacionSource = imagenRepresentacionSource;
    }

    public String getImagenRepresentacionReason() {
        return imagenRepresentacionReason;
    }

    public void setImagenRepresentacionReason(String imagenRepresentacionReason) {
        this.imagenRepresentacionReason = imagenRepresentacionReason;
    }

    public String getRepresentationInput() {
        return representationInput;
    }

    public void setRepresentationInput(String representationInput) {
        this.representationInput = representationInput;
    }

    public String getRepresentationInputSource() {
        return representationInputSource;
    }

    public void setRepresentationInputSource(String representationInputSource) {
        this.representationInputSource = representationInputSource;
    }

    public String getRepresentationInputReason() {
        return representationInputReason;
    }

    public void setRepresentationInputReason(String representationInputReason) {
        this.representationInputReason = representationInputReason;
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
