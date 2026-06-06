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
import java.util.concurrent.TimeUnit;

public class CdkDepictionPlayground {

    private static final Path OUTPUT_ROOT = Path.of("depiction-test-output");
    private static final Path CDK_DIR = OUTPUT_ROOT.resolve("cdk");
    private static final Path OPENBABEL_DIR = OUTPUT_ROOT.resolve("openbabel");
    private static final Path RDKIT_DIR = OUTPUT_ROOT.resolve("rdkit");
    private static final Path INDIGO_DIR = OUTPUT_ROOT.resolve("indigo");
    private static final Path RDKIT_SCRIPT = Path.of("tools", "rdkit_depiction_generator.py");
    private static final Path INDIGO_SCRIPT = Path.of("tools", "indigo_depiction_generator.py");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(CDK_DIR);
        Files.createDirectories(OPENBABEL_DIR);
        Files.createDirectories(RDKIT_DIR);
        Files.createDirectories(INDIGO_DIR);

        List<TestMolecule> molecules = testMolecules();
        boolean openBabelAvailable = isCommandAvailable("obabel", "-V");
        boolean rdkitAvailable = runPythonGenerator(RDKIT_SCRIPT, RDKIT_DIR, 60);
        boolean indigoAvailable = runPythonGenerator(INDIGO_SCRIPT, INDIGO_DIR, 60);

        for (TestMolecule molecule : molecules) {
            String filename = slug(molecule.name()) + ".svg";

            try {
                String svg = generateCdkSvg(molecule.smiles());
                Files.writeString(CDK_DIR.resolve(filename), svg, StandardCharsets.UTF_8);
            } catch (Exception exception) {
                Files.writeString(CDK_DIR.resolve(filename), errorSvg("CDK error", exception.getMessage()), StandardCharsets.UTF_8);
            }

            if (openBabelAvailable) {
                try {
                    generateOpenBabelSvg(molecule.smiles(), OPENBABEL_DIR.resolve(filename));
                } catch (Exception exception) {
                    Files.writeString(OPENBABEL_DIR.resolve(filename), errorSvg("OpenBabel error", exception.getMessage()), StandardCharsets.UTF_8);
                }
            }
        }

        String comparison = buildComparisonReport(molecules, openBabelAvailable, rdkitAvailable, indigoAvailable);
        Files.writeString(OUTPUT_ROOT.resolve("comparison.html"), comparison, StandardCharsets.UTF_8);
        Files.writeString(OUTPUT_ROOT.resolve("report.html"), comparison, StandardCharsets.UTF_8);

