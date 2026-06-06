package org.chemistrylab.representation;

public class RepresentationDecisionService {

    public RepresentationDecision decide(String familyValue) {
        RepresentationFamily family = RepresentationFamily.from(familyValue);

        return switch (family) {
            case SAL_BINARIA, HIDROXIDO, OXISAL, OXISAL_ACIDA ->
                    new RepresentationDecision(
                            family,
                            RepresentationStrategy.EDUCATIONAL_CANDIDATE,
                            "Familia susceptible de representación educativa específica."
                    );

            case COMPLEJO, ORGANOFOSFATO ->
                    new RepresentationDecision(
                            family,
                            RepresentationStrategy.SPECIAL_CASE,
                            "Familia compleja que requiere revisión específica."
                    );

            case DESCONOCIDA ->
                    new RepresentationDecision(
                            family,
                            RepresentationStrategy.FALLBACK,
                            "No existe una estrategia definida para esta familia."
                    );

            default ->
                    new RepresentationDecision(
                            family,
                            RepresentationStrategy.DIRECT_SMILES,
                            "La familia puede representarse directamente mediante SMILES estándar."
                    );
        };
    }
}
