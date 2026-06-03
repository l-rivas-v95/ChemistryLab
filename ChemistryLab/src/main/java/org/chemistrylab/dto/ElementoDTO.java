package org.chemistrylab.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementoDTO {

    private Long id;
    private String simbolo;
    private String nombre;
    private Integer numeroAtomico;
    private BigDecimal masaAtomica;
    private Integer grupoPeriodico;
    private Integer periodo;
    private String bloque;
    private String categoria;
    private String configuracionElectronica;
    private String configuracionElectronicaSemantica;
    private BigDecimal electronegatividad;
    private BigDecimal afinidadElectronica;
    private String estado25c;
    private String descripcion;
    private String apariencia;
    private BigDecimal puntoEbullicion;
    private BigDecimal puntoFusion;
    private BigDecimal densidad;
    private BigDecimal calorMolar;
    private String descubiertoPor;
    private String nombradoPor;
    private String fuente;
    private String imagenModeloBohr;
    private String modelo3dBohr;
    private String imagenEspectral;
    private Integer posicionX;
    private Integer posicionY;
    private Integer posicionWx;
    private Integer posicionWy;
    private Integer[] capas;
    private Integer electronesValencia;
    private BigDecimal[] energiasIonizacion;
    private String colorCpk;
    private String imagenTitulo;
    private String imagenUrl;
    private String imagenAtribucion;
}