package org.chemistrylab.chemistry.formula;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class FormulaParserService {

    private static final List<String> HALOGENS = List.of("F", "Cl", "Br", "I");
    private static final List<String> COMMON_CENTRAL_COVALENT = List.of(
            "B", "C", "Si", "N", "P", "As", "O", "S", "Se", "Cl", "Br", "I", "Xe"
    );
    private static final List<String> COMMON_METALS = List.of(
            "Li", "Na", "K", "Rb", "Cs", "Fr",
            "Be", "Mg", "Ca", "Sr", "Ba", "Ra",
            "Al", "Fe", "Cu", "Zn", "Ag", "Au", "Ti", "Mn", "Cr", "Co", "Ni", "Pb", "Sn", "Hg"
    );

    public Map<String, Integer> parsearFormula(String formula) {
        Map<String, Integer> resultado = new LinkedHashMap<>();

        if (formula == null || formula.isBlank()) {
            return resultado;
        }

        String formulaLimpia = limpiarFormula(formula);

        Stack<Map<String, Integer>> pila = new Stack<>();
        pila.push(new LinkedHashMap<>());

        int i = 0;

        while (i < formulaLimpia.length()) {
            char actual = formulaLimpia.charAt(i);

            if (actual == '(' || actual == '[') {
                pila.push(new LinkedHashMap<>());
                i++;
            } else if (actual == ')' || actual == ']') {
                i++;

                int inicioNumero = i;
                while (i < formulaLimpia.length() && Character.isDigit(formulaLimpia.charAt(i))) {
                    i++;
                }

                int multiplicador = inicioNumero == i
                        ? 1
                        : Integer.parseInt(formulaLimpia.substring(inicioNumero, i));

                Map<String, Integer> grupo = pila.pop();
                Map<String, Integer> destino = pila.peek();

                for (Map.Entry<String, Integer> entry : grupo.entrySet()) {
                    destino.put(
                            entry.getKey(),
                            destino.getOrDefault(entry.getKey(), 0) + entry.getValue() * multiplicador
                    );
                }
            } else if (Character.isUpperCase(actual)) {
                int inicio = i;
                i++;

                if (i < formulaLimpia.length() && Character.isLowerCase(formulaLimpia.charAt(i))) {
                    i++;
                }

                String simbolo = formulaLimpia.substring(inicio, i);

                int inicioNumero = i;
                while (i < formulaLimpia.length() && Character.isDigit(formulaLimpia.charAt(i))) {
                    i++;
                }

                int cantidad = inicioNumero == i
                        ? 1
                        : Integer.parseInt(formulaLimpia.substring(inicioNumero, i));

                Map<String, Integer> actualMap = pila.peek();
                actualMap.put(simbolo, actualMap.getOrDefault(simbolo, 0) + cantidad);
            } else {
                i++;
            }
        }

        if (!pila.isEmpty()) {
            resultado.putAll(pila.pop());
        }

        return resultado;
    }

    public boolean contieneComposicion(Map<String, Integer> formula, Map<String, Integer> ion, int cantidad) {
        if (formula == null || ion == null || ion.isEmpty() || cantidad <= 0) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : ion.entrySet()) {
            int disponible = formula.getOrDefault(entry.getKey(), 0);
            int necesario = entry.getValue() * cantidad;

            if (disponible < necesario) {
                return false;
            }
        }

        return true;
    }

    public Map<String, Integer> restarComposicion(
            Map<String, Integer> formula,
            Map<String, Integer> ion,
            int cantidad
    ) {
        Map<String, Integer> resultado = new LinkedHashMap<>(formula);

        for (Map.Entry<String, Integer> entry : ion.entrySet()) {
            int nuevoValor = resultado.getOrDefault(entry.getKey(), 0) - entry.getValue() * cantidad;

            if (nuevoValor <= 0) {
                resultado.remove(entry.getKey());
            } else {
                resultado.put(entry.getKey(), nuevoValor);
            }
        }

        return resultado;
    }

    public boolean estaVacia(Map<String, Integer> composicion) {
        return composicion == null || composicion.values().stream().allMatch(valor -> valor == null || valor == 0);
    }

    public boolean esComposicion(Map<String, Integer> atomos, Object... pares) {
        if (atomos == null || pares == null || pares.length % 2 != 0) {
            return false;
        }

        Map<String, Integer> esperada = new HashMap<>();

        for (int i = 0; i < pares.length; i += 2) {
            String simbolo = (String) pares[i];
            Integer cantidad = (Integer) pares[i + 1];
            esperada.put(simbolo, cantidad);
        }

        return atomos.equals(esperada);
    }

    public String normalizarFormulaVisual(String formula) {
        String formulaLimpia = limpiarFormula(formula);
        Map<String, Integer> atomos = parsearFormula(formulaLimpia);

        if (atomos.isEmpty()) {
            return formulaLimpia;
        }

        String formulaConGrupos = intentarFormatearGruposInorganicos(atomos);
        if (!formulaConGrupos.isBlank()) {
            return formulaConGrupos;
        }

        if (esCovalenteBinariaReordenable(atomos)) {
            String central = obtenerCentralCovalente(atomos);
            String terminal = atomos.keySet().stream()
                    .filter(simbolo -> !simbolo.equals(central))
                    .findFirst()
                    .orElse(null);

            if (central != null && terminal != null) {
                return formatearElemento(central, atomos.get(central))
                        + formatearElemento(terminal, atomos.get(terminal));
            }
        }

        if (esFormulaOrganica(atomos)) {
            return formatearOrganicaHill(atomos);
        }

        return formulaLimpia;
    }

    public String limpiarFormula(String formula) {
        if (formula == null) {
            return "";
        }

        return formula
                .replaceAll("[+-]\\d*$", "")
                .replaceAll("\\d*[+-]$", "")
                .replaceAll("\\s", "");
    }

    private String intentarFormatearGruposInorganicos(Map<String, Integer> atomos) {
        String cation = primerMetal(atomos);
        if (cation == null) {
            return "";
        }

        int cationes = atomos.getOrDefault(cation, 0);
        Map<String, Integer> resto = new LinkedHashMap<>(atomos);
        resto.remove(cation);

        if (resto.equals(Map.of("O", cationes, "H", cationes)) || resto.equals(Map.of("H", cationes, "O", cationes))) {
            return formatearElemento(cation, cationes) + formatearGrupo("OH", cationes);
        }

        if (resto.containsKey("C") && resto.containsKey("N") && resto.size() == 2
                && resto.get("C").equals(resto.get("N"))) {
            return formatearElemento(cation, cationes) + formatearGrupo("CN", resto.get("C"));
        }

        String oxoanion = formatearOxoanion(resto);
        if (!oxoanion.isBlank()) {
            return formatearElemento(cation, cationes) + oxoanion;
        }

        return "";
    }

    private String formatearOxoanion(Map<String, Integer> resto) {
        if (!resto.containsKey("O")) {
            return "";
        }

        String central = resto.keySet().stream()
                .filter(simbolo -> !"H".equals(simbolo) && !"O".equals(simbolo))
                .findFirst()
                .orElse(null);

        if (central == null) {
            return "";
        }

        int centralCantidad = resto.getOrDefault(central, 0);
        int oxigenos = resto.getOrDefault("O", 0);
        int hidrogenos = resto.getOrDefault("H", 0);

        if (centralCantidad <= 0 || oxigenos <= 0) {
            return "";
        }

        String grupo = formatearElemento("H", hidrogenos)
                + formatearElemento(central, centralCantidad)
                + formatearElemento("O", oxigenos);

        int divisor = maximoComunDivisor(centralCantidad, oxigenos, hidrogenos == 0 ? centralCantidad : hidrogenos);
        if (divisor > 1 && centralCantidad % divisor == 0 && oxigenos % divisor == 0 && hidrogenos % divisor == 0) {
            String grupoReducido = formatearElemento("H", hidrogenos / divisor)
                    + formatearElemento(central, centralCantidad / divisor)
                    + formatearElemento("O", oxigenos / divisor);
            return formatearGrupo(grupoReducido, divisor);
        }

        return formatearGrupo(grupo, 1);
    }

    private boolean esCovalenteBinariaReordenable(Map<String, Integer> atomos) {
        if (atomos.size() != 2) {
            return false;
        }

        String central = obtenerCentralCovalente(atomos);
        if (central == null) {
            return false;
        }

        String terminal = atomos.keySet().stream()
                .filter(simbolo -> !simbolo.equals(central))
                .findFirst()
                .orElse(null);

        return terminal != null && (HALOGENS.contains(terminal) || "O".equals(terminal) || "S".equals(terminal));
    }

    private String obtenerCentralCovalente(Map<String, Integer> atomos) {
        return COMMON_CENTRAL_COVALENT.stream()
                .filter(atomos::containsKey)
                .findFirst()
                .orElse(null);
    }

    private boolean esFormulaOrganica(Map<String, Integer> atomos) {
        return atomos.containsKey("C") && atomos.containsKey("H");
    }

    private String formatearOrganicaHill(Map<String, Integer> atomos) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatearElemento("C", atomos.get("C")));
        builder.append(formatearElemento("H", atomos.get("H")));

        atomos.entrySet().stream()
                .filter(entry -> !"C".equals(entry.getKey()) && !"H".equals(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.append(formatearElemento(entry.getKey(), entry.getValue())));

        return builder.toString();
    }

    private String primerMetal(Map<String, Integer> atomos) {
        return COMMON_METALS.stream()
                .filter(atomos::containsKey)
                .findFirst()
                .orElse(null);
    }

    private String formatearGrupo(String grupo, int cantidad) {
        if (cantidad <= 1) {
            return grupo;
        }
        return "(" + grupo + ")" + cantidad;
    }

    private int maximoComunDivisor(int... valores) {
        int resultado = 0;
        for (int valor : valores) {
            if (valor <= 0) {
                continue;
            }
            resultado = resultado == 0 ? valor : gcd(resultado, valor);
        }
        return resultado == 0 ? 1 : resultado;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temporal = b;
            b = a % b;
            a = temporal;
        }
        return Math.abs(a);
    }

    private String formatearElemento(String simbolo, Integer cantidad) {
        if (cantidad == null || cantidad <= 1) {
            return simbolo;
        }

        return simbolo + cantidad;
    }
}
