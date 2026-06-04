package org.chemistrylab.repository;

import org.chemistrylab.entity.ElementoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ElementoRepository extends JpaRepository<ElementoEntity, Long> {

    Optional<ElementoEntity> findBySimboloIgnoreCase(String simbolo);

    Optional<ElementoEntity> findByNumeroAtomico(Integer numeroAtomico);

    List<ElementoEntity> findBySimboloIn(Collection<String> simbolos);


    boolean existsByNumeroAtomico(Integer numeroAtomico);
}