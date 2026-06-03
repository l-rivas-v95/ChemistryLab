package org.chemistrylab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "moleculas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoleculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="pubchem_cid", unique = true, nullable = false)
    private Long pubchemCid;

    @Column(columnDefinition = "TEXT")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String formula;

    @Column(name = "masa_molecular", precision = 18, scale = 6)
    private BigDecimal masaMolecular;

    @Column(name = "nombre_iupac", columnDefinition = "TEXT")
    private String nombreIupac;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_compuesto", columnDefinition = "TEXT")
    private String tipoCompuesto;

    @Column(name = "estado_fisico", columnDefinition = "TEXT")
    private String estadoFisico;

    private Integer carga;

    @Column(name = "imagen_2d", columnDefinition = "TEXT")
    private String imagen2d;

    @Column(name = "modelo_3d_url", columnDefinition = "TEXT")
    private String modelo3dUrl;

    @Column(name = "punto_fusion", columnDefinition = "TEXT")
    private String puntoFusion;

    @Column(name = "punto_ebullicion", columnDefinition = "TEXT")
    private String puntoEbullicion;

    @Column(columnDefinition = "TEXT")
    private String densidad;

    @Column(columnDefinition = "TEXT")
    private String solubilidad;

    @Column(columnDefinition = "TEXT")
    private String ph;

    @Column(columnDefinition = "TEXT")
    private String riesgos;

    @Column(columnDefinition = "TEXT")
    private String usos;

    @Column(columnDefinition = "TEXT")
    private String sinonimos;

    @Column(name = "canonical_smiles", columnDefinition = "TEXT")
    private String canonicalSmiles;

    @Column(name = "isomeric_smiles", columnDefinition = "TEXT")
    private String isomericSmiles;

    @Column(columnDefinition = "TEXT")
    private String inchi;

    @Column(name = "inchi_key", columnDefinition = "TEXT")
    private String inchiKey;

    @Column(precision = 18, scale = 6)
    private BigDecimal xlogp;

    @Column(precision = 18, scale = 6)
    private BigDecimal tpsa;

    @Column(name = "donadores_h")
    private Integer donadoresH;

    @Column(name = "aceptores_h")
    private Integer aceptoresH;

    @Column(name = "enlaces_rotables")
    private Integer enlacesRotables;

    @Column(name = "atomos_pesados")
    private Integer atomosPesados;

    @Column(precision = 18, scale = 6)
    private BigDecimal complejidad;
}