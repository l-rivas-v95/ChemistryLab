package org.chemistrylab.tools;

import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public class CdkDepictionPlayground {

    private static final Path OUTPUT_DIR = Path.of("depiction-test-output", "cdk");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(OUTPUT_DIR);

        List<TestMolecule> molecules = List.of(
                new TestMolecule("Water", "H2O", "COVALENTE_SIMPLE", "O", "SMILES compacto: CDK añade H implícitos y queda demasiado grande."),
                new TestMolecule("Water explicit", "H2O", "COVALENTE_SIMPLE", "[H]O[H]", "Comparar con Water compacto. Debería parecerse más a estructura real."),
                new TestMolecule("Ammonia", "NH3", "COVALENTE_SIMPLE", "N", "SMILES compacto: CDK añade H implícitos y queda enorme."),
                new TestMolecule("Ammonia explicit", "NH3", "COVALENTE_SIMPLE", "[H]N([H])[H]", "Comparar con Ammonia compacto."),
                new TestMolecule("Hydrogen peroxide", "H2O2", "COVALENTE_SIMPLE", "OO", "Aceptable, pero conviene comparar con explícito."),
                new TestMolecule("Hydrogen peroxide explicit", "H2O2", "COVALENTE_SIMPLE", "[H]OO[H]", "Versión con hidrógenos explícitos."),
                new TestMolecule("Carbon dioxide", "CO2", "OXIDO_COVALENTE", "O=C=O", "Bien. Lineal y reconocible."),
                new TestMolecule("Carbon monoxide", "CO", "OXIDO_COVALENTE", "[C-]#[O+]", "Bien químicamente, pero las cargas pueden verse raras para nivel educativo."),
                new TestMolecule("Sulfur dioxide", "SO2", "OXIDO_COVALENTE", "O=S=O", "Bastante bien."),
                new TestMolecule("Sulfur trioxide", "SO3", "OXIDO_COVALENTE", "O=S(=O)=O", "Bastante bien."),
                new TestMolecule("Nitric oxide", "NO", "OXIDO_COVALENTE", "N=O", "Aceptable."),
                new TestMolecule("Nitrogen dioxide", "NO2", "OXIDO_COVALENTE", "O=[N+][O-]", "Bien, aunque muestra cargas formales."),
                new TestMolecule("Dinitrogen monoxide", "N2O", "OXIDO_COVALENTE", "N#[N+][O-]", "Bien, aunque muestra cargas formales."),
                new TestMolecule("Ozone", "O3", "COVALENTE_SIMPLE", "O=[O+][O-]", "Bien, aunque muestra cargas formales."),
                new TestMolecule("Hydrogen cyanide", "HCN", "COVALENTE_SIMPLE", "C#N", "CDK oculta H implícito; probar también explícito."),
                new TestMolecule("Hydrogen cyanide explicit", "HCN", "COVALENTE_SIMPLE", "[H]C#N", "Más claro para la app."),
                new TestMolecule("Hydrochloric acid", "HCl", "HIDRACIDO", "Cl", "Compacto queda como HCl enorme por H implícito."),
                new TestMolecule("Hydrochloric acid explicit", "HCl", "HIDRACIDO", "[H]Cl", "Más correcto para mostrar enlace H-Cl."),
                new TestMolecule("Nitric acid", "HNO3", "OXOACIDO", "O[N+](=O)[O-]", "Bastante bien. Estilo parecido a PubChem."),
                new TestMolecule("Sulfuric acid", "H2SO4", "OXOACIDO", "OS(=O)(=O)O", "Bien."),
                new TestMolecule("Phosphoric acid", "H3PO4", "OXOACIDO", "OP(=O)(O)O", "Bien."),
                new TestMolecule("Carbonic acid", "H2CO3", "OXOACIDO", "OC(=O)O", "Muy buen candidato para usar CDK."),
                new TestMolecule("Sodium chloride", "NaCl", "SAL_BINARIA", "[Na+].[Cl-]", "Visualmente claro, aunque no representa red cristalina."),
                new TestMolecule("Potassium chloride", "KCl", "SAL_BINARIA", "[K+].[Cl-]", "Visualmente claro, aunque no representa red cristalina."),
                new TestMolecule("Calcium chloride", "CaCl2", "SAL_BINARIA", "[Ca+2].[Cl-].[Cl-]", "Aceptable, pero la colocación puede ser irregular."),
                new TestMolecule("Sodium hydroxide", "NaOH", "HIDROXIDO", "[Na+].[OH-]", "Aceptable."),
                new TestMolecule("Calcium hydroxide", "Ca(OH)2", "HIDROXIDO", "[Ca+2].[OH-].[OH-]", "Aceptable, pero la colocación puede ser irregular."),
                new TestMolecule("Sodium carbonate", "Na2CO3", "OXISAL", "[Na+].[Na+].[O-]C(=O)[O-]", "Bastante claro."),
                new TestMolecule("Calcium carbonate", "CaCO3", "OXISAL", "[Ca+2].[O-]C(=O)[O-]", "Bastante claro."),
                new TestMolecule("Sodium bicarbonate", "NaHCO3", "OXISAL_ACIDA", "[Na+].OC(=O)[O-]", "Bastante claro."),
                new TestMolecule("Potassium carbonate", "K2CO3", "OXISAL", "[K+].[K+].[O-]C(=O)[O-]", "Comparar colocación con sodio."),
                new TestMolecule("Sodium sulfate", "Na2SO4", "OXISAL", "[Na+].[Na+].[O-]S(=O)(=O)[O-]", "Bastante claro."),
                new TestMolecule("Potassium sulfate", "K2SO4", "OXISAL", "[K+].[K+].[O-]S(=O)(=O)[O-]", "Bastante claro."),
                new TestMolecule("Calcium sulfate", "CaSO4", "OXISAL", "[Ca+2].[O-]S(=O)(=O)[O-]", "Bastante claro."),
                new TestMolecule("Aluminum sulfate", "Al2(SO4)3", "OXISAL", "[Al+3].[Al+3].[O-]S(=O)(=O)[O-].[O-]S(=O)(=O)[O-].[O-]S(=O)(=O)[O-]", "Punto débil: varios grupos, se hace pequeño y cargado."),
                new TestMolecule("Sodium nitrate", "NaNO3", "OXISAL", "[Na+].[O-][N+](=O)[O-]", "Bastante claro."),
                new TestMolecule("Calcium nitrate", "Ca(NO3)2", "OXISAL", "[Ca+2].[O-][N+](=O)[O-].[O-][N+](=O)[O-]", "Aceptable, algo saturado."),
                new TestMolecule("Iron(III) nitrate", "Fe(NO3)3", "OXISAL", "[Fe+3].[O-][N+](=O)[O-].[O-][N+](=O)[O-].[O-][N+](=O)[O-]", "Punto débil: varios grupos, se hace pequeño."),
                new TestMolecule("Sodium phosphate", "Na3PO4", "OXISAL", "[Na+].[Na+].[Na+].[O-]P(=O)([O-])[O-]", "Bastante claro."),
                new TestMolecule("Calcium phosphate", "Ca3(PO4)2", "OXISAL", "[Ca+2].[Ca+2].[Ca+2].[O-]P(=O)([O-])[O-].[O-]P(=O)([O-])[O-]", "Punto débil: muchos iones, se compacta."),
                new TestMolecule("Ammonium sulfate", "(NH4)2SO4", "OXISAL", "[NH4+].[NH4+].[O-]S(=O)(=O)[O-]", "Punto débil: amonio puede verse raro."),
                new TestMolecule("Potassium ferricyanide", "K3Fe(CN)6", "COMPLEJO", "[K+].[K+].[K+].N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N", "Complejo: sorprendentemente reconocible, pero puede saturar tarjeta."),
                new TestMolecule("Sodium dichromate", "Na2Cr2O7", "OXISAL", "[Na+].[Na+].[O-][Cr](=O)(=O)O[Cr](=O)(=O)[O-]", "Probar si CDK gestiona bien oxoanión puente."),
                new TestMolecule("Potassium permanganate", "KMnO4", "OXISAL", "[K+].[O-][Mn](=O)(=O)=O", "Probar metal central con oxígenos."),
                new TestMolecule("Tetrahydrocannabinol", "C21H30O2", "ORGANICA", "CCCCCC1=CC(=C2C3C=C(CC(C3CC(CC2=C1O)(C)C)O)C)O", "Orgánica: CDK debería ganar o empatar con SmilesDrawer."),
                new TestMolecule("ATP", "C10H16N5O13P3", "ORGANOFOSFATO", "Nc1ncnc2c1ncn2C3OC(COP(=O)(O)OP(=O)(O)OP(=O)(O)O)C(O)C3O", "Molécula grande con fosfatos. Buen test para orgánica + grupos inorgánicos.")
        );

        StringBuilder report = new StringBuilder();
        report.append("<!doctype html><html><head><meta charset='UTF-8'><title>Depiction engine comparison</title>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;background:#f7f4ec;padding:24px;color:#111827;}")
                .append("h1{margin:0 0 6px;} .intro{margin:0 0 22px;color:#4b5563;max-width:980px;line-height:1.45;}")
                .append(".grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(300px,1fr));gap:18px;}")
                .append(".card{background:white;border:1px solid #ddd;border-radius:14px;padding:14px;box-shadow:0 4px 16px #0001;}")
                .append(".head{display:flex;justify-content:space-between;gap:10px;align-items:start;margin-bottom:10px;}")
                .append("h2{font-size:20px;margin:0;} .formula{font-weight:800;background:#111827;color:#fff8c6;border-radius:999px;padding:5px 10px;white-space:nowrap;}")
                .append(".meta{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;}")
                .append(".tag{font-size:11px;font-weight:800;border:1px solid #d1d5db;border-radius:999px;padding:4px 8px;background:#f9fafb;}")
                .append(".engine{background:#eef2ff;border-color:#c7d2fe;}")
                .append("img{width:100%;height:210px;object-fit:contain;border:1px solid #eee;border-radius:10px;background:#fff;}")
                .append("code{font-size:12px;word-break:break-all;display:block;background:#f3f4f6;border-radius:8px;padding:8px;margin-top:10px;}")
                .append(".note{font-size:13px;line-height:1.35;color:#374151;margin-top:10px;background:#fff7ed;border:1px solid #fed7aa;border-radius:10px;padding:8px;}")
                .append(".error{background:#fef2f2;border-color:#fecaca;color:#991b1b;}")
                .append("</style>")
                .append("</head><body><h1>Depiction engine comparison</h1>")
                .append("<p class='intro'>Prueba independiente. Este informe compara cómo dibuja CDK distintas familias químicas desde SMILES. Los puntos débiles marcados sirven para decidir si conviene usar CDK, SmilesDrawer, OpenBabel, Indigo o un fallback propio. ATP queda incluido desde ahora como caso de molécula grande con grupos fosfato.</p>")
                .append("<div class='grid'>");

        for (TestMolecule molecule : molecules) {
            String filename = slug(molecule.name()) + ".svg";
            Path output = OUTPUT_DIR.resolve(filename);

            try {
                String svg = generateSvg(molecule.smiles());
                Files.writeString(output, svg, StandardCharsets.UTF_8);
                appendSuccessCard(report, molecule, filename);
            } catch (Exception exception) {
                appendErrorCard(report, molecule, exception);
            }
        }

        report.append("</div></body></html>");
        Files.writeString(Path.of("depiction-test-output", "report.html"), report.toString(), StandardCharsets.UTF_8);

        System.out.println("Generado: depiction-test-output/report.html");
    }

    private static String generateSvg(String smiles) throws CDKException {
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        StructureDiagramGenerator generator = new StructureDiagramGenerator();
        generator.setMolecule(molecule);
        generator.generateCoordinates();
        IAtomContainer withCoordinates = generator.getMolecule();

        DepictionGenerator depictionGenerator = new DepictionGenerator()
                .withSize(420, 300)
                .withAtomColors()
                .withFillToFit()
                .withTerminalCarbons();

        return depictionGenerator.depict(withCoordinates).toSvgStr();
    }

    private static void appendSuccessCard(StringBuilder report, TestMolecule molecule, String filename) {
        report.append("<div class='card'><div class='head'><h2>")
                .append(escapeHtml(molecule.name()))
                .append("</h2><span class='formula'>")
                .append(escapeHtml(molecule.formula()))
                .append("</span></div><div class='meta'><span class='tag engine'>CDK Depiction</span><span class='tag'>")
                .append(escapeHtml(molecule.family()))
                .append("</span></div><img src='cdk/")
                .append(filename)
                .append("'><code>")
                .append(escapeHtml(molecule.smiles()))
                .append("</code><div class='note'>")
                .append(escapeHtml(molecule.note()))
                .append("</div></div>");
    }

    private static void appendErrorCard(StringBuilder report, TestMolecule molecule, Exception exception) {
        report.append("<div class='card'><div class='head'><h2>")
                .append(escapeHtml(molecule.name()))
                .append("</h2><span class='formula'>")
                .append(escapeHtml(molecule.formula()))
                .append("</span></div><div class='meta'><span class='tag engine'>CDK Depiction</span><span class='tag'>")
                .append(escapeHtml(molecule.family()))
                .append("</span></div><div class='note error'>Error: ")
                .append(escapeHtml(exception.getMessage()))
                .append("</div><code>")
                .append(escapeHtml(molecule.smiles()))
                .append("</code><div class='note'>")
                .append(escapeHtml(molecule.note()))
                .append("</div></div>");
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    private static String escapeHtml(String value) {
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private record TestMolecule(String name, String formula, String family, String smiles, String note) {
    }
}
