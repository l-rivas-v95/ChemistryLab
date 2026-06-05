package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.connectivity.MolecularBond;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivity;
import org.chemistrylab.chemistry.connectivity.MolecularConnectivityService;
import org.chemistrylab.dto.EnlaceRepresentacionDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoleculaRepresentacionVseprService {

    private final MolecularConnectivityService molecularConnectivityService;

    public MoleculaRepresentacionDTO intentarConstruir(String formulaVisual) {
        return molecularConnectivityService.construir(formulaVisual)
                .map(connectivity -> construirRepresentacion(formulaVisual, connectivity))
                .orElse(null);
    }

    private MoleculaRepresentacionDTO construirRepresentacion(
            String formulaVisual,
            MolecularConnectivity connectivity
    ) {
        List<String> terminales = connectivity.getBonds().stream()
                .map(MolecularBond::getTo)
                .toList();

        List<EnlaceRepresentacionDTO> enlaces = connectivity.getBonds().stream()
                .map(bond -> new EnlaceRepresentacionDTO(
                        bond.getFrom(),
                        bond.getTo(),
                        bond.getOrder()
                ))
                .toList();

        String central = connectivity.getCentral();
        int paresLibres = connectivity.getLonePairs();

        if (esDioxidoDeNitrogeno(formulaVisual, connectivity, terminales)) {
            central = "N";
            terminales = List.of("O", "O");
            enlaces = List.of(
                    new EnlaceRepresentacionDTO("N", "O", 1),
                    new EnlaceRepresentacionDTO("N", "O", 1)
            );
            paresLibres = 1;
        }

        String vsepr = construirCodigoVsepr(terminales.size(), paresLibres);

        String geometria = obtenerGeometria(vsepr);
        if (geometria == null) {
            return null;
        }

        String polaridad = estimarPolaridad(terminales, paresLibres, vsepr);

        return MoleculaRepresentacionDTO.vsepr(
                formulaVisual,
                central,
                terminales,
                enlaces,
                paresLibres,
                vsepr,
                geometria,
                polaridad
        );
    }

    private boolean esDioxidoDeNitrogeno(
            String formulaVisual,
            MolecularConnectivity connectivity,
            List<String> terminales
    ) {
        if ("NO2".equals(normalizarFormula(formulaVisual))) {
            return true;
        }

        return "N".equals(connectivity.getCentral())
                && terminales.size() == 2
                && terminales.stream().allMatch("O"::equals);
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

    private String construirCodigoVsepr(int enlaces, int paresLibres) {
        if (paresLibres <= 0) {
            return "AX" + enlaces;
        }

        if (paresLibres == 1) {
            return "AX" + enlaces + "E";
        }

        return "AX" + enlaces + "E" + paresLibres;
    }

    private String obtenerGeometria(String vsepr) {
        return switch (vsepr) {
            case "AX1", "AX2" -> "Lineal";
            case "AX2E", "AX2E2" -> "Angular";
            case "AX3" -> "Trigonal plana";
            case "AX3E" -> "Piramidal trigonal";
            case "AX4" -> "Tetraédrica";
            default -> null;
        };
    }

    private String estimarPolaridad(List<String> terminales, int paresLibres, String vsepr) {
        boolean terminalesIguales = terminales.stream().distinct().count() == 1;

        if (paresLibres > 0) {
            return "Polar";
        }

        if (terminalesIguales && ("AX2".equals(vsepr) || "AX3".equals(vsepr) || "AX4".equals(vsepr))) {
            return "No polar";
        }

        return "Polar";
    }
}
