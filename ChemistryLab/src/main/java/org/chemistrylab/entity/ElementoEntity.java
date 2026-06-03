package org.chemistrylab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "elementos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String simbolo;

    private String nombre;

    @Column(name = "numero_atomico")
    private Integer numeroAtomico;

    @Column(name = "masa_atomica")
    private BigDecimal masaAtomica;

    @Column(name = "grupo_periodico")
    private Integer grupoPeriodico;

    private Integer periodo;

    private String bloque;

    private String categoria;

    @Column(name = "configuracion_electronica")
    private String configuracionElectronica;

    @Column(name = "configuracion_electronica_semantica")
    private String configuracionElectronicaSemantica;

    private BigDecimal electronegatividad;

    @Column(name = "afinidad_electronica")
    private BigDecimal afinidadElectronica;

    @Column(name = "estado_25c")
    private String estado25c;

    private String descripcion;

    private String apariencia;

    @Column(name = "punto_ebullicion")
    private BigDecimal puntoEbullicion;

    @Column(name = "punto_fusion")
    private BigDecimal puntoFusion;

    private BigDecimal densidad;

    @Column(name = "calor_molar")
    private BigDecimal calorMolar;

    @Column(name = "descubierto_por")
    private String descubiertoPor;

    @Column(name = "nombrado_por")
    private String nombradoPor;

    private String fuente;

    @Column(name = "imagen_modelo_bohr")
    private String imagenModeloBohr;

    @Column(name = "modelo_3d_bohr")
    private String modelo3dBohr;

    @Column(name = "imagen_espectral")
    private String imagenEspectral;

    @Column(name = "posicion_x")
    private Integer posicionX;

    @Column(name = "posicion_y")
    private Integer posicionY;

    @Column(name = "posicion_wx")
    private Integer posicionWx;

    @Column(name = "posicion_wy")
    private Integer posicionWy;

    @Column(name = "capas", columnDefinition = "integer[]")
    private Integer[] capas;

    @Column(name = "energias_ionizacion", columnDefinition = "numeric(12,6)[]")
    private BigDecimal[] energiasIonizacion;

    @Column(name = "color_cpk")
    private String colorCpk;

    @Column(name = "imagen_titulo")
    private String imagenTitulo;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "imagen_atribucion")
    private String imagenAtribucion;
}