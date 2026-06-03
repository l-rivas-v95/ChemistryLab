package org.chemistrylab.controller;

import org.chemistrylab.dto.MoleculaDTO;
import org.chemistrylab.service.MoleculaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moleculas")
@CrossOrigin(origins = "http://localhost:5173")
public class MoleculaController {

    private final MoleculaService moleculaService;

    public MoleculaController(MoleculaService moleculaService) {
        this.moleculaService = moleculaService;
    }

    @GetMapping
    public List<MoleculaDTO> findAll() {
        return moleculaService.findAll();
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