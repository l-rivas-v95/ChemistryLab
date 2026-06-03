package org.chemistrylab.repository;

import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoleculaRepository extends JpaRepository<MoleculaEntity, Long> {

    Optional<MoleculaEntity> findByPubchemCid(Long pubchemCid);

    Optional<MoleculaEntity> findByNombreIgnoreCase(String nombre);

    List<MoleculaEntity> findByTipoCompuestoIgnoreCase(String tipoCompuesto);
}