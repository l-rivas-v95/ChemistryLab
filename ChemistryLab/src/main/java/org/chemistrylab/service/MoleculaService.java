package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoleculaService {

    private final MoleculaRepository moleculaRepository;

    public MoleculaService(MoleculaRepository moleculaRepository) {
        this.moleculaRepository = moleculaRepository;
    }

    public Page<MoleculaDTO> findAllPaginado(String search, String categoria, String familia, Pageable pageable) {
        return moleculaRepository.buscarPaginado(
                        limpiarParametro(search),
                        limpiarParametro(categoria),
                        limpiarParametro(familia),
                        pageable
                )
                .map(this::toDTO);
    }

    public List<MoleculaDTO> findAll() {
        return moleculaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public MoleculaDTO findById(Long id) {
        MoleculaEntity entity = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return toDTO(entity);
    }

    public MoleculaDTO findByPubchemCid(Long pubchemCid) {
        MoleculaEntity entity = moleculaRepository.findByPubchemCid(pubchemCid)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return toDTO(entity);
    }

    public MoleculaDTO findByNombre(String nombre) {
        MoleculaEntity entity = moleculaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return toDTO(entity);
    }

    public List<MoleculaDTO> findByTipoCompuesto(String tipoCompuesto) {
        return moleculaRepository.findByTipoCompuestoIgnoreCase(tipoCompuesto)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private String limpiarParametro(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        return value.trim();
    }

    private MoleculaDTO toDTO(MoleculaEntity entity) {
        return MoleculaDTO.builder()
                .id(entity.getId())
                .pubchemCid(entity.getPubchemCid())
                .nombre(entity.getNombre())
                .formula(entity.getFormula())
                .masaMolecular(entity.getMasaMolecular())
                .nombreIupac(entity.getNombreIupac())
                .descripcion(entity.getDescripcion())
                .tipoCompuesto(entity.getTipoCompuesto())
                .estadoFisico(entity.getEstadoFisico())
                .carga(entity.getCarga())
                .imagen2d(entity.getImagen2d())
                .modelo3dUrl(entity.getModelo3dUrl())
                .puntoFusion(entity.getPuntoFusion())
                .puntoEbullicion(entity.getPuntoEbullicion())
                .densidad(entity.getDensidad())
                .solubilidad(entity.getSolubilidad())
                .ph(entity.getPh())
                .riesgos(entity.getRiesgos())
                .usos(entity.getUsos())
                .sinonimos(entity.getSinonimos())
                .canonicalSmiles(entity.getCanonicalSmiles())
                .isomericSmiles(entity.getIsomericSmiles())
                .inchi(entity.getInchi())
                .inchiKey(entity.getInchiKey())
                .xlogp(entity.getXlogp())
                .tpsa(entity.getTpsa())
                .donadoresH(entity.getDonadoresH())
                .aceptoresH(entity.getAceptoresH())
                .enlacesRotables(entity.getEnlacesRotables())
                .atomosPesados(entity.getAtomosPesados())
                .complejidad(entity.getComplejidad())
                .build();
    }
}