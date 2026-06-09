package org.chemistrylab.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BalanceReactionResponse {

    private boolean balanced;
    private String equation;
    private List<ReactionTermDTO> reactants;
    private List<ReactionTermDTO> products;
    private String message;
}
