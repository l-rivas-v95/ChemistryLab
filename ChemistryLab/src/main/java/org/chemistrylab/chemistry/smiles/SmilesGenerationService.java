package org.chemistrylab.chemistry.smiles;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmilesGenerationService {

    public SmilesGenerationResult completarSmiles(String canonicalSmiles, String isomericSmiles, String inchi) {
        if (tieneTexto(canonicalSmiles)) {
            return new SmilesGenerationResult(canonicalSmiles, isomericSmiles, false);
        }

        return generarDesdeInchi(inchi)
                .orElse(new SmilesGenerationResult(canonicalSmiles, isomericSmiles, false));
    }

    public Optional<SmilesGenerationResult> generarDesdeInchi(String inchi) {
        if (!tieneTexto(inchi)) {
            return Optional.empty();
        }

        try {
            InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
            IAtomContainer molecule = factory.getInChIToStructure(
                    inchi.trim(),
                    DefaultChemObjectBuilder.getInstance()
            ).getAtomContainer();

            if (molecule == null || molecule.isEmpty()) {
                return Optional.empty();
            }

            String canonical = new SmilesGenerator(SmiFlavor.Unique).create(molecule);
            String isomeric = new SmilesGenerator(SmiFlavor.Isomeric).create(molecule);

            if (!tieneTexto(canonical)) {
                return Optional.empty();
            }

            return Optional.of(new SmilesGenerationResult(canonical, isomeric, true));
        } catch (CDKException | RuntimeException error) {
            return Optional.empty();
        }
    }

    private boolean tieneTexto(String value) {
        return value != null && !value.isBlank();
    }
}
