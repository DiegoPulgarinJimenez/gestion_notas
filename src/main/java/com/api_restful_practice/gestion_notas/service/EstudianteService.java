package com.api_restful_practice.gestion_notas.service;

import com.api_restful_practice.gestion_notas.model.Estudiante;
import com.api_restful_practice.gestion_notas.model.Materia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api_restful_practice.gestion_notas.repository.EstudianteRepository;

import java.util.List;

@Service
public class EstudianteService {

    @Autowired
    private final EstudianteRepository repository;

    public EstudianteService(EstudianteRepository repository) {
        this.repository = repository;
    }

    public Estudiante guardar(Estudiante estudiante) {
        Estudiante guardado = repository.save(estudiante);
        guardado.setPromedio(calcularPromedio(guardado.getMaterias()));
        return guardado;
    }

    public Estudiante buscarPorCedula(String cedula) {
        Estudiante estudiante = repository.findByCedula(cedula).orElseThrow();
        estudiante.setPromedio(calcularPromedio(estudiante.getMaterias()));
        return estudiante;
    }

    public double calcularPromedio(List<Materia> materias) {
        if (materias == null || materias.isEmpty()) {
            return 0.0;
        }

        double suma = 0;
        for (Materia materia : materias) {
            suma += materia.getNota();
        }

        return Math.round((suma / materias.size()) * 10.0) / 10.0;
    }
}
