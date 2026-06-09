package org.chemistrylab.mapper;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.classification.CompoundFamily;
import org.chemistrylab.chemistry.classification.CompoundFamilyService;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoleculaMapper {

    private final FormulaParserService formulaParserService;
    private final CompoundFamilyService compoundFamilyService;

    public MoleculaDTO toDTO(MoleculaEntity entity) {
        if (entity == null) {
            return null;
        }

        String formulaVisible = formulaParserService.normalizarFormulaVisual(entity.getFormula());
        CompoundFamily compoundFamily = compoundFamilyService.clasificar(entity);

        return MoleculaDTO.builder()
                .id(entity.getId())
                .pubchemCid(entity.getPubchemCid())
                .nombre(entity.getNombre())
                .formula(formulaVisible)
                .masaMolecular(entity.getMasaMolecular())
                .nombreIupac(entity.getNombreIupac())
                .descripcion(entity.getDescripcion())
                .compoundFamily(compoundFamily.name())
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
