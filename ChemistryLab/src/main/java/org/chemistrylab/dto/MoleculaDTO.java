package org.chemistrylab.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoleculaDTO {

    private Long id;
    private Long pubchemCid;

    private String nombre;
    private String formula;
    private BigDecimal masaMolecular;
    private String nombreIupac;
    private String descripcion;

    private String tipoCompuesto;
    private String estadoFisico;
    private Integer carga;

    private String imagen2d;
    private String modelo3dUrl;

    private String puntoFusion;
    private String puntoEbullicion;
    private String densidad;
    private String solubilidad;
    private String ph;

    private String riesgos;
    private String usos;
    private String sinonimos;

    private String canonicalSmiles;
    private String isomericSmiles;
    private String inchi;
    private String inchiKey;

    private BigDecimal xlogp;
    private BigDecimal tpsa;
    private Integer donadoresH;
    private Integer aceptoresH;
    private Integer enlacesRotables;
    private Integer atomosPesados;
    private BigDecimal complejidad;
}