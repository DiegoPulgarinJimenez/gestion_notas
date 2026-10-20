package com.api_restful_practice.gestion_notas.controller;

import com.api_restful_practice.gestion_notas.model.Estudiante;
import com.api_restful_practice.gestion_notas.model.Materia;
import com.api_restful_practice.gestion_notas.service.EstudianteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstudianteController.class)
class EstudianteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstudianteService service;

    @Test
    void listar_retornaLaListaDeEstudiantes() throws Exception {
        Estudiante aprobado = new Estudiante("1", "123", "Ana",
                List.of(new Materia("Bases de Datos", 4.5)), 4.5, "APROBADO");
        Estudiante reprobado = new Estudiante("2", "456", "Luis",
                List.of(new Materia("Redes", 2.0)), 2.0, "REPROBADO");
        when(service.listarTodos()).thenReturn(List.of(aprobado, reprobado));

        mockMvc.perform(get("/api/v1/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cedula").value("123"))
                .andExpect(jsonPath("$[0].promedio").value(4.5))
                .andExpect(jsonPath("$[0].clasificacion").value("APROBADO"))
                .andExpect(jsonPath("$[1].clasificacion").value("REPROBADO"));
    }

    @Test
    void buscar_retornaEstudianteConPromedioClasificacionYEnlaces() throws Exception {
        Estudiante estudiante = new Estudiante("1", "123", "Ana",
                List.of(new Materia("Bases de Datos", 4.5)), 4.5, "APROBADO");
        when(service.buscarPorCedula("123")).thenReturn(estudiante);

        mockMvc.perform(get("/api/v1/estudiantes/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value("123"))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.promedio").value(4.5))
                .andExpect(jsonPath("$.clasificacion").value("APROBADO"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void crear_retornaEstudianteGuardadoConPromedioYClasificacion() throws Exception {
        Estudiante guardado = new Estudiante("1", "123", "Ana",
                List.of(new Materia("Bases de Datos", 4.5)), 4.5, "APROBADO");
        when(service.guardar(any(Estudiante.class))).thenReturn(guardado);

        String body = """
                {
                  "cedula": "123",
                  "nombre": "Ana",
                  "materias": [ { "nombre": "Bases de Datos", "nota": 4.5 } ]
                }
                """;

        mockMvc.perform(post("/api/v1/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value("123"))
                .andExpect(jsonPath("$.promedio").value(4.5))
                .andExpect(jsonPath("$.clasificacion").value("APROBADO"));
    }
}
