package org.chemistrylab.chemistry.ionic;

import org.chemistrylab.chemistry.catalog.IonCatalogService;
import org.chemistrylab.chemistry.config.IonConfig;
import org.chemistrylab.chemistry.formula.FormulaParserService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IonicFormulaResolver {

    private final IonCatalogService ionCatalogService;
    private final FormulaParserService formulaParserService;

    public IonicFormulaResolver(
            IonCatalogService ionCatalogService,
            FormulaParserService formulaParserService
    ) {
        this.ionCatalogService = ionCatalogService;
        this.formulaParserService = formulaParserService;
    }

    public Optional<IonicFormulaResolution> resolver(String formulaVisual) {
        Map<String, Integer> composicion = formulaParserService.parsearFormula(formulaVisual);

        if (composicion.isEmpty()) {
            return Optional.empty();
        }

        List<IonConfig> cationes = ionCatalogService.obtenerCationes().stream()
                .sorted(Comparator
                        .comparingInt((IonConfig ion) -> formulaParserService.parsearFormula(ion.getFormula()).size())
                        .thenComparingInt(ion -> ion.getFormula().length())
                        .reversed())
                .toList();

        List<IonConfig> aniones = ionCatalogService.obtenerAniones().stream()
                .sorted(Comparator
                        .comparingInt((IonConfig ion) -> formulaParserService.parsearFormula(ion.getFormula()).size())
                        .thenComparingInt(ion -> ion.getFormula().length())
                        .reversed())
                .toList();

        IonicFormulaResolution mejor = null;
        int mejorPuntuacion = Integer.MIN_VALUE;

        for (IonConfig cation : cationes) {
            Map<String, Integer> composicionCation = formulaParserService.parsearFormula(cation.getFormula());

            for (int cantidadCation = 1; cantidadCation <= 8; cantidadCation++) {
                if (!formulaParserService.contieneComposicion(composicion, composicionCation, cantidadCation)) {
                    continue;
                }

                Map<String, Integer> restante = formulaParserService.restarComposicion(
                        composicion,
                        composicionCation,
                        cantidadCation
                );

                for (IonConfig anion : aniones) {
                    Map<String, Integer> composicionAnion = formulaParserService.parsearFormula(anion.getFormula());

                    for (int cantidadAnion = 1; cantidadAnion <= 8; cantidadAnion++) {
                        if (!formulaParserService.contieneComposicion(restante, composicionAnion, cantidadAnion)) {
                            continue;
                        }

                        Map<String, Integer> finalRestante = formulaParserService.restarComposicion(
                                restante,
                                composicionAnion,
                                cantidadAnion
                        );

                        if (!formulaParserService.estaVacia(finalRestante)) {
                            continue;
                        }

                        int cargaTotal = cation.getCarga() * cantidadCation + anion.getCarga() * cantidadAnion;

                        if (cargaTotal != 0) {
                            continue;
                        }

                        int puntuacion = calcularPuntuacion(
                                cation,
                                anion,
                                cantidadCation,
                                cantidadAnion
                        );

                        if (puntuacion > mejorPuntuacion) {
                            mejorPuntuacion = puntuacion;
                            mejor = new IonicFormulaResolution(
                                    new IonMatch(cation, cantidadCation),
                                    new IonMatch(anion, cantidadAnion)
                            );
                        }
                    }
                }
            }
        }

        return Optional.ofNullable(mejor);
    }

    public Optional<IonMatch> resolverAnionRestante(String formulaAnion, int cargaEsperada) {
        Map<String, Integer> composicion = formulaParserService.parsearFormula(formulaAnion);

        return ionCatalogService.obtenerAniones().stream()
                .filter(ion -> ion.getCarga() != null && ion.getCarga() == cargaEsperada)
                .filter(ion -> mismaComposicion(
                        composicion,
                        formulaParserService.parsearFormula(ion.getFormula())
                ))
                .findFirst()
                .map(ion -> new IonMatch(ion, 1));
    }

    private int calcularPuntuacion(
            IonConfig cation,
            IonConfig anion,
            int cantidadCation,
            int cantidadAnion
    ) {
        int puntuacion = 0;

        puntuacion += cation.getFormula().length() * 10;
        puntuacion += anion.getFormula().length() * 20;

        if ("poliatomico".equalsIgnoreCase(anion.getCategoria())
                || "oxoanion".equalsIgnoreCase(anion.getCategoria())
                || "oxoanion_acido".equalsIgnoreCase(anion.getCategoria())) {
            puntuacion += 100;
        }

        if ("poliatomico".equalsIgnoreCase(cation.getCategoria())) {
            puntuacion += 80;
        }

        puntuacion -= cantidadCation;
        puntuacion -= cantidadAnion;

        return puntuacion;
    }

    private boolean mismaComposicion(Map<String, Integer> a, Map<String, Integer> b) {
        return a.equals(b);
    }
}