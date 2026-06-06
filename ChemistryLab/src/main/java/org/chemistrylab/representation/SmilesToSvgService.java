package org.chemistrylab.representation;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.depict.DepictionGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmilesToSvgService {

    private final SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    private final DepictionGenerator depictionGenerator = new DepictionGenerator()
            .withSize(260, 180)
            .withAtomColors()
            .withZoom(1.5);

    public Optional<String> renderSvg(String smiles) {
        if (smiles == null || smiles.isBlank()) {
            return Optional.empty();
        }

        try {
            IAtomContainer molecule = smilesParser.parseSmiles(smiles);

            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule(molecule);
            sdg.generateCoordinates();

            IAtomContainer laidOut = sdg.getMolecule();
            String svg = depictionGenerator.depict(laidOut).toSvgStr();

            if (svg == null || svg.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(svg);
        } catch (CDKException | RuntimeException ex) {
            return Optional.empty();
        }
    }
}
