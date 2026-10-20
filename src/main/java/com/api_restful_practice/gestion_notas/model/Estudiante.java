package com.api_restful_practice.gestion_notas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "estudiantes")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Estudiante {

    @Id
    private String id;
    private String cedula;
    private String nombre;
    private List<Materia> materias;

    @Transient
    private Double promedio;

    @Transient
    private String clasificacion;

}
