package com.api_restful_practice.gestion_notas.service;

import com.api_restful_practice.gestion_notas.model.Estudiante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api_restful_practice.gestion_notas.repository.EstudianteRepository;

@Service
public class EstudianteService {

    @Autowired
    private final EstudianteRepository repository;

    public EstudianteService(EstudianteRepository repository) {
        this.repository = repository;
    }

    public Estudiante guardar(Estudiante estudiante) {
        return repository.save(estudiante);
    }

    public Estudiante buscarPorCedula(String cedula) {
        return repository.findByCedula(cedula).orElseThrow();
    }
}
