package org.chemistrylab.service;

import lombok.RequiredArgsConstructor;
import org.chemistrylab.dto.MoleculaImportResponse;
import org.chemistrylab.entity.MoleculaEntity;
import org.chemistrylab.pubchem.PubChemClient;
import org.chemistrylab.pubchem.PubChemCompoundData;
import org.chemistrylab.repository.MoleculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoleculaImportService {

    private final MoleculaRepository moleculaRepository;
    private final PubChemClient pubChemClient;

    @Transactional
    public MoleculaImportResponse importar(String query) {
        String busqueda = normalizarQuery(query);

        MoleculaImportResponse existenteEnBd = buscarExistenteEnBd(busqueda);
        if (existenteEnBd != null) {
            return existenteEnBd;
        }

        Long cid = pubChemClient.buscarCid(busqueda)
                .orElseThrow(() -> new RuntimeException("No se encontró la molécula en PubChem"));

        return moleculaRepository.findByPubchemCid(cid)
                .map(this::respuestaYaExiste)
                .orElseGet(() -> descargarGuardarYResponder(cid));
    }

    private MoleculaImportResponse buscarExistenteEnBd(String query) {
        if (query.matches("\\d+")) {
            Long cid = Long.parseLong(query);
            return moleculaRepository.findByPubchemCid(cid)
                    .map(this::respuestaYaExiste)
                    .orElse(null);
        }

        return moleculaRepository.findByNombreIgnoreCase(query)
                .map(this::respuestaYaExiste)
                .orElse(null);
    }

    private MoleculaImportResponse descargarGuardarYResponder(Long cid) {
        PubChemCompoundData data = pubChemClient.descargarCompuesto(cid);
        MoleculaEntity entity = construirEntidad(data);
        MoleculaEntity guardada = moleculaRepository.save(entity);

        return MoleculaImportResponse.builder()
                .status("IMPORTED")
                .message("Molécula importada correctamente")
                .id(guardada.getId())
                .pubchemCid(guardada.getPubchemCid())
                .nombre(guardada.getNombre())
                .formula(guardada.getFormula())
                .build();
    }

    private MoleculaEntity construirEntidad(PubChemCompoundData data) {
        return MoleculaEntity.builder()
                .pubchemCid(data.getPubchemCid())
                .nombre(data.getNombre())
                .formula(data.getFormula())
                .masaMolecular(data.getMasaMolecular())
                .nombreIupac(data.getNombreIupac())
                .descripcion(data.getDescripcion())
                .tipoCompuesto(null)
                .estadoFisico(null)
                .carga(data.getCarga())
                .imagen2d(data.getImagen2d())
                .modelo3dUrl(data.getModelo3dUrl())
                .puntoFusion(data.getPuntoFusion())
                .puntoEbullicion(data.getPuntoEbullicion())
                .densidad(data.getDensidad())
                .solubilidad(data.getSolubilidad())
                .ph(data.getPh())
                .riesgos(data.getRiesgos())
                .usos(data.getUsos())
                .sinonimos(data.getSinonimos())
                .canonicalSmiles(data.getCanonicalSmiles())
                .isomericSmiles(data.getIsomericSmiles())
                .inchi(data.getInchi())
                .inchiKey(data.getInchiKey())
                .xlogp(data.getXlogp())
                .tpsa(data.getTpsa())
                .donadoresH(data.getDonadoresH())
                .aceptoresH(data.getAceptoresH())
                .enlacesRotables(data.getEnlacesRotables())
                .atomosPesados(data.getAtomosPesados())
                .complejidad(data.getComplejidad())
                .build();
    }

    private MoleculaImportResponse respuestaYaExiste(MoleculaEntity entity) {
        return MoleculaImportResponse.builder()
                .status("ALREADY_EXISTS")
                .message("La molécula ya existe en la base de datos")
                .id(entity.getId())
                .pubchemCid(entity.getPubchemCid())
                .nombre(entity.getNombre())
                .formula(entity.getFormula())
                .build();
    }

    private String normalizarQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new RuntimeException("La búsqueda no puede estar vacía");
        }

        return query.trim();
    }
}
