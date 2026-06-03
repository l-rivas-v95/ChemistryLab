package org.chemistrylab.service;

import org.chemistrylab.dto.ElementoDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.mapper.ElementoMapper;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElementoService {

    private final ElementoRepository elementoRepository;
    private final ElementoMapper elementoMapper;

    public ElementoService(ElementoRepository elementoRepository, ElementoMapper elementoMapper) {
        this.elementoRepository = elementoRepository;
        this.elementoMapper = elementoMapper;
    }

    public List<ElementoDTO> findAll() {
        return elementoRepository.findAll()
                .stream()
                .map(elementoMapper::toDTO)
                .toList();
    }

    public ElementoDTO findById(Long id) {
        ElementoEntity elementoEntity = elementoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        return elementoMapper.toDTO(elementoEntity);
    }

    public ElementoDTO findBySimbolo(String simbolo) {
        ElementoEntity elementoEntity = elementoRepository.findBySimboloIgnoreCase(simbolo)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        return elementoMapper.toDTO(elementoEntity);
    }

    public ElementoDTO findByNumeroAtomico(Integer numeroAtomico) {
        ElementoEntity elementoEntity = elementoRepository.findByNumeroAtomico(numeroAtomico)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        return elementoMapper.toDTO(elementoEntity);
    }

    public ElementoDTO save(ElementoDTO elementoDTO) {
        ElementoEntity elementoEntity = elementoMapper.toEntity(elementoDTO);
        ElementoEntity elementoGuardado = elementoRepository.save(elementoEntity);

        return elementoMapper.toDTO(elementoGuardado);
    }

    public ElementoDTO update(Long id, ElementoDTO elementoDTO) {
        ElementoEntity elementoEntity = elementoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        elementoMapper.updateEntityFromDTO(elementoDTO, elementoEntity);

        ElementoEntity elementoActualizado = elementoRepository.save(elementoEntity);

        return elementoMapper.toDTO(elementoActualizado);
    }

    public void deleteById(Long id) {
        ElementoEntity elementoEntity = elementoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        elementoRepository.delete(elementoEntity);
    }
}