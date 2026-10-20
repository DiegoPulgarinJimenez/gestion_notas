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
    void clasificar_conPromedioIgualOMayorAlUmbral_retornaAprobado() {
        assertThat(service.clasificar(3.0)).isEqualTo("APROBADO");
        assertThat(service.clasificar(4.5)).isEqualTo("APROBADO");
    }

    @Test
    void clasificar_conPromedioMenorAlUmbral_retornaReprobado() {
        assertThat(service.clasificar(2.9)).isEqualTo("REPROBADO");
        assertThat(service.clasificar(0.0)).isEqualTo("REPROBADO");
    }

    @Test
    void buscarPorCedula_cuandoExiste_retornaEstudianteConPromedioYClasificacion() {
        Estudiante estudiante = new Estudiante("1", "123", "Ana",
                List.of(new Materia("Bases de Datos", 4.0), new Materia("Redes", 3.0)), null, null);
        when(repository.findByCedula("123")).thenReturn(Optional.of(estudiante));

        Estudiante resultado = service.buscarPorCedula("123");

        assertThat(resultado.getPromedio()).isEqualTo(3.5);
        assertThat(resultado.getClasificacion()).isEqualTo("APROBADO");
    }

    @Test
    void buscarPorCedula_cuandoNoExiste_lanzaExcepcion() {
        when(repository.findByCedula("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorCedula("999"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void guardar_retornaElEstudianteConPromedioYClasificacion() {
        Estudiante estudiante = new Estudiante(null, "123", "Ana",
                List.of(new Materia("Bases de Datos", 5.0), new Materia("Redes", 4.0)), null, null);
        when(repository.save(estudiante)).thenReturn(estudiante);

        Estudiante resultado = service.guardar(estudiante);

        assertThat(resultado.getPromedio()).isEqualTo(4.5);
        assertThat(resultado.getClasificacion()).isEqualTo("APROBADO");
    }

    @Test
    void listarTodos_retornaListaEnriquecidaConPromedioYClasificacion() {
        Estudiante aprobado = new Estudiante("1", "123", "Ana",
                List.of(new Materia("Bases de Datos", 4.0)), null, null);
        Estudiante reprobado = new Estudiante("2", "456", "Luis",
                List.of(new Materia("Redes", 2.0)), null, null);
        when(repository.findAll()).thenReturn(List.of(aprobado, reprobado));

        List<Estudiante> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getPromedio()).isEqualTo(4.0);
        assertThat(resultado.get(0).getClasificacion()).isEqualTo("APROBADO");
        assertThat(resultado.get(1).getPromedio()).isEqualTo(2.0);
        assertThat(resultado.get(1).getClasificacion()).isEqualTo("REPROBADO");
    }

    @Test
    void listarTodos_cuandoNoHayEstudiantes_retornaListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.listarTodos()).isEmpty();
    }
}
