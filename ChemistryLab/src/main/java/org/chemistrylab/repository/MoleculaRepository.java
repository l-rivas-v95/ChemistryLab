package org.chemistrylab.repository;

import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoleculaRepository extends JpaRepository<MoleculaEntity, Long> {

    Optional<MoleculaEntity> findByPubchemCid(Long pubchemCid);

    Optional<MoleculaEntity> findByNombreIgnoreCase(String nombre);

    List<MoleculaEntity> findByTipoCompuestoIgnoreCase(String tipoCompuesto);

    @Query("""
            SELECT m
            FROM MoleculaEntity m
            WHERE
                :search IS NULL
                OR :search = ''
                OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(m.formula) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(m.sinonimos) LIKE LOWER(CONCAT('%', :search, '%'))
            ORDER BY m.id ASC
            """)
    List<MoleculaEntity> buscarPorTexto(@Param("search") String search);
}
