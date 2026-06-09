package org.chemistrylab.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SandboxSuggestResponse {

    private String formulaEntrada;
    private boolean exactMatchFound;
    private List<SandboxProductSuggestionDTO> suggestions;
}
