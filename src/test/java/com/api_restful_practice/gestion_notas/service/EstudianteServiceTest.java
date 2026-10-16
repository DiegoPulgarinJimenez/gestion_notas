package com.api_restful_practice.gestion_notas.service;

import com.api_restful_practice.gestion_notas.model.Estudiante;
import com.api_restful_practice.gestion_notas.model.Materia;
import com.api_restful_practice.gestion_notas.repository.EstudianteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository repository;

    @InjectMocks
    private EstudianteService service;

    @Test
    void calcularPromedio_cuandoListaEsNula_retornaCero() {
        assertThat(service.calcularPromedio(null)).isZero();
    }

    @Test
    void calcularPromedio_cuandoListaEstaVacia_retornaCero() {
        assertThat(service.calcularPromedio(List.of())).isZero();
    }

    @Test
    void calcularPromedio_conUnaNota_retornaEsaNota() {
        List<Materia> materias = List.of(new Materia("Bases de Datos", 4.0));

        assertThat(service.calcularPromedio(materias)).isEqualTo(4.0);
    }

    @Test
    void calcularPromedio_conVariosValores_promediaYRedondeaUnDecimal() {
        List<Materia> materias = List.of(
                new Materia("Bases de Datos", 4.0),
                new Materia("Arquitectura", 3.0),
                new Materia("Redes", 4.0)
        );

        assertThat(service.calcularPromedio(materias)).isEqualTo(3.7);
    }

    @Test
    void buscarPorCedula_cuandoExiste_retornaEstudianteConPromedio() {
        Estudiante estudiante = new Estudiante("1", "123", "Ana",
                List.of(new Materia("Bases de Datos", 4.0), new Materia("Redes", 3.0)), null);
        when(repository.findByCedula("123")).thenReturn(Optional.of(estudiante));

        Estudiante resultado = service.buscarPorCedula("123");

        assertThat(resultado.getPromedio()).isEqualTo(3.5);
    }

    @Test
    void buscarPorCedula_cuandoNoExiste_lanzaExcepcion() {
        when(repository.findByCedula("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorCedula("999"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void guardar_retornaElEstudianteConPromedio() {
        Estudiante estudiante = new Estudiante(null, "123", "Ana",
                List.of(new Materia("Bases de Datos", 5.0), new Materia("Redes", 4.0)), null);
        when(repository.save(estudiante)).thenReturn(estudiante);

        Estudiante resultado = service.guardar(estudiante);

        assertThat(resultado.getPromedio()).isEqualTo(4.5);
    }
}
