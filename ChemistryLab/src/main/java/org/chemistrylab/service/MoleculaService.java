package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.mapper.MoleculaMapper;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MoleculaService {

    private final MoleculaRepository moleculaRepository;
    private final MoleculaMapper moleculaMapper;

    public Page<MoleculaDTO> findAllPaginado(String search, String categoria, String familia, Pageable pageable) {
        String searchLimpio = limpiarParametro(search);
        String categoriaLimpia = limpiarParametro(categoria);
        String familiaLimpia = limpiarParametro(familia);

        List<MoleculaDTO> filtradas = moleculaRepository.buscarPorTexto(searchLimpio)
                .stream()
                .map(moleculaMapper::toDTO)
                .filter(dto -> coincideBusquedaCalculada(dto, searchLimpio))
                .filter(dto -> coincideCategoria(dto, categoriaLimpia))
                .filter(dto -> coincideFamilia(dto, familiaLimpia))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), filtradas.size());
        List<MoleculaDTO> contenido = start >= filtradas.size()
                ? List.of()
                : filtradas.subList(start, end);

        return new PageImpl<>(contenido, pageable, filtradas.size());
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
        String tipoNormalizado = normalizar(tipoCompuesto);

        return moleculaRepository.findAll()
                .stream()
                .map(moleculaMapper::toDTO)
                .filter(dto -> normalizar(dto.getTipoCompuesto()).contains(tipoNormalizado))
                .toList();
    }

    private boolean coincideBusquedaCalculada(MoleculaDTO dto, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String patron = normalizar(search);

        return normalizar(dto.getNombre()).contains(patron)
                || normalizar(dto.getFormula()).contains(patron)
                || normalizar(dto.getSinonimos()).contains(patron)
                || normalizar(dto.getTipoCompuesto()).contains(patron);
    }

    private boolean coincideCategoria(MoleculaDTO dto, String categoria) {
        if (categoria == null || categoria.isBlank() || "all".equals(categoria)) {
            return true;
        }

        String tipo = normalizar(dto.getTipoCompuesto());

        if ("organic".equals(categoria)) {
            return tipo.equals("organica");
        }

        if ("inorganic".equals(categoria)) {
            return !tipo.equals("organica");
        }

        return true;
    }

    private boolean coincideFamilia(MoleculaDTO dto, String familia) {
        if (familia == null || familia.isBlank()
                || "all".equals(familia)
                || "all-organic".equals(familia)
                || "all-inorganic".equals(familia)) {
            return true;
        }

        String tipo = normalizar(dto.getTipoCompuesto());

        return switch (familia) {
            case "acid" -> tipo.contains("acido");
            case "base" -> tipo.contains("base") || tipo.contains("hidroxido");
            case "oxide" -> tipo.contains("oxido");
            case "salt" -> tipo.contains("sal");
            case "other-inorganic" -> !tipo.equals("organica")
                    && !tipo.contains("acido")
                    && !tipo.contains("base")
                    && !tipo.contains("hidroxido")
                    && !tipo.contains("oxido")
                    && !tipo.contains("sal");
            case "all-organic" -> tipo.equals("organica");
            default -> true;
        };
    }

    private String limpiarParametro(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        return value.trim();
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
