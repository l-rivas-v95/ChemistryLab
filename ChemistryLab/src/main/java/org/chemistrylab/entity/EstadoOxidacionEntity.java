package org.chemistrylab.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_oxidacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoOxidacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer valor;

    @Column(nullable = false)
    private Boolean comun = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false)
    private ElementoEntity elemento;
}
