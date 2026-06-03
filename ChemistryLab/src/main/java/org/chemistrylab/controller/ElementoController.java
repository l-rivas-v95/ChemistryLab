package org.chemistrylab.controller;

import org.chemistrylab.dto.ElementoDTO;
import org.chemistrylab.service.ElementoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elementos")
@CrossOrigin(origins = "http://localhost:5173")
public class ElementoController {

    private final ElementoService elementoService;

    public ElementoController(ElementoService elementoService) {
        this.elementoService = elementoService;
    }

    @GetMapping
    public List<ElementoDTO> findAll() {
        return elementoService.findAll();
    }

    @GetMapping("/{id}")
    public ElementoDTO findById(@PathVariable Long id) {
        return elementoService.findById(id);
    }

    @GetMapping("/simbolo/{simbolo}")
    public ElementoDTO findBySimbolo(@PathVariable String simbolo) {
        return elementoService.findBySimbolo(simbolo);
    }

    @GetMapping("/numero/{numeroAtomico}")
    public ElementoDTO findByNumeroAtomico(@PathVariable Integer numeroAtomico) {
        return elementoService.findByNumeroAtomico(numeroAtomico);
    }
}