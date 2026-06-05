package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.connectivity.MolecularBond;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivityService;
import org.chemistrylab.dto.AtomoRepresentacionDTO;
import org.chemistrylab.dto.EnlaceRepresentacionDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Estructura2DService {

    private final MolecularConnectivityService molecularConnectivityService;

    public Optional<MoleculaRepresentacionDTO> intentarConstruir(String formulaVisual) {
        return molecularConnectivityService.construir(formulaVisual)
                .filter(this::esCadenaSimple)
                .map(connectivity -> construirCadenaSimple(formulaVisual, connectivity));
    }

    private boolean esCadenaSimple(MolecularConnectivity connectivity) {
        return connectivity.getBonds() != null
                && connectivity.getBonds().size() >= 2
                && connectivity.getBonds().stream().allMatch(bond -> bond.getOrder() == 1);
    }

    private MoleculaRepresentacionDTO construirCadenaSimple(
            String formulaVisual,
            MolecularConnectivity connectivity
    ) {
        List<AtomoRepresentacionDTO> atomos = new ArrayList<>();
        List<EnlaceRepresentacionDTO> enlaces = new ArrayList<>();

        List<MolecularBond> bonds = connectivity.getBonds();
        int startX = 45;
        int stepX = 55;
        int y = 80;

        String previous = null;
        int index = 0;

        for (MolecularBond bond : bonds) {
            String fromId = crearId(bond.getFrom(), index);
            String toId = crearId(bond.getTo(), index + 1);

            if (previous == null) {
                atomos.add(new AtomoRepresentacionDTO(fromId, bond.getFrom(), startX + index * stepX, y, null, obtenerParesLibres(bond.getFrom(), connectivity)));
            }

            atomos.add(new AtomoRepresentacionDTO(toId, bond.getTo(), startX + (index + 1) * stepX, y, null, obtenerParesLibres(bond.getTo(), connectivity)));
            enlaces.add(new EnlaceRepresentacionDTO(fromId, toId, bond.getOrder()));

            previous = bond.getTo();
            index++;
        }

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                construirTexto(atomos),
                "Polar"
        );
    }

    private int obtenerParesLibres(String simbolo, MolecularConnectivity connectivity) {
        if (simbolo.equals(connectivity.getCentral())) {
            return connectivity.getLonePairs();
        }

        if ("O".equals(simbolo)) {
            return 2;
        }

        if ("N".equals(simbolo)) {
            return 1;
        }

        return 0;
    }

    private String crearId(String simbolo, int index) {
        return simbolo + index;
    }

    private String construirTexto(List<AtomoRepresentacionDTO> atomos) {
        return atomos.stream()
                .map(AtomoRepresentacionDTO::getSimbolo)
                .reduce((a, b) -> a + "—" + b)
                .orElse("");
    }
}
