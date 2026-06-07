package org.chemistrylab.dto;

public class MoleculaRepresentacionDTO {

    private String tipoRepresentacion;
    private String formulaVisual;
    private String imagen2d;
    private String svg;
    private String imagenRepresentacionSource;
    private String imagenRepresentacionReason;
    private String representationInput;
    private String representationInputSource;
    private String representationInputReason;

    public MoleculaRepresentacionDTO() {}

    public static MoleculaRepresentacionDTO svg(String formulaVisual, String svg, String source, String reason) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("SVG");
        dto.setFormulaVisual(formulaVisual);
        dto.setSvg(svg);
        dto.setImagenRepresentacionSource(source);
        dto.setImagenRepresentacionReason(reason);
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

    public static MoleculaRepresentacionDTO formula(String formulaVisual) {
        MoleculaRepresentacionDTO dto = new MoleculaRepresentacionDTO();
        dto.setTipoRepresentacion("FORMULA");
        dto.setFormulaVisual(formulaVisual);
        dto.setImagenRepresentacionSource("FORMULA_ONLY");
        dto.setImagenRepresentacionReason("No hay imagen molecular disponible. Se muestra solo la formula.");
        return dto;
    }

    public String getTipoRepresentacion() { return tipoRepresentacion; }
    public void setTipoRepresentacion(String tipoRepresentacion) { this.tipoRepresentacion = tipoRepresentacion; }
    public String getFormulaVisual() { return formulaVisual; }
    public void setFormulaVisual(String formulaVisual) { this.formulaVisual = formulaVisual; }
    public String getImagen2d() { return imagen2d; }
    public void setImagen2d(String imagen2d) { this.imagen2d = imagen2d; }
    public String getSvg() { return svg; }
    public void setSvg(String svg) { this.svg = svg; }
    public String getImagenRepresentacionSource() { return imagenRepresentacionSource; }
    public void setImagenRepresentacionSource(String imagenRepresentacionSource) { this.imagenRepresentacionSource = imagenRepresentacionSource; }
    public String getImagenRepresentacionReason() { return imagenRepresentacionReason; }
    public void setImagenRepresentacionReason(String imagenRepresentacionReason) { this.imagenRepresentacionReason = imagenRepresentacionReason; }
    public String getRepresentationInput() { return representationInput; }
    public void setRepresentationInput(String representationInput) { this.representationInput = representationInput; }
    public String getRepresentationInputSource() { return representationInputSource; }
    public void setRepresentationInputSource(String representationInputSource) { this.representationInputSource = representationInputSource; }
    public String getRepresentationInputReason() { return representationInputReason; }
    public void setRepresentationInputReason(String representationInputReason) { this.representationInputReason = representationInputReason; }
}
