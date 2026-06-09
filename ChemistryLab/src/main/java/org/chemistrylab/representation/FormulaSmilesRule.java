package org.chemistrylab.representation;

import java.util.Optional;

public interface FormulaSmilesRule {

    Optional<String> build(String formula);
}
