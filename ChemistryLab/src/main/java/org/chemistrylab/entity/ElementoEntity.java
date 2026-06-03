package org.chemistrylab.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

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

    @Column(nullable = false, unique = true, length = 5)
    private String simbolo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "numero_atomico", nullable = false, unique = true)
    private Integer numeroAtomico;

    @Column(name = "masa_atomica", nullable = false, precision = 10, scale = 4)
    private BigDecimal masaAtomica;

    @Column(name = "grupo_periodico")
    private Integer grupoPeriodico;

    private Integer periodo;

    @Column(length = 1)
    private String bloque;

    @Column(length = 50)
    private String categoria;

    @Column(name = "configuracion_electronica", length = 255)
    private String configuracionElectronica;

    @Column(precision = 5, scale = 2)
    private BigDecimal electronegatividad;

    @Column(name = "estado_25c", length = 20)
    private String estado25c;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "elemento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstadoOxidacionEntity> estadosOxidacion = new ArrayList<>();
}
