package org.chemistrylab.representation;

import org.springframework.stereotype.Service;

@Service
public class RepresentationInputService {

    public RepresentationInputResult resolveInput(String canonicalSmiles, String isomericSmiles, String inchi) {
        if (hasText(canonicalSmiles)) {
            return RepresentationInputResult.of(
                    canonicalSmiles,
                    RepresentationInputSource.CANONICAL_SMILES,
                    "Se usa canonicalSmiles como entrada principal para la representación."
            );
        }

        if (hasText(isomericSmiles)) {
            return RepresentationInputResult.of(
                    isomericSmiles,
                    RepresentationInputSource.ISOMERIC_SMILES,
                    "No hay canonicalSmiles. Se usa isomericSmiles como entrada alternativa."
            );
        }

        if (hasText(inchi)) {
            return RepresentationInputResult.of(
                    inchi,
                    RepresentationInputSource.INCHI,
                    "No hay SMILES disponible. Se mantiene InChI para conversión posterior con motor químico."
            );
        }

        return RepresentationInputResult.of(
                null,
                RepresentationInputSource.UNKNOWN,
                "No hay canonicalSmiles, isomericSmiles ni InChI para generar representación."
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
