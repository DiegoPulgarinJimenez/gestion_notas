package com.api_restful_practice.gestion_notas.repository;
import com.api_restful_practice.gestion_notas.model.Estudiante;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository
        extends MongoRepository<Estudiante, String> {

    Optional<Estudiante> findByCedula(String cedula);
}
