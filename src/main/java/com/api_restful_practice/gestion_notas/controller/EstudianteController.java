package com.api_restful_practice.gestion_notas.controller;

import com.api_restful_practice.gestion_notas.model.Estudiante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api_restful_practice.gestion_notas.service.EstudianteService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/estudiantes")
@CrossOrigin(origins = "http://localhost:5173")
public class EstudianteController {

    @Autowired
    private final EstudianteService service;

    public EstudianteController(EstudianteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante estudiante){

        return ResponseEntity.ok(service.guardar(estudiante));
    }

    @GetMapping("/{cedula}")
    public EntityModel<Estudiante> buscar(@PathVariable String cedula){
        Estudiante estudiante =  service.buscarPorCedula(cedula);

        return EntityModel.of(estudiante,
                linkTo(methodOn(EstudianteController.class).buscar(cedula)).withSelfRel(),
                linkTo(methodOn(EstudianteController.class).crear(estudiante)).withRel("crear"));
    }

}
