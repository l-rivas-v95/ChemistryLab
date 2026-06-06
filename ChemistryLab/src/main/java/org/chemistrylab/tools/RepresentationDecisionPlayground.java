package org.chemistrylab.tools;

import org.chemistrylab.representation.RepresentationDecision;
import org.chemistrylab.representation.RepresentationDecisionService;

import java.util.List;

public class RepresentationDecisionPlayground {

    public static void main(String[] args) {
        RepresentationDecisionService service = new RepresentationDecisionService();

        List<String> families = List.of(
                "COVALENTE_SIMPLE",
                "OXIDO_COVALENTE",
                "HIDRACIDO",
                "OXOACIDO",
                "SAL_BINARIA",
                "HIDROXIDO",
                "OXISAL",
                "OXISAL_ACIDA",
                "COMPLEJO",
                "ORGANICA",
                "ORGANICA_ALQUENO",
                "ORGANOFOSFATO",
                "familia_no_registrada"
        );

        for (String family : families) {
            RepresentationDecision decision = service.decide(family);

            System.out.println("========================================");
            System.out.println("family input: " + family);
            System.out.println("family enum : " + decision.family());
            System.out.println("strategy    : " + decision.strategy());
            System.out.println("reason      : " + decision.reason());
        }
    }
}
