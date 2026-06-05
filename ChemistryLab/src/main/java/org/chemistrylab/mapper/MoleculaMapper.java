package org.chemistrylab.mapper;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.service.MoleculaFormulaService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoleculaMapper {

    private final MoleculaFormulaService moleculaFormulaService;

    public MoleculaDTO toDTO(MoleculaEntity entity) {
        if (entity == null) {
            return null;
        }

        return MoleculaDTO.builder()
                .id(entity.getId())
                .pubchemCid(entity.getPubchemCid())
                .nombre(entity.getNombre())
                .formula(moleculaFormulaService.obtenerFormulaVisible(entity.getNombre(), entity.getFormula()))
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
