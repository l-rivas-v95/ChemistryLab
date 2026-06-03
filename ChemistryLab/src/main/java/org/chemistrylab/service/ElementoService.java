package org.chemistrylab.service;

import org.chemistrylab.dto.ElementoDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElementoService {

    private final ElementoRepository elementoRepository;

    public ElementoService(ElementoRepository elementoRepository) {
        this.elementoRepository = elementoRepository;
    }

    public List<ElementoDTO> findAll() {
        return elementoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ElementoDTO findById(Long id) {
        ElementoEntity entity = elementoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        return toDTO(entity);
    }

    public ElementoDTO findBySimbolo(String simbolo) {
        ElementoEntity entity = elementoRepository.findBySimboloIgnoreCase(simbolo)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        return toDTO(entity);
    }

    public ElementoDTO findByNumeroAtomico(Integer numeroAtomico) {
        ElementoEntity entity = elementoRepository.findByNumeroAtomico(numeroAtomico)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        return toDTO(entity);
    }

    private Integer calcularElectronesValencia(Integer[] capas) {
        if (capas == null || capas.length == 0) {
            return null;
        }

        for (int i = capas.length - 1; i >= 0; i--) {
            if (capas[i] != null && capas[i] > 0) {
                return capas[i];
            }
        }

        return null;
    }

    private ElementoDTO toDTO(ElementoEntity entity) {
        return ElementoDTO.builder()
                .id(entity.getId())
                .simbolo(entity.getSimbolo())
                .nombre(entity.getNombre())
                .numeroAtomico(entity.getNumeroAtomico())
                .masaAtomica(entity.getMasaAtomica())
                .grupoPeriodico(entity.getGrupoPeriodico())
                .periodo(entity.getPeriodo())
                .bloque(entity.getBloque())
                .categoria(entity.getCategoria())
                .configuracionElectronica(entity.getConfiguracionElectronica())
                .configuracionElectronicaSemantica(entity.getConfiguracionElectronicaSemantica())
                .electronegatividad(entity.getElectronegatividad())
                .afinidadElectronica(entity.getAfinidadElectronica())
                .estado25c(entity.getEstado25c())
                .descripcion(entity.getDescripcion())
                .apariencia(entity.getApariencia())
                .puntoEbullicion(entity.getPuntoEbullicion())
                .puntoFusion(entity.getPuntoFusion())
                .densidad(entity.getDensidad())
                .calorMolar(entity.getCalorMolar())
                .descubiertoPor(entity.getDescubiertoPor())
                .nombradoPor(entity.getNombradoPor())
                .fuente(entity.getFuente())
                .imagenModeloBohr(entity.getImagenModeloBohr())
                .modelo3dBohr(entity.getModelo3dBohr())
                .imagenEspectral(entity.getImagenEspectral())
                .posicionX(entity.getPosicionX())
                .posicionY(entity.getPosicionY())
                .posicionWx(entity.getPosicionWx())
                .posicionWy(entity.getPosicionWy())
                .capas(entity.getCapas())
                .electronesValencia(calcularElectronesValencia(entity.getCapas()))
                .energiasIonizacion(entity.getEnergiasIonizacion())
                .colorCpk(entity.getColorCpk())
                .imagenTitulo(entity.getImagenTitulo())
                .imagenUrl(entity.getImagenUrl())
                .imagenAtribucion(entity.getImagenAtribucion())
                .build();
    }
}