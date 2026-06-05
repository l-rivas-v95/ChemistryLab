package org.chemistrylab.chemistry.formula;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

@Service
public class FormulaParserService {

    public Map<String, Integer> parsearFormula(String formula) {
        Map<String, Integer> resultado = new LinkedHashMap<>();

        if (formula == null || formula.isBlank()) {
            return resultado;
        }

        String formulaLimpia = limpiarFormula(formula);

        Stack<Map<String, Integer>> pila = new Stack<>();
        Stack<Integer> multiplicadoresPendientes = new Stack<>();

        pila.push(new LinkedHashMap<>());

        int i = 0;

        while (i < formulaLimpia.length()) {
            char actual = formulaLimpia.charAt(i);

            if (actual == '(') {
                pila.push(new LinkedHashMap<>());
                i++;
            } else if (actual == ')') {
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

    public String limpiarFormula(String formula) {
        if (formula == null) {
            return "";
        }

        return formula
                .replaceAll("[+-]\\d*$", "")
                .replaceAll("\\d*[+-]$", "")
                .replaceAll("\\s", "");
    }
}