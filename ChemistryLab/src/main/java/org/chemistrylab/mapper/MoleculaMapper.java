package org.chemistrylab.mapper;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.classification.CompoundFamily;
import org.chemistrylab.chemistry.classification.CompoundFamilyService;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        String tipoCalculado = obtenerTipoCalculado(entity);

        return MoleculaDTO.builder()
                .id(entity.getId())
                .pubchemCid(entity.getPubchemCid())
                .nombre(entity.getNombre())
                .formula(formulaVisible)
                .masaMolecular(entity.getMasaMolecular())
                .nombreIupac(entity.getNombreIupac())
                .descripcion(entity.getDescripcion())
                .tipoCompuesto(tipoCalculado)
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

    private String obtenerTipoCalculado(MoleculaEntity entity) {
        CompoundFamily family = compoundFamilyService.clasificar(entity);

        if (family == CompoundFamily.ORGANIC && esCovalenteInorganicoSinCarbono(entity)) {
            return "Inorganica";
        }

        return switch (family) {
            case ORGANIC -> "Organica";
            case ACID -> "Acido inorganico";
            case HYDROXIDE -> "Base / hidroxido";
            case SALT -> "Sal";
            case METALLIC_OXIDE, COVALENT_OXIDE, PEROXIDE -> "Oxido";
            case COVALENT -> "Inorganica";
            case UNKNOWN -> "Indefinida";
        };
    }

    private boolean esCovalenteInorganicoSinCarbono(MoleculaEntity entity) {
        Map<String, Integer> atomos = formulaParserService.parsearFormula(entity.getFormula());

        if (atomos.isEmpty() || atomos.containsKey("C")) {
            return false;
        }

        return atomos.keySet().stream()
                .noneMatch(simbolo -> "Na".equals(simbolo)
                        || "K".equals(simbolo)
                        || "Li".equals(simbolo)
                        || "Rb".equals(simbolo)
                        || "Cs".equals(simbolo)
                        || "Mg".equals(simbolo)
                        || "Ca".equals(simbolo)
                        || "Sr".equals(simbolo)
                        || "Ba".equals(simbolo)
                        || "Al".equals(simbolo)
                        || "Fe".equals(simbolo)
                        || "Cu".equals(simbolo)
                        || "Zn".equals(simbolo)
                        || "Mn".equals(simbolo)
                        || "Ni".equals(simbolo)
                        || "Co".equals(simbolo)
                        || "Ag".equals(simbolo)
                        || "Pb".equals(simbolo)
                        || "Sn".equals(simbolo)
                        || "Hg".equals(simbolo));
    }
}
