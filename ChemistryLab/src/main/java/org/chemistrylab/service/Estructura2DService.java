package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.dto.AtomoRepresentacionDTO;
import org.chemistrylab.dto.EnlaceRepresentacionDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Estructura2DService {

    private final FormulaParserService formulaParserService;

    public Optional<MoleculaRepresentacionDTO> intentarConstruir(String formulaVisual) {
        Map<String, Integer> composicion = formulaParserService.parsearFormula(formulaVisual);

        if (formulaParserService.esComposicion(composicion, "H", 2, "O", 2)) {
            return Optional.of(peroxidoHidrogeno(formulaVisual));
        }

        return Optional.empty();
    }

    private MoleculaRepresentacionDTO peroxidoHidrogeno(String formulaVisual) {
        List<AtomoRepresentacionDTO> atomos = List.of(
                new AtomoRepresentacionDTO("H1", "H", 45, 95, null, 0),
                new AtomoRepresentacionDTO("O1", "O", 95, 70, null, 2),
                new AtomoRepresentacionDTO("O2", "O", 165, 70, null, 2),
                new AtomoRepresentacionDTO("H2", "H", 215, 95, null, 0)
        );

        List<EnlaceRepresentacionDTO> enlaces = List.of(
                new EnlaceRepresentacionDTO("H1", "O1", 1),
                new EnlaceRepresentacionDTO("O1", "O2", 1),
                new EnlaceRepresentacionDTO("O2", "H2", 1)
        );

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                "H—O—O—H",
                "Polar"
        );
    }
}
