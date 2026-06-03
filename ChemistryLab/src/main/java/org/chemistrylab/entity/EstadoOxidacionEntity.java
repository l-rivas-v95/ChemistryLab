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

    private Integer valor;

    private Boolean comun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false)
    private ElementoEntity elemento;
}