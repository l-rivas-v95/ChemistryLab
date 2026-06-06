package org.chemistrylab.chemistry.smiles;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmilesGenerationService {

    public SmilesGenerationResult completarSmiles(String canonicalSmiles, String isomericSmiles, String inchi) {
        if (tieneTexto(canonicalSmiles)) {
            return new SmilesGenerationResult(canonicalSmiles, isomericSmiles, false);
        }

        return generarDesdeInchi(inchi)
                .orElse(new SmilesGenerationResult(canonicalSmiles, isomericSmiles, false));
    }

    public Optional<SmilesGenerationResult> generarDesdeInchi(String inchi) {
        if (!tieneTexto(inchi)) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    private boolean tieneTexto(String value) {
        return value != null && !value.isBlank();
    }
}
