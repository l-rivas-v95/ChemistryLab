package org.chemistrylab.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MoleculaImportResponse {

    private String status;
    private String message;
    private Long id;
    private Long pubchemCid;
    private String nombre;
    private String formula;
}
