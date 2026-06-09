package org.chemistrylab.chemistry.smiles;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmilesGenerationService {

    private final SmilesGenerator canonicalSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
    private final SmilesGenerator isomericSmilesGenerator = new SmilesGenerator(SmiFlavor.Isomeric | SmiFlavor.Unique);

    public SmilesGenerationResult completarSmiles(String canonicalSmiles, String isomericSmiles, String inchi) {
        if (tieneTexto(canonicalSmiles) && tieneTexto(isomericSmiles)) {
            return new SmilesGenerationResult(canonicalSmiles, isomericSmiles, false);
        }

        Optional<SmilesGenerationResult> generatedFromInchi = generarDesdeInchi(inchi);
        if (generatedFromInchi.isEmpty()) {
            return new SmilesGenerationResult(canonicalSmiles, isomericSmiles, false);
        }

        SmilesGenerationResult generated = generatedFromInchi.get();
        return new SmilesGenerationResult(
                tieneTexto(canonicalSmiles) ? canonicalSmiles : generated.canonicalSmiles(),
                tieneTexto(isomericSmiles) ? isomericSmiles : generated.isomericSmiles(),
                true
        );
    }

    public Optional<SmilesGenerationResult> generarDesdeInchi(String inchi) {
        if (!tieneTexto(inchi)) {
            return Optional.empty();
        }

        try {
            InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
            InChIToStructure inchiToStructure = factory.getInChIToStructure(
                    inchi.trim(),
                    SilentChemObjectBuilder.getInstance()
            );

            IAtomContainer molecule = inchiToStructure.getAtomContainer();
            if (molecule == null || molecule.getAtomCount() == 0) {
                return Optional.empty();
            }

            String canonicalSmiles = canonicalSmilesGenerator.create(molecule);
            String isomericSmiles = isomericSmilesGenerator.create(molecule);

            if (!tieneTexto(canonicalSmiles)) {
                return Optional.empty();
            }

            return Optional.of(new SmilesGenerationResult(
                    canonicalSmiles,
                    tieneTexto(isomericSmiles) ? isomericSmiles : canonicalSmiles,
                    true
            ));
        } catch (CDKException | RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private boolean tieneTexto(String value) {
        return value != null && !value.isBlank();
    }
}
