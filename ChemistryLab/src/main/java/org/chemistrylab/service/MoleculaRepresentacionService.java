package org.chemistrylab.service;

import org.chemistrylab.chemistry.catalog.IonCatalogService;
import org.chemistrylab.chemistry.config.IonConfig;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.chemistrylab.chemistry.ionic.IonMatch;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolution;
import org.chemistrylab.chemistry.ionic.IonicFormulaResolver;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.repository.ElementoRepository;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class MoleculaRepresentacionService {

    private final MoleculaRepository moleculaRepository;
    private final ElementoRepository elementoRepository;
    private final IonCatalogService ionCatalogService;
    private final IonicFormulaResolver ionicFormulaResolver;
    private final FormulaParserService formulaParserService;

    private final Estructura2DService estructura2DService;
    private final MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService;

    public MoleculaRepresentacionService(
            MoleculaRepository moleculaRepository,
            ElementoRepository elementoRepository,
            IonCatalogService ionCatalogService,
            IonicFormulaResolver ionicFormulaResolver,
            FormulaParserService formulaParserService,
            Estructura2DService estructura2DService,
            MoleculaRepresentacionIonicaService moleculaRepresentacionIonicaService
    ) {
        this.moleculaRepository = moleculaRepository;
        this.elementoRepository = elementoRepository;
        this.ionCatalogService = ionCatalogService;
        this.ionicFormulaResolver = ionicFormulaResolver;
        this.formulaParserService = formulaParserService;
        this.estructura2DService = estructura2DService;
        this.moleculaRepresentacionIonicaService = moleculaRepresentacionIonicaService;
    }

    public MoleculaRepresentacionDTO obtenerRepresentacion(Long id) {
        MoleculaEntity molecula = moleculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Molécula no encontrada"));

        return construirRepresentacion(molecula);
    }

    private MoleculaRepresentacionDTO construirRepresentacion(MoleculaEntity molecula) {
        String tipo = normalizar(molecula.getTipoCompuesto());
        String formulaVisual = limpiarFormula(molecula.getFormula());

        if (esOrganica(tipo) && tieneTexto(molecula.getCanonicalSmiles())) {
            return MoleculaRepresentacionDTO.smiles(
                    formulaVisual,
                    molecula.getCanonicalSmiles(),
                    molecula.getIsomericSmiles(),
                    molecula.getImagen2d()
            );
        }

        if (moleculaRepresentacionIonicaService.esRepresentacionIonicaPreferente(tipo, formulaVisual)) {
            return MoleculaRepresentacionDTO.ionica(
                    formulaVisual,
                    moleculaRepresentacionIonicaService.construirTextoIonico(formulaVisual, tipo)
            );
        }

        MoleculaRepresentacionDTO vsepr = intentarVsepr(formulaVisual);
        if (vsepr != null) {
            return vsepr;
        }

        Optional<MoleculaRepresentacionDTO> estructura2d = estructura2DService.intentarConstruir(formulaVisual);
        if (estructura2d.isPresent()) {
            return estructura2d.get();
        }

        if (moleculaRepresentacionIonicaService.esRepresentacionIonica(tipo)) {
            return MoleculaRepresentacionDTO.ionica(
                    formulaVisual,
                    moleculaRepresentacionIonicaService.construirTextoIonico(formulaVisual, tipo)
            );
        }

        return MoleculaRepresentacionDTO.formula(formulaVisual);
    }

    private boolean esRepresentacionIonicaPreferente(String tipo, String formulaVisual) {
        if (tipo.contains("sal") || tipo.contains("acido") || tipo.contains("hidroxido") || tipo.contains("base")) {
            return true;
        }

        if (tipo.contains("oxido")) {
            return esOxidoIonico(formulaVisual);
        }

        return false;
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

    private String construirTextoIonico(String formulaVisual, String tipo) {
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

    private String construirTextoAcido(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);

        if (esComposicion(atomos, "H", 2, "C", 1, "O", 3)) {
            return "2H⁺ + CO3²⁻";
        }

        if (esComposicion(atomos, "H", 3, "B", 1, "O", 3)) {
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

        if (esComposicion(atomos, "Al", 1, "O", 3, "H", 3)) {
            return "Al³⁺ + 3OH⁻";
        }

        if (esComposicion(atomos, "N", 1, "H", 5, "O", 1)) {
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

        if (esComposicion(atomos, "Ti", 1, "O", 2)) {
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

        if (esComposicion(atomos, "Na", 3, "P", 1, "O", 4)) {
            return "3Na⁺ + PO4³⁻";
        }

        if (esComposicion(atomos, "K", 3, "P", 1, "O", 4)) {
            return "3K⁺ + PO4³⁻";
        }

        if (esComposicion(atomos, "N", 3, "H", 12, "P", 1, "O", 4)) {
            return "3NH4⁺ + PO4³⁻";
        }

        return ionicFormulaResolver.resolver(formulaVisual)
                .map(this::formatearResolucionIonica)
                .orElse("catión + anión");
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

    private MoleculaRepresentacionDTO intentarVsepr(String formulaVisual) {
        Map<String, Integer> atomosFormula = parsearFormula(formulaVisual);

        if (atomosFormula.isEmpty()) {
            return null;
        }

        MoleculaRepresentacionDTO excepcion = intentarExcepcionVsepr(formulaVisual);
        if (excepcion != null) {
            return excepcion;
        }

        MoleculaRepresentacionDTO diatomica = intentarDiatomica(formulaVisual, atomosFormula);
        if (diatomica != null) {
            return diatomica;
        }

        Map<String, ElementoEntity> elementos = cargarElementosPorSimbolo(atomosFormula);

        if (!esCandidataCovalentePequena(atomosFormula, elementos)) {
            return null;
        }

        String central = elegirAtomoCentral(atomosFormula, elementos);

        if (central == null) {
            return null;
        }

        List<String> terminales = obtenerTerminales(atomosFormula, central);

        if (terminales.isEmpty() || terminales.size() > 4) {
            return null;
        }

        ElementoEntity elementoCentral = elementos.get(central);

        int paresLibres = estimarParesLibres(elementoCentral, terminales.size());
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
                paresLibres,
                vsepr,
                geometria,
                polaridad
        );
    }

    private MoleculaRepresentacionDTO intentarDiatomica(String formulaVisual, Map<String, Integer> atomosFormula) {
        int totalAtomos = atomosFormula.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalAtomos != 2) {
            return null;
        }

        List<String> atomosExpandidos = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : atomosFormula.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                atomosExpandidos.add(entry.getKey());
            }
        }

        if (atomosExpandidos.size() != 2) {
            return null;
        }

        String atomoCentral = atomosExpandidos.get(0);
        String atomoTerminal = atomosExpandidos.get(1);

        String polaridad = atomoCentral.equals(atomoTerminal)
                ? "No polar"
                : "Polar";

        return MoleculaRepresentacionDTO.vsepr(
                formulaVisual,
                atomoCentral,
                List.of(atomoTerminal),
                0,
                "AX1",
                "Lineal",
                polaridad
        );
    }

    private MoleculaRepresentacionDTO intentarExcepcionVsepr(String formulaVisual) {
        Map<String, Integer> atomos = parsearFormula(formulaVisual);


        if (esComposicion(atomos, "C", 1, "O", 2)) {
            return MoleculaRepresentacionDTO.vsepr(
                    formulaVisual,
                    "C",
                    List.of("O", "O"),
                    0,
                    "AX2",
                    "Lineal",
                    "No polar"
            );
        }

        if (esComposicion(atomos, "C", 1, "O", 1)) {
            return MoleculaRepresentacionDTO.vsepr(
                    formulaVisual,
                    "C",
                    List.of("O"),
                    0,
                    "AX1",
                    "Lineal",
                    "Polar"
            );
        }

        if (esComposicion(atomos, "N", 2, "O", 1)) {
            return MoleculaRepresentacionDTO.vsepr(
                    formulaVisual,
                    "N",
                    List.of("N", "O"),
                    0,
                    "AX2",
                    "Lineal",
                    "Polar"
            );
        }

        if (esComposicion(atomos, "S", 1, "O", 3)) {
            return MoleculaRepresentacionDTO.vsepr(
                    formulaVisual,
                    "S",
                    List.of("O", "O", "O"),
                    0,
                    "AX3",
                    "Trigonal plana",
                    "No polar"
            );
        }

        if (esComposicion(atomos, "Si", 1, "O", 2)) {
            return MoleculaRepresentacionDTO.vsepr(
                    formulaVisual,
                    "Si",
                    List.of("O", "O"),
                    0,
                    "AX2",
                    "Lineal",
                    "No polar"
            );
        }

        return null;
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

    private String elegirAtomoCentral(
            Map<String, Integer> atomosFormula,
            Map<String, ElementoEntity> elementos
    ) {
        List<String> candidatos = atomosFormula.keySet().stream()
                .filter(simbolo -> !"H".equals(simbolo))
                .filter(simbolo -> elementos.containsKey(simbolo))
                .filter(simbolo -> esNoMetal(elementos.get(simbolo)))
                .toList();

        if (candidatos.isEmpty()) {
            return null;
        }

        if (candidatos.contains("C")) return "C";
        if (candidatos.contains("N")) return "N";
        if (candidatos.contains("S")) return "S";
        if (candidatos.contains("P")) return "P";
        if (candidatos.contains("B")) return "B";

        return candidatos.stream()
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

    private int estimarParesLibres(ElementoEntity central, int enlaces) {
        Integer grupo = getGrupo(central);

        if (grupo == null) {
            return 0;
        }

        int electronesValencia = obtenerElectronesValenciaPorGrupo(grupo);

        if (electronesValencia <= 0) {
            return 0;
        }

        int electronesRestantes = electronesValencia - enlaces;

        if (electronesRestantes <= 0) {
            return 0;
        }

        return electronesRestantes / 2;
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

    private boolean esRepresentacionIonica(String tipo) {
        return tipo.contains("sal")
                || tipo.contains("acido")
                || tipo.contains("base")
                || tipo.contains("hidroxido")
                || tipo.contains("oxido");
    }

    private boolean esOrganica(String tipo) {
        return (tipo.contains("organica") || tipo.contains("organico"))
                && !tipo.contains("inorganica")
                && !tipo.contains("inorganico");
    }

    private Map<String, Integer> parsearFormula(String formula) {
        return formulaParserService.parsearFormula(formula);
    }

    private String limpiarFormula(String formula) {
        return formulaParserService.limpiarFormula(formula);
    }

    private String formatearCantidad(int cantidad, String texto) {
        if (cantidad <= 1) {
            return texto;
        }

        return cantidad + texto;
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
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

    private boolean esNoMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(getCategoria(elemento));

        return categoria.contains("nonmetal")
                || categoria.contains("halogen")
                || categoria.contains("noble gas")
                || categoria.contains("chalcogen")
                || categoria.contains("pnictogen")
                || categoria.contains("no metal")
                || categoria.contains("metaloide")
                || categoria.contains("metalloid");
    }

    private boolean esMetal(ElementoEntity elemento) {
        if (elemento == null) {
            return false;
        }

        String categoria = normalizar(getCategoria(elemento));

        return categoria.contains("metal")
                && !categoria.contains("nonmetal")
                && !categoria.contains("no metal")
                && !categoria.contains("metalloid")
                && !categoria.contains("metaloide");
    }

    private boolean esComposicion(Map<String, Integer> atomos, Object... pares) {
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

    private String getCategoria(ElementoEntity elemento) {
        return elemento.getCategoria();
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