        System.out.println("Generado: depiction-test-output/comparison.html");
        System.out.println("OpenBabel detectado: " + openBabelAvailable);
        System.out.println("RDKit detectado: " + rdkitAvailable);
        System.out.println("Indigo detectado: " + indigoAvailable);
    }

    private static List<TestMolecule> testMolecules() {
        return List.of(
                new TestMolecule("Water", "H2O", "COVALENTE_SIMPLE", "O", "SMILES compacto: los motores añaden H implícitos; puede verse demasiado simple o grande."),
                new TestMolecule("Water explicit", "H2O", "COVALENTE_SIMPLE", "[H]O[H]", "Comparar con Water compacto. Debería parecerse más a estructura real."),
                new TestMolecule("Ammonia", "NH3", "COVALENTE_SIMPLE", "N", "SMILES compacto: los motores añaden H implícitos."),
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
                new TestMolecule("Hydrogen cyanide", "HCN", "COVALENTE_SIMPLE", "C#N", "Los H pueden quedar implícitos; probar también explícito."),
                new TestMolecule("Hydrogen cyanide explicit", "HCN", "COVALENTE_SIMPLE", "[H]C#N", "Más claro para la app."),
                new TestMolecule("Hydrochloric acid", "HCl", "HIDRACIDO", "Cl", "Compacto queda como cloro con H implícito."),
                new TestMolecule("Hydrochloric acid explicit", "HCl", "HIDRACIDO", "[H]Cl", "Más correcto para mostrar enlace H-Cl."),
                new TestMolecule("Nitric acid", "HNO3", "OXOACIDO", "O[N+](=O)[O-]", "Bastante bien. Estilo parecido a PubChem."),
                new TestMolecule("Sulfuric acid", "H2SO4", "OXOACIDO", "OS(=O)(=O)O", "Bien."),
                new TestMolecule("Phosphoric acid", "H3PO4", "OXOACIDO", "OP(=O)(O)O", "Bien."),
                new TestMolecule("Carbonic acid", "H2CO3", "OXOACIDO", "OC(=O)O", "Muy buen candidato para motor 2D automático."),
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
                new TestMolecule("Potassium ferricyanide", "K3Fe(CN)6", "COMPLEJO", "[K+].[K+].[K+].N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N", "Complejo: reconocible, pero puede saturar tarjeta."),
                new TestMolecule("Sodium dichromate", "Na2Cr2O7", "OXISAL", "[Na+].[Na+].[O-][Cr](=O)(=O)O[Cr](=O)(=O)[O-]", "Probar si gestiona bien oxoanión puente."),
                new TestMolecule("Potassium permanganate", "KMnO4", "OXISAL", "[K+].[O-][Mn](=O)(=O)=O", "Probar metal central con oxígenos."),
                new TestMolecule("Tetrahydrocannabinol", "C21H30O2", "ORGANICA", "CCCCCC1=CC(=C2C3C=C(CC(C3CC(CC2=C1O)(C)C)O)C)O", "Orgánica: comparar calidad del layout."),
                new TestMolecule("ATP", "C10H16N5O13P3", "ORGANOFOSFATO", "Nc1ncnc2c1ncn2C3OC(COP(=O)(O)OP(=O)(O)OP(=O)(O)O)C(O)C3O", "Molécula grande con fosfatos. Buen test para orgánica + grupos inorgánicos.")
        );
    }

    private static String generateCdkSvg(String smiles) throws CDKException {
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

    private static void generateOpenBabelSvg(String smiles, Path outputFile) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "obabel",
                "-:" + smiles,
                "-O",
                outputFile.toString(),
                "--gen2d"
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        boolean finished = process.waitFor(20, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new IOException("Tiempo agotado ejecutando obabel");
        }

        if (process.exitValue() != 0 || !Files.exists(outputFile) || Files.size(outputFile) == 0) {
            throw new IOException("obabel no pudo generar SVG para SMILES: " + smiles);
        }
    }

    private static boolean runPythonGenerator(Path script, Path outputDir, int timeoutSeconds) {
        if (!Files.exists(script)) {
            return false;
        }

        return runPythonGenerator("python", script, outputDir, timeoutSeconds)
                || runPythonGenerator("python3", script, outputDir, timeoutSeconds)
                || runPythonGenerator("py", script, outputDir, timeoutSeconds);
    }

    private static boolean runPythonGenerator(String pythonCommand, Path script, Path outputDir, int timeoutSeconds) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonCommand,
                    script.toString(),
                    "--out",
                    outputDir.toString()
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return false;
            }

            return process.exitValue() == 0;
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isCommandAvailable(String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception exception) {
            return false;
        }
    }

    private static String buildComparisonReport(List<TestMolecule> molecules, boolean openBabelAvailable, boolean rdkitAvailable, boolean indigoAvailable) {
        StringBuilder report = new StringBuilder();
        report.append("<!doctype html><html><head><meta charset='UTF-8'><title>Unified depiction comparison</title>")
                .append("<script src='https://unpkg.com/smiles-drawer@2.0.1/dist/smiles-drawer.min.js'></script>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;background:#f7f4ec;padding:24px;color:#111827;}")
                .append("h1{margin:0 0 6px;} .intro{margin:0 0 22px;color:#4b5563;max-width:1200px;line-height:1.45;}")
                .append(".molecule-card{background:white;border:1px solid #d7d0c4;border-radius:16px;padding:16px;margin-bottom:22px;box-shadow:0 4px 16px #0001;}")
                .append(".head{display:flex;justify-content:space-between;gap:10px;align-items:start;margin-bottom:12px;}")
                .append("h2{font-size:24px;margin:0;} .formula{font-weight:800;background:#111827;color:#fff8c6;border-radius:999px;padding:6px 12px;white-space:nowrap;}")
                .append(".meta{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;}")
                .append(".tag{font-size:11px;font-weight:800;border:1px solid #d1d5db;border-radius:999px;padding:4px 8px;background:#f9fafb;}")
                .append(".engines{display:grid;grid-template-columns:repeat(auto-fit,minmax(230px,1fr));gap:14px;}")
                .append(".engine-card{border:1px solid #e5e7eb;border-radius:14px;padding:10px;background:#fcfcfd;}")
                .append(".engine-title{font-size:13px;font-weight:900;margin-bottom:8px;border-radius:999px;display:inline-block;padding:4px 8px;background:#eef2ff;border:1px solid #c7d2fe;}")
                .append("img,.smiles-canvas{width:100%;height:210px;object-fit:contain;border:1px solid #eee;border-radius:10px;background:#fff;}")
                .append(".placeholder{height:210px;display:flex;align-items:center;justify-content:center;text-align:center;border:1px dashed #cbd5e1;border-radius:10px;background:#f8fafc;color:#64748b;font-weight:800;padding:10px;}")
                .append("code{font-size:12px;word-break:break-all;display:block;background:#f3f4f6;border-radius:8px;padding:8px;margin-top:10px;}")
                .append(".note{font-size:13px;line-height:1.35;color:#374151;margin-top:10px;background:#fff7ed;border:1px solid #fed7aa;border-radius:10px;padding:8px;}")
                .append("</style></head><body><h1>Unified depiction comparison</h1>")
                .append("<p class='intro'>Una tarjeta por molécula y una columna por motor. CDK genera SVG desde Java. SmilesDrawer se dibuja en el navegador usando el mismo SMILES. OpenBabel se genera si existe el comando obabel en PATH. RDKit e Indigo se generan si existe Python con sus librerías instaladas.</p>");

        for (int i = 0; i < molecules.size(); i++) {
            TestMolecule molecule = molecules.get(i);
            String filename = slug(molecule.name()) + ".svg";
            String canvasId = "smilesdrawer_" + i;

            report.append("<section class='molecule-card'><div class='head'><h2>")
                    .append(escapeHtml(molecule.name()))
                    .append("</h2><span class='formula'>")
                    .append(escapeHtml(molecule.formula()))
                    .append("</span></div><div class='meta'><span class='tag'>")
                    .append(escapeHtml(molecule.family()))
                    .append("</span></div><div class='engines'>");

            appendEngineWithImage(report, "CDK", "cdk/" + filename, molecule.smiles(), molecule.note());
            appendSmilesDrawerEngine(report, canvasId, molecule.smiles());

            if (openBabelAvailable) {
                appendEngineWithImage(report, "OpenBabel", "openbabel/" + filename, molecule.smiles(), "SVG generado con obabel --gen2d desde el mismo SMILES.");
            } else {
                appendPlaceholder(report, "OpenBabel", "No detectado. Instala OpenBabel y asegúrate de que obabel esté en PATH.");
            }

            if (rdkitAvailable) {
                appendEngineWithImage(report, "RDKit", "rdkit/" + filename, molecule.smiles(), "SVG generado por Python/RDKit desde el mismo SMILES.");
            } else {
                appendPlaceholder(report, "RDKit", "No detectado. Instala Python con rdkit o ejecuta tools/rdkit_depiction_generator.py manualmente.");
            }

            if (indigoAvailable) {
                appendEngineWithImage(report, "Indigo", "indigo/" + filename, molecule.smiles(), "SVG generado por Python/Indigo desde el mismo SMILES.");
            } else {
                appendPlaceholder(report, "Indigo", "No detectado. Instala epam.indigo o ejecuta tools/indigo_depiction_generator.py manualmente.");
            }

            report.append("</div></section>");
        }

        appendSmilesDrawerScript(report, molecules);
        report.append("</body></html>");
        return report.toString();
    }

    private static void appendEngineWithImage(StringBuilder report, String engine, String imagePath, String smiles, String note) {
        report.append("<div class='engine-card'><div class='engine-title'>")
                .append(escapeHtml(engine))
                .append("</div><img src='")
                .append(escapeHtml(imagePath))
                .append("'><code>")
                .append(escapeHtml(smiles))
                .append("</code><div class='note'>")
                .append(escapeHtml(note))
                .append("</div></div>");
    }

    private static void appendSmilesDrawerEngine(StringBuilder report, String canvasId, String smiles) {
        report.append("<div class='engine-card'><div class='engine-title'>SmilesDrawer</div><canvas class='smiles-canvas' id='")
                .append(escapeHtml(canvasId))
                .append("' width='420' height='300'></canvas><code>")
                .append(escapeHtml(smiles))
                .append("</code><div class='note'>Render JS desde el mismo SMILES usado por CDK.</div></div>");
    }

    private static void appendPlaceholder(StringBuilder report, String engine, String message) {
        report.append("<div class='engine-card'><div class='engine-title'>")
                .append(escapeHtml(engine))
                .append("</div><div class='placeholder'>")
                .append(escapeHtml(message))
                .append("</div></div>");
    }

    private static void appendSmilesDrawerScript(StringBuilder report, List<TestMolecule> molecules) {
        report.append("<script>\n")
                .append("const drawer = new SmilesDrawer.Drawer({ width: 420, height: 300, padding: 28, compactDrawing: true, terminalCarbons: false });\n")
                .append("const smilesJobs = [\n");

        for (int i = 0; i < molecules.size(); i++) {
            TestMolecule molecule = molecules.get(i);
            report.append("{ id: 'smilesdrawer_")
                    .append(i)
                    .append("', smiles: ")
                    .append(toJsString(molecule.smiles()))
                    .append(" }");
            if (i < molecules.size() - 1) {
                report.append(",");
            }
            report.append("\n");
        }

        report.append("];\n")
                .append("for (const job of smilesJobs) {\n")
                .append("  const canvas = document.getElementById(job.id);\n")
                .append("  if (!canvas) continue;\n")
                .append("  SmilesDrawer.parse(job.smiles, tree => drawer.draw(tree, canvas, 'light', false), err => {\n")
                .append("    const ctx = canvas.getContext('2d'); ctx.clearRect(0,0,canvas.width,canvas.height); ctx.font='16px Arial'; ctx.fillStyle='#991b1b'; ctx.textAlign='center'; ctx.fillText('SmilesDrawer error', canvas.width/2, canvas.height/2); console.error(err);\n")
                .append("  });\n")
                .append("}\n")
                .append("</script>");
    }

    private static String errorSvg(String title, String message) {
        return "<svg xmlns='http://www.w3.org/2000/svg' width='420' height='300'>"
                + "<rect width='100%' height='100%' fill='#fff1f2'/>"
                + "<text x='210' y='130' text-anchor='middle' font-family='Arial' font-size='22' font-weight='700' fill='#991b1b'>" + escapeXml(title) + "</text>"
                + "<text x='210' y='165' text-anchor='middle' font-family='Arial' font-size='13' fill='#7f1d1d'>" + escapeXml(message) + "</text>"
                + "</svg>";
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

    private static String escapeXml(String value) {
        return escapeHtml(value);
    }

    private static String toJsString(String value) {
        return "'" + String.valueOf(value)
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "") + "'";
    }

    private record TestMolecule(String name, String formula, String family, String smiles, String note) {
    }
}
