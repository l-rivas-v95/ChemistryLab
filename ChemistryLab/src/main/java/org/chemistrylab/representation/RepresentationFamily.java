package org.chemistrylab.representation;

import java.util.Locale;

public enum RepresentationFamily {
    COVALENTE_SIMPLE,
    OXIDO_COVALENTE,
    HIDRACIDO,
    OXOACIDO,
    SAL_BINARIA,
    HIDROXIDO,
    OXISAL,
    OXISAL_ACIDA,
    COMPLEJO,
    ORGANICA,
    ORGANICA_ALQUENO,
    ORGANOFOSFATO,
    DESCONOCIDA;

    public static RepresentationFamily from(String value) {
        if (value == null || value.isBlank()) {
            return DESCONOCIDA;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);

        for (RepresentationFamily family : values()) {
            if (family.name().equals(normalized)) {
                return family;
            }
        }

        return DESCONOCIDA;
    }

    public boolean isSaltLike() {
        return this == SAL_BINARIA
                || this == HIDROXIDO
                || this == OXISAL
                || this == OXISAL_ACIDA;
    }

    public boolean isLargeOrSpecialCase() {
        return this == COMPLEJO
                || this == ORGANOFOSFATO;
    }
}
