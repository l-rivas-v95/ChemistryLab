package org.chemistrylab.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private BigDecimal electronegatividad;
    private String estado25c;
    private String descripcion;
}