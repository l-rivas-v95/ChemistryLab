package org.chemistrylab.service;

import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OxidoIonico2DService {

    public Optional<MoleculaRepresentacionDTO> intentarConstruir(String formulaVisual) {
        return Optional.empty();
    }
}
