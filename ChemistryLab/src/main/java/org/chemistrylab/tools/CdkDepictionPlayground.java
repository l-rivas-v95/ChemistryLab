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
                new TestMolecule("Water", "O"),
                new TestMolecule("Ammonia", "N"),
                new TestMolecule("Hydrogen peroxide", "OO"),
                new TestMolecule("Carbon dioxide", "O=C=O"),
                new TestMolecule("Carbon monoxide", "[C-]#[O+]"),
                new TestMolecule("Sulfur dioxide", "O=S=O"),
                new TestMolecule("Sulfur trioxide", "O=S(=O)=O"),
                new TestMolecule("Nitric oxide", "N=O"),
                new TestMolecule("Nitrogen dioxide", "O=[N+][O-]"),
                new TestMolecule("Dinitrogen monoxide", "N#[N+][O-]"),
                new TestMolecule("Ozone", "O=[O+][O-]"),
                new TestMolecule("Hydrogen cyanide", "C#N"),
                new TestMolecule("Hydrochloric acid", "Cl"),
                new TestMolecule("Nitric acid", "O[N+](=O)[O-]"),
                new TestMolecule("Sulfuric acid", "OS(=O)(=O)O"),
                new TestMolecule("Phosphoric acid", "OP(=O)(O)O"),
                new TestMolecule("Carbonic acid", "OC(=O)O"),
                new TestMolecule("Sodium chloride", "[Na+].[Cl-]"),
                new TestMolecule("Potassium chloride", "[K+].[Cl-]"),
                new TestMolecule("Calcium chloride", "[Ca+2].[Cl-].[Cl-]"),
                new TestMolecule("Sodium hydroxide", "[Na+].[OH-]"),
                new TestMolecule("Calcium hydroxide", "[Ca+2].[OH-].[OH-]"),
                new TestMolecule("Sodium carbonate", "[Na+].[Na+].[O-]C(=O)[O-]"),
                new TestMolecule("Calcium carbonate", "[Ca+2].[O-]C(=O)[O-]"),
                new TestMolecule("Sodium bicarbonate", "[Na+].OC(=O)[O-]"),
                new TestMolecule("Sodium sulfate", "[Na+].[Na+].[O-]S(=O)(=O)[O-]"),
                new TestMolecule("Aluminum sulfate", "[Al+3].[Al+3].[O-]S(=O)(=O)[O-].[O-]S(=O)(=O)[O-].[O-]S(=O)(=O)[O-]"),
                new TestMolecule("Sodium nitrate", "[Na+].[O-][N+](=O)[O-]"),
                new TestMolecule("Calcium nitrate", "[Ca+2].[O-][N+](=O)[O-].[O-][N+](=O)[O-]"),
                new TestMolecule("Sodium phosphate", "[Na+].[Na+].[Na+].[O-]P(=O)([O-])[O-]"),
                new TestMolecule("Calcium phosphate", "[Ca+2].[Ca+2].[Ca+2].[O-]P(=O)([O-])[O-].[O-]P(=O)([O-])[O-]"),
                new TestMolecule("Potassium ferricyanide", "[K+].[K+].[K+].N#C[Fe](C#N)(C#N)(C#N)(C#N)C#N"),
                new TestMolecule("Tetrahydrocannabinol", "CCCCCC1=CC(=C2C3C=C(CC(C3CC(CC2=C1O)(C)C)O)C)O")
        );

        StringBuilder report = new StringBuilder();
        report.append("<!doctype html><html><head><meta charset='UTF-8'><title>CDK depiction test</title>")
                .append("<style>body{font-family:Arial,sans-serif;background:#f7f4ec;padding:24px;} .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:18px;} .card{background:white;border:1px solid #ddd;border-radius:14px;padding:14px;box-shadow:0 4px 16px #0001;} img{width:100%;height:190px;object-fit:contain;border:1px solid #eee;border-radius:10px;} code{font-size:12px;word-break:break-all;}</style>")
                .append("</head><body><h1>CDK depiction test</h1><div class='grid'>");

        for (TestMolecule molecule : molecules) {
            String filename = slug(molecule.name()) + ".svg";
            Path output = OUTPUT_DIR.resolve(filename);

            try {
                String svg = generateSvg(molecule.smiles());
                Files.writeString(output, svg, StandardCharsets.UTF_8);

                report.append("<div class='card'><h2>")
                        .append(escapeHtml(molecule.name()))
                        .append("</h2><img src='cdk/")
                        .append(filename)
                        .append("'><p><code>")
                        .append(escapeHtml(molecule.smiles()))
                        .append("</code></p></div>");
            } catch (Exception exception) {
                report.append("<div class='card'><h2>")
                        .append(escapeHtml(molecule.name()))
                        .append("</h2><p>Error: ")
                        .append(escapeHtml(exception.getMessage()))
                        .append("</p><p><code>")
                        .append(escapeHtml(molecule.smiles()))
                        .append("</code></p></div>");
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
                .withSize(360, 260)
                .withAtomColors()
                .withFillToFit()
                .withTerminalCarbons();

        return depictionGenerator.depict(withCoordinates).toSvgStr();
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

    private record TestMolecule(String name, String smiles) {
    }
}
