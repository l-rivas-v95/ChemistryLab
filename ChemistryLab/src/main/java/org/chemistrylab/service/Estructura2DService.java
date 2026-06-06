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
                .filter(this::esCadenaRepresentable)
                .map(connectivity -> construirEstructura(formulaVisual, connectivity));
    }

    private boolean esCadenaRepresentable(MolecularConnectivity connectivity) {
        return connectivity.getBonds() != null
                && connectivity.getBonds().size() >= 2;
    }

    private MoleculaRepresentacionDTO construirEstructura(
            String formulaVisual,
            MolecularConnectivity connectivity
    ) {
        if (esPeroxidoDeHidrogeno(formulaVisual, connectivity)) {
            return construirPeroxidoDeHidrogeno(formulaVisual);
        }

        return construirCadenaSimple(formulaVisual, connectivity);
    }

    private MoleculaRepresentacionDTO construirPeroxidoDeHidrogeno(String formulaVisual) {
        List<AtomoRepresentacionDTO> atomos = List.of(
                new AtomoRepresentacionDTO("H0", "H", 72, 58, null, 0),
                new AtomoRepresentacionDTO("O1", "O", 112, 78, null, 2),
                new AtomoRepresentacionDTO("O2", "O", 148, 102, null, 2),
                new AtomoRepresentacionDTO("H3", "H", 188, 82, null, 0)
        );

        List<EnlaceRepresentacionDTO> enlaces = List.of(
                new EnlaceRepresentacionDTO("H0", "O1", 1),
                new EnlaceRepresentacionDTO("O1", "O2", 1),
                new EnlaceRepresentacionDTO("O2", "H3", 1)
        );

        return MoleculaRepresentacionDTO.estructura2d(
                formulaVisual,
                atomos,
                enlaces,
                null,
                "Polar"
        );
    }

    private boolean esPeroxidoDeHidrogeno(String formulaVisual, MolecularConnectivity connectivity) {
        if ("H2O2".equals(normalizarFormula(formulaVisual))) {
            return true;
        }

        long enlacesOxigenoOxigeno = connectivity.getBonds().stream()
                .filter(bond -> "O".equals(bond.getFrom()) && "O".equals(bond.getTo()))
                .count();

        long enlacesHidrogenoOxigeno = connectivity.getBonds().stream()
                .filter(bond -> ("H".equals(bond.getFrom()) && "O".equals(bond.getTo()))
                        || ("O".equals(bond.getFrom()) && "H".equals(bond.getTo())))
                .count();

        return enlacesOxigenoOxigeno == 1 && enlacesHidrogenoOxigeno == 2;
    }

    private MoleculaRepresentacionDTO construirCadenaSimple(
            String formulaVisual,
            MolecularConnectivity connectivity
    ) {
        List<MolecularBond> bonds = connectivity.getBonds();
        List<String> simbolosOrdenados = construirSimbolosOrdenados(bonds);
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

        for (int i = 0; i < bonds.size(); i++) {
            MolecularBond bond = bonds.get(i);

            enlaces.add(new EnlaceRepresentacionDTO(
                    idsPorIndice.get(i),
                    idsPorIndice.get(i + 1),
                    bond.getOrder()
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

    private String normalizarFormula(String formula) {
        if (formula == null) {
            return "";
        }

        return formula
                .replace("₀", "0")
                .replace("₁", "1")
                .replace("₂", "2")
                .replace("₃", "3")
                .replace("₄", "4")
                .replace("₅", "5")
                .replace("₆", "6")
                .replace("₇", "7")
                .replace("₈", "8")
                .replace("₉", "9")
                .replace(" ", "")
                .trim()
                .toUpperCase();
    }

    private String crearId(String simbolo, int index) {
        return simbolo + index;
    }
}
