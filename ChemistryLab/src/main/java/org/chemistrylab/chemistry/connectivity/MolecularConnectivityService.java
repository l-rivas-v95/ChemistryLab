package org.chemistrylab.chemistry.connectivity;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.connectivity.rules.MolecularConnectivityContext;
import org.chemistrylab.chemistry.connectivity.rules.MolecularConnectivityRule;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MolecularConnectivityService {

    private final ElementoRepository elementoRepository;
    private final FormulaParserService formulaParserService;
    private final List<MolecularConnectivityRule> rules;

    public Optional<MolecularConnectivity> construir(String formulaVisual) {
        Map<String, Integer> atomosFormula = formulaParserService.parsearFormula(formulaVisual);

        if (atomosFormula.isEmpty()) {
            return Optional.empty();
        }

        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomosFormula);

        if (!esCandidataCovalentePequena(atomosFormula, elementos)) {
            return Optional.empty();
        }

        MolecularConnectivityContext context = new MolecularConnectivityContext(
                formulaVisual,
                atomosFormula,
                elementos
        );

        for (MolecularConnectivityRule rule : rules) {
            Optional<MolecularConnectivity> connectivity = rule.tryBuild(context);

            if (connectivity.isPresent()) {
                return connectivity;
            }
        }

        String central = elegirAtomoCentral(atomosFormula, elementos);

        if (central == null) {
            return Optional.empty();
        }

        List<String> terminales = obtenerTerminales(atomosFormula, central);

        if (terminales.isEmpty() || terminales.size() > 4) {
            return Optional.empty();
        }

        List<MolecularBond> bonds = construirEnlaces(central, terminales, atomosFormula);
        int paresLibres = estimarParesLibres(elementos.get(central), bonds);

        return Optional.of(MolecularConnectivity.builder()
                .central(central)
                .bonds(bonds)
                .lonePairs(paresLibres)
                .build());
    }

    private List<MolecularBond> construirEnlaces(
            String central,
            List<String> terminales,
            Map<String, Integer> atomosFormula
    ) {
        List<MolecularBond> bonds = new ArrayList<>();

        for (String terminal : terminales) {
            bonds.add(MolecularBond.builder()
                    .from(central)
                    .to(terminal)
                    .order(estimarOrdenEnlace(central, terminal, atomosFormula))
                    .build());
        }

        return bonds;
    }

    private int estimarOrdenEnlace(String central, String terminal, Map<String, Integer> atomosFormula) {
        if (esOxidoCovalenteBinario(central, terminal, atomosFormula)) {
            return 2;
        }

        return 1;
    }

    private boolean esOxidoCovalenteBinario(String central, String terminal, Map<String, Integer> atomosFormula) {
        return atomosFormula.size() == 2
                && atomosFormula.containsKey("O")
                && !"O".equals(central)
                && "O".equals(terminal);
    }

    private int estimarParesLibres(ElementoEntity central, List<MolecularBond> bonds) {
        Integer grupo = getGrupo(central);

        if (grupo == null) {
            return 0;
        }

        int electronesValencia = obtenerElectronesValenciaPorGrupo(grupo);

        if (electronesValencia <= 0) {
            return 0;
        }

        int enlacesEfectivos = bonds.stream()
                .mapToInt(MolecularBond::getOrder)
                .sum();

        int electronesRestantes = electronesValencia - enlacesEfectivos;

        if (electronesRestantes <= 0) {
            return 0;
        }

        return electronesRestantes / 2;
    }

    private String elegirAtomoCentral(
            Map<String, Integer> atomosFormula,
            Map<String, ElementoEntity> elementos
    ) {
        List<String> candidatos = atomosFormula.keySet().stream()
                .filter(simbolo -> !"H".equals(simbolo))
                .filter(elementos::containsKey)
                .filter(simbolo -> esNoMetal(elementos.get(simbolo)))
                .toList();

        if (candidatos.isEmpty()) {
            return null;
        }

        if (candidatos.size() == 1) {
            return candidatos.get(0);
        }

        List<String> candidatosSinOxigeno = candidatos.stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .toList();

        if (candidatosSinOxigeno.size() == 1) {
            return candidatosSinOxigeno.get(0);
        }

        if (candidatosSinOxigeno.contains("C")) return "C";
        if (candidatosSinOxigeno.contains("N")) return "N";
        if (candidatosSinOxigeno.contains("S")) return "S";
        if (candidatosSinOxigeno.contains("P")) return "P";
        if (candidatosSinOxigeno.contains("B")) return "B";

        List<String> candidatosFinales = candidatosSinOxigeno.isEmpty() ? candidatos : candidatosSinOxigeno;

        return candidatosFinales.stream()
                .min((a, b) -> Double.compare(
                        getElectronegatividad(elementos.get(a)).doubleValue(),
                        getElectronegatividad(elementos.get(b)).doubleValue()
                ))
                .orElse(null);
    }

    private List<String> obtenerTerminales(Map<String, Integer> atomosFormula, String central) {
        List<String> terminales = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : atomosFormula.entrySet()) {
            String simbolo = entry.getKey();

            if (simbolo.equals(central)) {
                continue;
            }

            for (int i = 0; i < entry.getValue(); i++) {
                terminales.add(simbolo);
            }
        }

        return terminales;
    }

    private Map<String, ElementoEntity> cargarElementosPorSimbolo(Map<String, Integer> atomosFormula) {
        List<ElementoEntity> elementos = elementoRepository.findBySimboloIn(atomosFormula.keySet());

        Map<String, ElementoEntity> mapa = new HashMap<>();

        for (ElementoEntity elemento : elementos) {
            mapa.put(elemento.getSimbolo(), elemento);
        }

        return mapa;
    }

    private boolean esCandidataCovalentePequena(
            Map<String, Integer> atomosFormula,
            Map<String, ElementoEntity> elementos
    ) {
        if (atomosFormula.size() < 2 || atomosFormula.size() > 3) {
            return false;
        }

        int totalAtomos = atomosFormula.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalAtomos > 6) {
            return false;
        }

        for (String simbolo : atomosFormula.keySet()) {
            ElementoEntity elemento = elementos.get(simbolo);

            if (elemento == null) {
                return false;
            }

            if (!esNoMetal(elemento)) {
                return false;
            }
        }

        return true;
    }

    private boolean esNoMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(elemento.getCategoria());

        return categoria.contains("nonmetal")
                || categoria.contains("halogen")
                || categoria.contains("noble gas")
                || categoria.contains("chalcogen")
                || categoria.contains("pnictogen")
                || categoria.contains("no metal")
                || categoria.contains("metaloide")
                || categoria.contains("metalloid");
    }

    private int obtenerElectronesValenciaPorGrupo(int grupo) {
        if (grupo >= 1 && grupo <= 2) {
            return grupo;
        }

        if (grupo >= 13 && grupo <= 18) {
            return grupo - 10;
        }

        return 0;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private Integer getGrupo(ElementoEntity elemento) {
        return elemento.getGrupoPeriodico();
    }

    private Number getElectronegatividad(ElementoEntity elemento) {
        return elemento.getElectronegatividad() != null
                ? elemento.getElectronegatividad()
                : 99.0;
    }
}
