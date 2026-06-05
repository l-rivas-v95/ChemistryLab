package org.chemistrylab.pubchem;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PubChemCompoundData {

    private Long pubchemCid;
    private String nombre;
    private String formula;
    private BigDecimal masaMolecular;
    private String nombreIupac;
    private String descripcion;
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
