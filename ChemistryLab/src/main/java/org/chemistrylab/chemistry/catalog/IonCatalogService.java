package org.chemistrylab.chemistry.catalog;


import org.chemistrylab.chemistry.config.IonConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class IonCatalogService {

    private final ObjectMapper objectMapper;

    private List<IonConfig> iones = new ArrayList<>();

    public IonCatalogService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void cargarCatalogo() {
        try {
            ClassPathResource resource = new ClassPathResource("chemistry/ions.json");

            try (InputStream inputStream = resource.getInputStream()) {
                iones = objectMapper.readValue(inputStream, new TypeReference<List<IonConfig>>() {});
            }

            iones = iones.stream()
                    .filter(ion -> tieneTexto(ion.getFormula()))
                    .sorted(Comparator.comparingInt((IonConfig ion) -> ion.getFormula().length()).reversed())
                    .toList();

            System.out.println("Catálogo de iones cargado: " + iones.size());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar chemistry/ions.json", e);
        }
    }

    public List<IonConfig> obtenerTodos() {
        return iones;
    }

    public List<IonConfig> obtenerCationes() {
        return iones.stream()
                .filter(IonConfig::esCation)
                .toList();
    }

    public List<IonConfig> obtenerAniones() {
        return iones.stream()
                .filter(IonConfig::esAnion)
                .toList();
    }

    public Optional<IonConfig> buscarPorFormula(String formula) {
        String normalizada = normalizarFormula(formula);

        return iones.stream()
                .filter(ion -> normalizarFormula(ion.getFormula()).equals(normalizada))
                .findFirst();
    }

    public Optional<IonConfig> buscarAnionPorFormula(String formula) {
        String normalizada = normalizarFormula(formula);

        return iones.stream()
                .filter(IonConfig::esAnion)
                .filter(ion -> normalizarFormula(ion.getFormula()).equals(normalizada))
                .findFirst();
    }

    public Optional<IonConfig> buscarCationPorFormula(String formula) {
        String normalizada = normalizarFormula(formula);

        return iones.stream()
                .filter(IonConfig::esCation)
                .filter(ion -> normalizarFormula(ion.getFormula()).equals(normalizada))
                .findFirst();
    }

    public Optional<IonConfig> buscarAnionContenidoEnFormula(String formula) {
        String normalizada = normalizarFormula(formula);

        return obtenerAniones().stream()
                .filter(ion -> normalizada.contains(normalizarFormula(ion.getFormula())))
                .findFirst();
    }

    public Optional<IonConfig> buscarCationContenidoEnFormula(String formula) {
        String normalizada = normalizarFormula(formula);

        return obtenerCationes().stream()
                .filter(ion -> normalizada.contains(normalizarFormula(ion.getFormula())))
                .findFirst();
    }

    public String formatearIon(IonConfig ion) {
        if (ion == null || !tieneTexto(ion.getFormula()) || ion.getCarga() == null) {
            return "";
        }

        return ion.getFormula() + formatearCarga(ion.getCarga());
    }

    public String formatearCarga(Integer carga) {
        if (carga == null || carga == 0) {
            return "";
        }

        String signo = carga > 0 ? "⁺" : "⁻";
        int valorAbsoluto = Math.abs(carga);

        if (valorAbsoluto == 1) {
            return signo;
        }

        return toSuperscript(valorAbsoluto) + signo;
    }

    public String formatearCantidadIon(int cantidad, IonConfig ion) {
        if (cantidad <= 1) {
            return formatearIon(ion);
        }

        return cantidad + formatearIon(ion);
    }

    private String normalizarFormula(String formula) {
        if (formula == null) {
            return "";
        }

        return formula
                .replace("(", "")
                .replace(")", "")
                .replaceAll("\\s", "")
                .trim();
    }

    private String toSuperscript(int numero) {
        return String.valueOf(numero)
                .replace("0", "⁰")
                .replace("1", "¹")
                .replace("2", "²")
                .replace("3", "³")
                .replace("4", "⁴")
                .replace("5", "⁵")
                .replace("6", "⁶")
                .replace("7", "⁷")
                .replace("8", "⁸")
                .replace("9", "⁹");
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    public Optional<IonConfig> buscarCationPorFormulaYCarga(String formula, int carga) {
        String normalizada = normalizarFormula(formula);

        return iones.stream()
                .filter(IonConfig::esCation)
                .filter(ion -> normalizarFormula(ion.getFormula()).equals(normalizada))
                .filter(ion -> ion.getCarga() != null && ion.getCarga() == carga)
                .findFirst();
    }

    public Optional<IonConfig> buscarAnionPorFormulaYCarga(String formula, int carga) {
        String normalizada = normalizarFormula(formula);

        return iones.stream()
                .filter(IonConfig::esAnion)
                .filter(ion -> normalizarFormula(ion.getFormula()).equals(normalizada))
                .filter(ion -> ion.getCarga() != null && ion.getCarga() == carga)
                .findFirst();
    }
}