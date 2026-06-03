package org.chemistrylab.repository;

import org.chemistrylab.entity.ElementoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ElementoRepository extends JpaRepository<ElementoEntity, Long> {

    Optional<ElementoEntity> findBySimboloIgnoreCase(String simbolo);

    Optional<ElementoEntity> findByNumeroAtomico(Integer numeroAtomico);

    boolean existsByNumeroAtomico(Integer numeroAtomico);
}