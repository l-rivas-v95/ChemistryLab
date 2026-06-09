package org.chemistrylab.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BalanceReactionRequest {

    private List<String> reactants;
    private List<String> products;
}
