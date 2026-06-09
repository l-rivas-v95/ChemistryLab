package org.chemistrylab.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SandboxSuggestRequest {

    private List<SandboxElementDTO> elementos;
}
