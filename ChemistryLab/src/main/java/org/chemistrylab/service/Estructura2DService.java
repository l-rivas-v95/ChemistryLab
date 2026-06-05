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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        List<String> simbolosOrdenados = construirSimbolosOrdenados(connectivity.getBonds());
        List<AtomoRepresentacionDTO> atomos = new ArrayList<>();
        List<EnlaceRepresentacionDTO> enlaces = new ArrayList<>();
        Map<Integer, String> idsPorIndice = new LinkedHashMap<>();

        int stepX = 48;
        int startX = 130 - ((simbolosOrdenados.size() - 1) * stepX) / 2;
        int y = 78;

        for (int i = 0; i < simbolosOrdenados.size(); i++) {
            String simbolo = simbolosOrdenados.get(i);
            String id = crearId(simbolo, i);
            idsPorIndice.put(i, id);

            atomos.add(new AtomoRepresentacionDTO(
                    id,
                    simbolo,
                    startX + i * stepX,
                    y,
                    null,
                    obtenerParesLibres(simbolo, connectivity)
            ));
        }

        for (int i = 0; i < simbolosOrdenados.size() - 1; i++) {
            enlaces.add(new EnlaceRepresentacionDTO(
                    idsPorIndice.get(i),
                    idsPorIndice.get(i + 1),
                    1
            ));
        }

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                null,
                "Polar"
        );
    }

    private List<String> construirSimbolosOrdenados(List<MolecularBond> bonds) {
        List<String> simbolos = new ArrayList<>();

        if (bonds.isEmpty()) {
            return simbolos;
        }

        simbolos.add(bonds.get(0).getFrom());

        for (MolecularBond bond : bonds) {
            simbolos.add(bond.getTo());
        }

        return simbolos;
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
}
