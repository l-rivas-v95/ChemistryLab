package org.chemistrylab.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReactionTermDTO {

    private String formula;
    private Integer coefficient;
}
