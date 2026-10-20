package com.api_restful_practice.gestion_notas.service;

import com.api_restful_practice.gestion_notas.model.Estudiante;
import com.api_restful_practice.gestion_notas.model.Materia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api_restful_practice.gestion_notas.repository.EstudianteRepository;

import java.util.List;

@Service
public class EstudianteService {

    private static final double NOTA_MINIMA = 3.0;

    @Autowired
    private final EstudianteRepository repository;

    public EstudianteService(EstudianteRepository repository) {
        this.repository = repository;
    }

    public Estudiante guardar(Estudiante estudiante) {
        return enriquecer(repository.save(estudiante));
    }

    public Estudiante buscarPorCedula(String cedula) {
        Estudiante estudiante = repository.findByCedula(cedula).orElseThrow();
        return enriquecer(estudiante);
    }

    public List<Estudiante> listarTodos() {
        return repository.findAll().stream()
                .map(this::enriquecer)
                .toList();
    }

    public double calcularPromedio(List<Materia> materias) {
        if (esVacia(materias)) {
            return 0.0;
        }

        return redondear(sumarNotas(materias) / materias.size());
    }

    public String clasificar(double promedio) {
        if (promedio >= NOTA_MINIMA) {
            return "APROBADO";
        }
        return "REPROBADO";
    }

    private Estudiante enriquecer(Estudiante estudiante) {
        double promedio = calcularPromedio(estudiante.getMaterias());
        estudiante.setPromedio(promedio);
        estudiante.setClasificacion(clasificar(promedio));
        return estudiante;
    }

    private boolean esVacia(List<Materia> materias) {
        return materias == null || materias.isEmpty();
    }

    private double sumarNotas(List<Materia> materias) {
        double suma = 0;
        for (Materia materia : materias) {
            suma += materia.getNota();
        }
        return suma;
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
