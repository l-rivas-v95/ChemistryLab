package org.chemistrylab.representation;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InchiToSmilesService {

    private final SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.Absolute);

    public Optional<String> convert(String inchi) {
        if (inchi == null || inchi.isBlank()) {
            return Optional.empty();
        }

        try {
            InChIToStructure converter = new InChIToStructure(
                    inchi,
                    SilentChemObjectBuilder.getInstance()
            );

            IAtomContainer atomContainer = converter.getAtomContainer();
            if (atomContainer == null || atomContainer.getAtomCount() == 0) {
                return Optional.empty();
            }

            String smiles = smilesGenerator.create(atomContainer);
            if (smiles == null || smiles.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(smiles);
        } catch (CDKException | RuntimeException ex) {
            return Optional.empty();
        }
    }
}
