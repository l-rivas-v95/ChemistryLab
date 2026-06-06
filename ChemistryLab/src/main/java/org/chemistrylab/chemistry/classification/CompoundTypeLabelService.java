package org.chemistrylab.chemistry.classification;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.entity.MoleculaEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompoundTypeLabelService {

    private final CompoundFamilyService compoundFamilyService;

    public String getLabel(MoleculaEntity molecule) {
        CompoundFamily family = compoundFamilyService.clasificar(molecule);

        return switch (family) {
            case ORGANIC -> "Orgánica";
            case ACID -> "Ácido inorgánico";
            case HYDROXIDE -> "Base / hidróxido";
            case SALT -> "Sal";
            case METALLIC_OXIDE -> "Óxido metálico";
            case COVALENT_OXIDE -> "Óxido covalente";
            case PEROXIDE -> "Peróxido";
            case COVALENT -> "Covalente";
            case UNKNOWN -> "Indefinida";
        };
    }
}
