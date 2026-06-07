package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
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
    private final MoleculeCardRepresentationService moleculeCardRepresentationService;

    public Page<MoleculaDTO> findAllPaginado(String search, String categoria, String familia, Pageable pageable) {
        String searchLimpio = limpiarParametro(search);
        String categoriaLimpia = limpiarParametro(categoria);
        String familiaLimpia = limpiarParametro(familia);

        List<MoleculaEntity> filtradas = moleculaRepository.buscarPorTexto(searchLimpio)
                .stream()
                .filter(entity -> coincideBusquedaCalculada(entity, searchLimpio))
                .filter(entity -> coincideCategoria(entity, categoriaLimpia))
                .filter(entity -> coincideFamilia(entity, familiaLimpia))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), filtradas.size());
        List<MoleculaDTO> contenido = start >= filtradas.size()
                ? List.of()
                : filtradas.subList(start, end)
                        .stream()
                        .map(this::toDTOConRepresentacion)
                        .toList();

        return new PageImpl<>(contenido, pageable, filtradas.size());
    }

    public List<MoleculaDTO> findAll() {
        return moleculaRepository.findAll()
                .stream()
                .map(this::toDTOConRepresentacion)
                .toList();
    }

    public MoleculaDTO findById(Long id) {
        MoleculaEntity entity = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molecula no encontrada"));

        return toDTOConRepresentacion(entity);
    }

    public MoleculaDTO findByPubchemCid(Long pubchemCid) {
        MoleculaEntity entity = moleculaRepository.findByPubchemCid(pubchemCid)
                .orElseThrow(() -> new RuntimeException("Molecula no encontrada"));

        return toDTOConRepresentacion(entity);
    }

    public MoleculaDTO findByNombre(String nombre) {
        MoleculaEntity entity = moleculaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Molecula no encontrada"));

        return toDTOConRepresentacion(entity);
    }

    private MoleculaDTO toDTOConRepresentacion(MoleculaEntity entity) {
        MoleculaDTO dto = moleculaMapper.toDTO(entity);
        MoleculaRepresentacionDTO representacion = moleculeCardRepresentationService.construirRepresentacion(entity);

        dto.setTipoRepresentacion(representacion.getTipoRepresentacion());
        dto.setSvg(representacion.getSvg());
        dto.setImagenRepresentacionSource(representacion.getImagenRepresentacionSource());
        dto.setImagenRepresentacionReason(representacion.getImagenRepresentacionReason());
        dto.setRepresentationInput(representacion.getRepresentationInput());
        dto.setRepresentationInputSource(representacion.getRepresentationInputSource());
        dto.setRepresentationInputReason(representacion.getRepresentationInputReason());

        return dto;
    }

    private boolean coincideBusquedaCalculada(MoleculaEntity entity, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String patron = normalizar(search);

        return normalizar(entity.getNombre()).contains(patron)
                || normalizar(entity.getFormula()).contains(patron)
                || normalizar(entity.getSinonimos()).contains(patron)
                || normalizar(moleculaMapper.toDTO(entity).getTipoCompuesto()).contains(patron);
    }

    private boolean coincideCategoria(MoleculaEntity entity, String categoria) {
        if (categoria == null || categoria.isBlank() || "all".equals(categoria)) {
            return true;
        }

        String tipo = normalizar(moleculaMapper.toDTO(entity).getTipoCompuesto());

        if ("organic".equals(categoria)) {
            return tipo.equals("organica");
        }

        if ("inorganic".equals(categoria)) {
            return !tipo.equals("organica");
        }

        return true;
    }

    private boolean coincideFamilia(MoleculaEntity entity, String familia) {
        if (familia == null || familia.isBlank()
                || "all".equals(familia)
                || "all-organic".equals(familia)
                || "all-inorganic".equals(familia)) {
            return true;
        }

        String tipo = normalizar(moleculaMapper.toDTO(entity).getTipoCompuesto());

        return switch (familia) {
            case "acid" -> tipo.contains("acido");
            case "base" -> tipo.contains("base") || tipo.contains("hidroxido");
            case "oxide" -> tipo.contains("oxido") || tipo.contains("peroxido");
            case "salt" -> tipo.contains("sal");
            case "other-inorganic" -> !tipo.equals("organica")
                    && !tipo.contains("acido")
                    && !tipo.contains("base")
                    && !tipo.contains("hidroxido")
                    && !tipo.contains("oxido")
                    && !tipo.contains("peroxido")
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
