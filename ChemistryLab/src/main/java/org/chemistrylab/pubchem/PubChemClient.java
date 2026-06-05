package org.chemistrylab.pubchem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PubChemClient {

    private static final String BASE_URL = "https://pubchem.ncbi.nlm.nih.gov/rest";
    private static final String PROPERTIES = String.join(",",
            "MolecularFormula",
            "MolecularWeight",
            "IUPACName",
            "Title",
            "CanonicalSMILES",
            "IsomericSMILES",
            "InChI",
            "InChIKey",
            "Charge",
            "XLogP",
            "TPSA",
            "HBondDonorCount",
            "HBondAcceptorCount",
            "RotatableBondCount",
            "HeavyAtomCount",
            "Complexity"
    );

    private final WebClient.Builder webClientBuilder;

    public Optional<Long> buscarCid(String query) {
        String valor = query == null ? "" : query.trim();

        if (valor.matches("\\d+")) {
            return Optional.of(Long.parseLong(valor));
        }

        String nombre = UriUtils.encodePathSegment(valor, StandardCharsets.UTF_8);
        String url = BASE_URL + "/pug/compound/name/" + nombre + "/cids/JSON";

        Map<?, ?> response = getMap(url);
        Object identifierList = response.get("IdentifierList");

        if (!(identifierList instanceof Map<?, ?> identifierMap)) {
            return Optional.empty();
        }

        Object cids = identifierMap.get("CID");
        if (!(cids instanceof List<?> cidList) || cidList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toLong(cidList.get(0)));
    }

    public PubChemCompoundData descargarCompuesto(Long cid) {
        Map<?, ?> propiedades = descargarPropiedades(cid);
        Map<String, String> pugView = descargarPugView(cid);

        return PubChemCompoundData.builder()
                .pubchemCid(cid)
                .nombre(getString(propiedades, "Title"))
                .formula(getString(propiedades, "MolecularFormula"))
                .masaMolecular(toBigDecimal(propiedades.get("MolecularWeight")))
                .nombreIupac(getString(propiedades, "IUPACName"))
                .descripcion(pugView.get("descripcion"))
                .carga(toInteger(propiedades.get("Charge")))
                .imagen2d("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + cid + "/PNG")
                .modelo3dUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + cid + "/SDF?record_type=3d")
                .puntoFusion(pugView.get("puntoFusion"))
                .puntoEbullicion(pugView.get("puntoEbullicion"))
                .densidad(pugView.get("densidad"))
                .solubilidad(pugView.get("solubilidad"))
                .ph(pugView.get("ph"))
                .riesgos(pugView.get("riesgos"))
                .usos(pugView.get("usos"))
                .sinonimos(pugView.get("sinonimos"))
                .canonicalSmiles(getString(propiedades, "CanonicalSMILES"))
                .isomericSmiles(getString(propiedades, "IsomericSMILES"))
                .inchi(getString(propiedades, "InChI"))
                .inchiKey(getString(propiedades, "InChIKey"))
                .xlogp(toBigDecimal(propiedades.get("XLogP")))
                .tpsa(toBigDecimal(propiedades.get("TPSA")))
                .donadoresH(toInteger(propiedades.get("HBondDonorCount")))
                .aceptoresH(toInteger(propiedades.get("HBondAcceptorCount")))
                .enlacesRotables(toInteger(propiedades.get("RotatableBondCount")))
                .atomosPesados(toInteger(propiedades.get("HeavyAtomCount")))
                .complejidad(toBigDecimal(propiedades.get("Complexity")))
                .build();
    }

    private Map<?, ?> descargarPropiedades(Long cid) {
        String url = BASE_URL + "/pug/compound/cid/" + cid + "/property/" + PROPERTIES + "/JSON";
        Map<?, ?> response = getMap(url);
        Object propertyTable = response.get("PropertyTable");

        if (!(propertyTable instanceof Map<?, ?> propertyMap)) {
            throw new RuntimeException("PubChem no devolvió propiedades para CID " + cid);
        }

        Object properties = propertyMap.get("Properties");
        if (!(properties instanceof List<?> propertyList) || propertyList.isEmpty()) {
            throw new RuntimeException("PubChem no devolvió propiedades para CID " + cid);
        }

        Object first = propertyList.get(0);
        if (!(first instanceof Map<?, ?> firstMap)) {
            throw new RuntimeException("Respuesta de propiedades inválida para CID " + cid);
        }

        return firstMap;
    }

    private Map<String, String> descargarPugView(Long cid) {
        String url = BASE_URL + "/pug_view/data/compound/" + cid + "/JSON";
        Map<?, ?> response;

        try {
            response = getMap(url);
        } catch (Exception ignored) {
            return Map.of();
        }

        Object record = response.get("Record");
        if (!(record instanceof Map<?, ?> recordMap)) {
            return Map.of();
        }

        Object sections = recordMap.get("Section");
        if (!(sections instanceof List<?> sectionList)) {
            return Map.of();
        }

        return Map.of(
                "descripcion", buscarTexto(sectionList, "Description"),
                "puntoFusion", buscarTexto(sectionList, "Melting Point"),
                "puntoEbullicion", buscarTexto(sectionList, "Boiling Point"),
                "densidad", buscarTexto(sectionList, "Density"),
                "solubilidad", buscarTexto(sectionList, "Solubility"),
                "ph", buscarTexto(sectionList, "pH"),
                "usos", String.join(" | ", buscarLista(sectionList, "Use and Manufacturing", 5)),
                "riesgos", String.join(" | ", buscarLista(sectionList, "GHS Classification", 5)),
                "sinonimos", String.join(", ", buscarLista(sectionList, "Depositor-Supplied Synonyms", 8))
        );
    }

    private String buscarTexto(List<?> sections, String titulo) {
        for (Object item : sections) {
            if (!(item instanceof Map<?, ?> section)) {
                continue;
            }

            Object heading = section.get("TOCHeading");
            if (titulo.equalsIgnoreCase(String.valueOf(heading))) {
                String texto = extraerTexto(section.get("Information"));
                if (texto != null && !texto.isBlank()) {
                    return texto;
                }
            }

            Object children = section.get("Section");
            if (children instanceof List<?> childList) {
                String encontrado = buscarTexto(childList, titulo);
                if (encontrado != null && !encontrado.isBlank()) {
                    return encontrado;
                }
            }
        }

        return "";
    }

    private List<String> buscarLista(List<?> sections, String titulo, int limite) {
        List<String> resultados = new ArrayList<>();
        buscarListaRecursiva(sections, titulo, limite, resultados);
        return resultados;
    }

    private void buscarListaRecursiva(List<?> sections, String titulo, int limite, List<String> resultados) {
        if (resultados.size() >= limite) {
            return;
        }

        for (Object item : sections) {
            if (!(item instanceof Map<?, ?> section)) {
                continue;
            }

            Object heading = section.get("TOCHeading");
            if (titulo.equalsIgnoreCase(String.valueOf(heading))) {
                extraerTextos(section.get("Information"), resultados, limite);
            }

            Object children = section.get("Section");
            if (children instanceof List<?> childList) {
                buscarListaRecursiva(childList, titulo, limite, resultados);
            }

            if (resultados.size() >= limite) {
                return;
            }
        }
    }

    private String extraerTexto(Object informationObject) {
        List<String> textos = new ArrayList<>();
        extraerTextos(informationObject, textos, 1);
        return textos.isEmpty() ? "" : textos.get(0);
    }

    private void extraerTextos(Object informationObject, List<String> resultados, int limite) {
        if (!(informationObject instanceof List<?> informationList)) {
            return;
        }

        for (Object item : informationList) {
            if (!(item instanceof Map<?, ?> info)) {
                continue;
            }

            Object value = info.get("Value");
            if (!(value instanceof Map<?, ?> valueMap)) {
                continue;
            }

            agregarStrings(valueMap.get("StringWithMarkup"), resultados, limite);
            agregarStrings(valueMap.get("String"), resultados, limite);
            agregarStrings(valueMap.get("Number"), resultados, limite);

            if (resultados.size() >= limite) {
                return;
            }
        }
    }

    private void agregarStrings(Object object, List<String> resultados, int limite) {
        if (object instanceof List<?> list) {
            for (Object value : list) {
                String texto;
                if (value instanceof Map<?, ?> valueMap) {
                    texto = getString(valueMap, "String");
                } else {
                    texto = String.valueOf(value);
                }

                if (texto != null && !texto.isBlank() && !resultados.contains(texto)) {
                    resultados.add(texto);
                }

                if (resultados.size() >= limite) {
                    return;
                }
            }
        }
    }

    private Map<?, ?> getMap(String url) {
        Object response = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        if (!(response instanceof Map<?, ?> responseMap)) {
            throw new RuntimeException("Respuesta inválida desde PubChem");
        }

        return responseMap;
    }

    private String getString(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(String.valueOf(value));
    }
}
