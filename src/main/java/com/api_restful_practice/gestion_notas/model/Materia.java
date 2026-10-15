package com.api_restful_practice.gestion_notas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Materia {

    private String nombre;
    private Double nota;
}
