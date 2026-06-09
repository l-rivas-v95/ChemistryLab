package org.chemistrylab.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SandboxProductSuggestionDTO {

    private Long id;
    private Long pubchemCid;
    private String nombre;
    private String formula;
    private String compoundFamily;
    private boolean exactMatch;
}
