package org.chemistrylab.repository;

import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                (
                    :search IS NULL
                    OR :search = ''
                    OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.formula) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.tipoCompuesto) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.sinonimos) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                AND
                (
                    :categoria IS NULL
                    OR :categoria = ''
                    OR :categoria = 'all'
                    OR (:categoria = 'organic' AND LOWER(m.tipoCompuesto) = 'orgánica')
                    OR (:categoria = 'inorganic' AND LOWER(m.tipoCompuesto) <> 'orgánica')
                )
                AND
                (
                    :familia IS NULL
                    OR :familia = ''
                    OR :familia = 'all'
                    OR :familia = 'all-organic'
                    OR :familia = 'all-inorganic'
                    OR (:familia = 'acid' AND LOWER(m.tipoCompuesto) LIKE '%ácido%')
                    OR (:familia = 'base' AND (
                        LOWER(m.tipoCompuesto) LIKE '%base%'
                        OR LOWER(m.tipoCompuesto) LIKE '%hidróxido%'
                    ))
                    OR (:familia = 'oxide' AND LOWER(m.tipoCompuesto) LIKE '%óxido%')
                    OR (:familia = 'salt' AND LOWER(m.tipoCompuesto) LIKE '%sal%')
                    OR (:familia = 'other-inorganic' AND (
                        LOWER(m.tipoCompuesto) <> 'orgánica'
                        AND LOWER(m.tipoCompuesto) NOT LIKE '%ácido%'
                        AND LOWER(m.tipoCompuesto) NOT LIKE '%base%'
                        AND LOWER(m.tipoCompuesto) NOT LIKE '%hidróxido%'
                        AND LOWER(m.tipoCompuesto) NOT LIKE '%óxido%'
                        AND LOWER(m.tipoCompuesto) NOT LIKE '%sal%'
                    ))
                )
            ORDER BY m.id ASC
            """)
    Page<MoleculaEntity> buscarPaginado(
            @Param("search") String search,
            @Param("categoria") String categoria,
            @Param("familia") String familia,
            Pageable pageable
    );
}