package org.chemistrylab.controller;

import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.dto.MoleculaRepresentacionDTO;
import org.chemistrylab.service.MoleculaRepresentacionService;
import org.chemistrylab.service.MoleculaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moleculas")
@CrossOrigin(origins = "http://localhost:5173")
public class MoleculaController {

    private final MoleculaService moleculaService;
    private final MoleculaRepresentacionService moleculaRepresentacionService;

    public MoleculaController(
            MoleculaService moleculaService,
            MoleculaRepresentacionService moleculaRepresentacionService
    ) {
        this.moleculaService = moleculaService;
        this.moleculaRepresentacionService = moleculaRepresentacionService;
    }

    @GetMapping("/{id}/representacion")
    public ResponseEntity<MoleculaRepresentacionDTO> obtenerRepresentacion(@PathVariable Long id) {
        return ResponseEntity.ok(moleculaRepresentacionService.obtenerRepresentacion(id));
    }

    @GetMapping
    public Page<MoleculaDTO> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String categoria,
            @RequestParam(required = false, defaultValue = "all") String familia,
            Pageable pageable
    ) {
        return moleculaService.findAllPaginado(search, categoria, familia, pageable);
    }

    @GetMapping("/{id}")
    public MoleculaDTO findById(@PathVariable Long id) {
        return moleculaService.findById(id);
    }

    @GetMapping("/pubchem/{pubchemCid}")
    public MoleculaDTO findByPubchemCid(@PathVariable Long pubchemCid) {
        return moleculaService.findByPubchemCid(pubchemCid);
    }

    @GetMapping("/nombre/{nombre}")
    public MoleculaDTO findByNombre(@PathVariable String nombre) {
        return moleculaService.findByNombre(nombre);
    }

    @GetMapping("/tipo/{tipoCompuesto}")
    public List<MoleculaDTO> findByTipoCompuesto(@PathVariable String tipoCompuesto) {
        return moleculaService.findByTipoCompuesto(tipoCompuesto);
    }
}