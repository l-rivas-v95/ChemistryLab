package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.mapper.MoleculaMapper;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoleculaService {

    private final MoleculaRepository moleculaRepository;
    private final MoleculaMapper moleculaMapper;

    public Page<MoleculaDTO> findAllPaginado(String search, String categoria, String familia, Pageable pageable) {
        return moleculaRepository.buscarPaginado(
                        limpiarParametro(search),
                        limpiarParametro(categoria),
                        limpiarParametro(familia),
                        pageable
                )
                .map(moleculaMapper::toDTO);
    }

    public List<MoleculaDTO> findAll() {
        return moleculaRepository.findAll()
                .stream()
                .map(moleculaMapper::toDTO)
                .toList();
    }

    public MoleculaDTO findById(Long id) {
        MoleculaEntity entity = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return moleculaMapper.toDTO(entity);
    }

    public MoleculaDTO findByPubchemCid(Long pubchemCid) {
        MoleculaEntity entity = moleculaRepository.findByPubchemCid(pubchemCid)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return moleculaMapper.toDTO(entity);
    }

    public MoleculaDTO findByNombre(String nombre) {
        MoleculaEntity entity = moleculaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return moleculaMapper.toDTO(entity);
    }

    public List<MoleculaDTO> findByTipoCompuesto(String tipoCompuesto) {
        return moleculaRepository.findByTipoCompuestoIgnoreCase(tipoCompuesto)
                .stream()
                .map(moleculaMapper::toDTO)
                .toList();
    }

    private String limpiarParametro(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        return value.trim();
    }
}
