package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.chemistry.catalog.IonCatalogService;
import org.chemistrylab.chemistry.config.IonConfig;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.chemistry.ionic.IonMatch;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolution;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolver;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MoleculaRepresentacionIonicaService {

    private final ElementoRepository elementoRepository;
    private final IonCatalogService ionCatalogService;
    private final IonicFormulaResolver ionicFormulaResolver;
    private final FormulaParserService formulaParserService;

    public boolean esRepresentacionIonicaPreferente(String tipo, String formulaVisual) {
        if (tipo.contains("sal") || tipo.contains("acido") || tipo.contains("hidroxido") || tipo.contains("base")) {
            return true;
        }

        if (tipo.contains("oxido")) {
            return esOxidoIonico(formulaVisual);
        }

        return false;
    }

    public boolean esRepresentacionIonica(String tipo) {
        return tipo.contains("sal")
                || tipo.contains("acido")
                || tipo.contains("base")
                || tipo.contains("hidroxido")
                || tipo.contains("oxido");
    }

    public String construirTextoIonico(String formulaVisual, String tipo) {
        if (tipo.contains("hidroxido") || tipo.contains("base")) {
            return construirTextoHidroxido(formulaVisual);
        }

        if (tipo.contains("acido")) {
            return construirTextoAcido(formulaVisual);
        }

        if (tipo.contains("oxido")) {
            return construirTextoOxido(formulaVisual);
        }

        if (tipo.contains("sal")) {
            return construirTextoSal(formulaVisual);
        }

        return ionicFormulaResolver.resolver(formulaVisual)
                .map(this::formatearResolucionIonica)
                .orElse("representación iónica");
    }

    private boolean esOxidoIonico(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        if (!atomos.containsKey("O") || atomos.size() < 2) {
            return false;
        }

        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomos);

        return atomos.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .map(elementos::get)
                .anyMatch(this::esMetal);
    }

    private String construirTextoAcido(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        if (formulaParserService.esComposicion(atomos, "H", 2, "C", 1, "O", 3)) {
            return "2H⁺ + CO3²⁻";
        }

        if (formulaParserService.esComposicion(atomos, "H", 3, "B", 1, "O", 3)) {
            return "B(OH)3";
        }

        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("^H(\\d?)(.+)$")
                .matcher(formulaVisual);

        if (!matcher.matches()) {
            return ionicFormulaResolver.resolver(formulaVisual)
                    .map(this::formatearResolucionIonica)
                    .orElse("H⁺ + anión");
        }

        String cantidadHTexto = matcher.group(1);
        String anionFormula = matcher.group(2);

        int cantidadH = cantidadHTexto == null || cantidadHTexto.isBlank()
                ? 1
                : Integer.parseInt(cantidadHTexto);

        int cargaAnionEsperada = -cantidadH;
        String protones = cantidadH == 1 ? "H⁺" : cantidadH + "H⁺";

        Optional<IonMatch> anion = ionicFormulaResolver.resolverAnionRestante(
                anionFormula,
                cargaAnionEsperada
        );

        if (anion.isPresent()) {
            return protones + " + " + ionCatalogService.formatearIon(anion.get().getIon());
        }

        return protones + " + " + anionFormula + ionCatalogService.formatearCarga(cargaAnionEsperada);
    }

    private String construirTextoHidroxido(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        if (formulaParserService.esComposicion(atomos, "Al", 1, "O", 3, "H", 3)) {
            return "Al³⁺ + 3OH⁻";
        }

        if (formulaParserService.esComposicion(atomos, "N", 1, "H", 5, "O", 1)) {
            return "NH4⁺ + OH⁻";
        }

        return ionicFormulaResolver.resolver(formulaVisual)
                .map(this::formatearResolucionIonica)
                .orElseGet(() -> construirTextoHidroxidoBasico(formulaVisual));
    }

    private String construirTextoHidroxidoBasico(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        Integer oxigenos = atomos.get("O");
        Integer hidrogenos = atomos.get("H");

        if (oxigenos == null || hidrogenos == null) {
            return "catión + OH⁻";
        }

        int cantidadOH = Math.min(oxigenos, hidrogenos);

        Optional<IonConfig> hidroxido = ionCatalogService.buscarAnionPorFormulaYCarga("OH", -1);

        String textoOH = hidroxido
                .map(ion -> ionCatalogService.formatearCantidadIon(cantidadOH, ion))
                .orElse(cantidadOH <= 1 ? "OH⁻" : cantidadOH + "OH⁻");

        String cationFormula = atomos.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .filter(simbolo -> !"H".equals(simbolo))
                .findFirst()
                .orElse(null);

        if (cationFormula == null) {
            return "catión + " + textoOH;
        }

        Optional<IonConfig> cation = ionCatalogService.buscarCationPorFormulaYCarga(cationFormula, cantidadOH);

        String cationTexto = cation
                .map(ionCatalogService::formatearIon)
                .orElse(cationFormula + ionCatalogService.formatearCarga(cantidadOH));

        return cationTexto + " + " + textoOH;
    }

    private String construirTextoOxido(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        if (formulaParserService.esComposicion(atomos, "Ti", 1, "O", 2)) {
            return "Ti⁴⁺ + 2O²⁻";
        }

        return ionicFormulaResolver.resolver(formulaVisual)
                .map(this::formatearResolucionIonica)
                .orElseGet(() -> construirTextoOxidoBasico(formulaVisual));
    }

    private String construirTextoOxidoBasico(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        Integer oxigenos = atomos.get("O");

        if (oxigenos == null || oxigenos == 0 || atomos.size() < 2) {
            return "elemento + O²⁻";
        }

        String elementoFormula = atomos.keySet().stream()
                .filter(simbolo -> !"O".equals(simbolo))
                .findFirst()
                .orElse(null);

        if (elementoFormula == null) {
            return "elemento + O²⁻";
        }

        int cantidadElemento = atomos.getOrDefault(elementoFormula, 1);
        int cargaTotalOxigeno = oxigenos * -2;

        if (cantidadElemento == 0 || (-cargaTotalOxigeno) % cantidadElemento != 0) {
            return "elemento + O²⁻";
        }

        int cargaCationEsperada = (-cargaTotalOxigeno) / cantidadElemento;

        Optional<IonConfig> cation = ionCatalogService.buscarCationPorFormulaYCarga(
                elementoFormula,
                cargaCationEsperada
        );

        Optional<IonConfig> oxido = ionCatalogService.buscarAnionPorFormulaYCarga("O", -2);

        String cationTexto = cation
                .map(ion -> ionCatalogService.formatearCantidadIon(cantidadElemento, ion))
                .orElse(formatearCantidad(cantidadElemento, elementoFormula + ionCatalogService.formatearCarga(cargaCationEsperada)));

        String oxidoTexto = oxido
                .map(ion -> ionCatalogService.formatearCantidadIon(oxigenos, ion))
                .orElse(formatearCantidad(oxigenos, "O²⁻"));

        return cationTexto + " + " + oxidoTexto;
    }

    private String construirTextoSal(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        Optional<String> fosfato = construirTextoFosfato(formulaVisual, atomos);
        if (fosfato.isPresent()) {
            return fosfato.get();
        }

        return ionicFormulaResolver.resolver(formulaVisual)
                .map(this::formatearResolucionIonica)
                .orElse("catión + anión");
    }

    private Optional<String> construirTextoFosfato(String formulaVisual, Map<String, Integer> atomos) {
        GrupoFosfato grupo = detectarGrupoFosfato(formulaVisual, atomos);
        if (grupo == null) {
            return Optional.empty();
        }

        Optional<CationCalculado> cation = detectarCationFosfato(formulaVisual, atomos, grupo.cargaPositivaNecesaria());
        if (cation.isEmpty()) {
            return Optional.empty();
        }

        String cationTexto = formatearCantidad(cation.get().cantidad(),
                cation.get().simbolo() + ionCatalogService.formatearCarga(cation.get().carga()));

        String anionTexto = formatearCantidad(grupo.cantidad(),
                grupo.formula() + ionCatalogService.formatearCarga(grupo.carga()));

        return Optional.of(cationTexto + " + " + anionTexto);
    }

    private GrupoFosfato detectarGrupoFosfato(String formulaVisual, Map<String, Integer> atomos) {
        String formula = normalizarFormulaQuimica(formulaVisual);

        Matcher fosfatoConParentesis = Pattern.compile("\\(PO4\\)(\\d*)").matcher(formula);
        if (fosfatoConParentesis.find()) {
            int cantidad = parsearCantidadOpcional(fosfatoConParentesis.group(1));
            return new GrupoFosfato("PO4", cantidad, -3);
        }

        if (formula.contains("PO4")) {
            int hAcidos = 0;
            if (formula.contains("H2PO4")) {
                hAcidos = 2;
            } else if (formula.contains("HPO4")) {
                hAcidos = 1;
            }

            int cantidad = Math.max(1, atomos.getOrDefault("P", 1));
            if (hAcidos == 2) {
                return new GrupoFosfato("H2PO4", cantidad, -1);
            }
            if (hAcidos == 1) {
                return new GrupoFosfato("HPO4", cantidad, -2);
            }
            return new GrupoFosfato("PO4", cantidad, -3);
        }

        return null;
    }

    private Optional<CationCalculado> detectarCationFosfato(
            String formulaVisual,
            Map<String, Integer> atomos,
            int cargaPositivaNecesaria
    ) {
        String formula = normalizarFormulaQuimica(formulaVisual);

        Matcher amonio = Pattern.compile("^\\(NH4\\)(\\d*)").matcher(formula);
        if (amonio.find()) {
            int cantidadAmonio = parsearCantidadOpcional(amonio.group(1));
            if (cantidadAmonio == cargaPositivaNecesaria) {
                return Optional.of(new CationCalculado("NH4", cantidadAmonio, 1));
            }
        }

        Matcher cationSimple = Pattern.compile("^([A-Z][a-z]?)(\\d*)").matcher(formula);
        if (cationSimple.find()) {
            String simbolo = cationSimple.group(1);
            if (!"H".equals(simbolo) && !"P".equals(simbolo) && !"O".equals(simbolo)) {
                int cantidad = parsearCantidadOpcional(cationSimple.group(2));
                if (cantidad > 0 && cargaPositivaNecesaria % cantidad == 0) {
                    return Optional.of(new CationCalculado(simbolo, cantidad, cargaPositivaNecesaria / cantidad));
                }
            }
        }

        return Optional.empty();
    }

    private int parsearCantidadOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return 1;
        }

        return Integer.parseInt(valor);
    }

    private String normalizarFormulaQuimica(String formula) {
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
                .trim();
    }

    private String formatearResolucionIonica(IonicFormulaResolution resolucion) {
        String cationTexto = ionCatalogService.formatearCantidadIon(
                resolucion.getCation().getCantidad(),
                resolucion.getCation().getIon()
        );

        String anionTexto = ionCatalogService.formatearCantidadIon(
                resolucion.getAnion().getCantidad(),
                resolucion.getAnion().getIon()
        );

        return cationTexto + " + " + anionTexto;
    }

    private Map<String, Integer> parsearFormula(String formula) {
        return formulaParserService.parsearFormula(formula);
    }

    private String formatearCantidad(int cantidad, String texto) {
        if (cantidad <= 1) {
            return texto;
        }

        return cantidad + texto;
    }

    private Map<String, ElementoEntity> cargarElementosPorSimbolo(Map<String, Integer> atomosFormula) {
        List<ElementoEntity> elementos = elementoRepository.findBySimboloIn(atomosFormula.keySet());

        Map<String, ElementoEntity> mapa = new HashMap<>();

        for (ElementoEntity elemento : elementos) {
            mapa.put(elemento.getSimbolo(), elemento);
        }

        return mapa;
    }

    private boolean esMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(elemento.getCategoria());

        return categoria.contains("metal")
                && !categoria.contains("nonmetal")
                && !categoria.contains("no metal")
                && !categoria.contains("metalloid")
                && !categoria.contains("metaloide");
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

    private record CationCalculado(String simbolo, int cantidad, int carga) {
    }

    private record GrupoFosfato(String formula, int cantidad, int carga) {
        private int cargaPositivaNecesaria() {
            return -(cantidad * carga);
        }
    }
}
