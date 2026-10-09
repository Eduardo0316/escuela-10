package com.eduardo.escuela.services.alumnos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.eduardo.escuela.dto.alumnos.AlumnoRequest;
import com.eduardo.escuela.dto.alumnos.AlumnoResponse;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.AlumnoMapper;
import com.eduardo.escuela.repositories.AlumnoRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de AlumnoServiceImpl")
class AlumnoServiceImplTest {

    @Mock private AlumnoRepository alumnoRepository;
    @Mock private AlumnoMapper alumnoMapper;
    @Mock private InscripcionRepository inscripcionRepository;

    @InjectMocks private AlumnoServiceImpl alumnoService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private AlumnoRequest request;
    private Alumno alumno;
    private AlumnoResponse response;
    private final Long ID = 1L;

    @BeforeEach
    void setUp() {
        request = new AlumnoRequest("Eduardo", "García", "López");
        alumno = mock(Alumno.class);
        response = mock(AlumnoResponse.class);

        // Mock estático de ServiceUtils.obtenerEntidadOException
        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    // Helper: simula que ServiceUtils devuelve el alumno
    private void stubObtenerAlumno(Alumno retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(alumnoRepository), eq(ID), eq(Alumno.class)))
                .thenReturn(retorno);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar un alumno correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            when(alumnoRepository.generarEmail(anyString(), anyString(), anyString()))
                    .thenReturn("eduardo.garcia@escuela.com");
            when(alumnoRepository.generarMatricula(anyString(), anyString(), anyString()))
                    .thenReturn("A123456789");
            when(alumnoMapper.requestAEntidad(eq(request),
                    eq("eduardo.garcia@escuela.com"), eq("A123456789")))
                    .thenReturn(alumno);
            when(alumnoMapper.entidadAResponse(alumno)).thenReturn(response);

            AlumnoResponse resultado = alumnoService.registrar(request);

            assertSame(response, resultado);
            verify(alumnoRepository).save(alumno);
            verify(alumnoRepository).flush();
            verify(alumnoMapper).requestAEntidad(request,
                    "eduardo.garcia@escuela.com", "A123456789");
        }

        @Test
        @DisplayName("debe generar email y matrícula con los datos recortados")
        void registrar_debePasarDatosRecortadosAlRepositorio() {
            AlumnoRequest conEspacios = new AlumnoRequest("  Eduardo  ", "  García  ", "  López  ");
            when(alumnoRepository.generarEmail("Eduardo", "García", "López"))
                    .thenReturn("email@escuela.com");
            when(alumnoRepository.generarMatricula("Eduardo", "García", "López"))
                    .thenReturn("A123456789");
            when(alumnoMapper.requestAEntidad(any(), anyString(), anyString()))
                    .thenReturn(alumno);
            when(alumnoMapper.entidadAResponse(alumno)).thenReturn(response);

            alumnoService.registrar(conEspacios);

            verify(alumnoRepository).generarEmail("Eduardo", "García", "López");
            verify(alumnoRepository).generarMatricula("Eduardo", "García", "López");
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar el alumno cuando hay cambios en los datos")
        void actualizar_conCambios_debeActualizarYGuardar() {
            stubObtenerAlumno(alumno);
            when(alumno.cambioEnDatos("Eduardo", "García", "López")).thenReturn(true);
            when(alumnoRepository.generarEmail("Eduardo", "García", "López"))
                    .thenReturn("eduardo.garcia@escuela.com");
            when(alumnoRepository.generarMatricula("Eduardo", "García", "López"))
                    .thenReturn("A123456789");
            when(alumnoMapper.entidadAResponse(alumno)).thenReturn(response);

            AlumnoResponse resultado = alumnoService.actualizar(request, ID);

            assertSame(response, resultado);
            verify(alumno).actualizar("Eduardo", "García", "López",
                    "eduardo.garcia@escuela.com", "A123456789");
            verify(alumnoRepository).save(alumno);
            verify(alumnoRepository).flush();
        }

        @Test
        @DisplayName("no debe actualizar ni guardar si no hay cambios en los datos")
        void actualizar_sinCambios_noDebeGuardarNiActualizar() {
            stubObtenerAlumno(alumno);
            when(alumno.cambioEnDatos("Eduardo", "García", "López")).thenReturn(false);
            when(alumnoMapper.entidadAResponse(alumno)).thenReturn(response);

            AlumnoResponse resultado = alumnoService.actualizar(request, ID);

            assertSame(response, resultado);
            verify(alumno, never()).actualizar(any(), any(), any(), any(), any());
            verify(alumnoRepository, never()).save(any());
            verify(alumnoRepository, never()).flush();
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de alumnos")
        void listar_debeMapearTodosLosAlumnos() {
            Alumno otro = mock(Alumno.class);
            AlumnoResponse response2 = mock(AlumnoResponse.class);
            when(alumnoRepository.findAll()).thenReturn(List.of(alumno, otro));
            when(alumnoMapper.entidadAResponse(alumno)).thenReturn(response);
            when(alumnoMapper.entidadAResponse(otro)).thenReturn(response2);

            List<AlumnoResponse> resultado = alumnoService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay alumnos")
        void listar_sinAlumnos_debeRetornarListaVacia() {
            when(alumnoRepository.findAll()).thenReturn(List.of());

            List<AlumnoResponse> resultado = alumnoService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el alumno mapeado cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerAlumno(alumno);
            when(alumnoMapper.entidadAResponse(alumno)).thenReturn(response);

            AlumnoResponse resultado = alumnoService.obtenerPorId(ID);

            assertSame(response, resultado);
        }
    }

    // ============================================================
    // HAPPY PATHS - eliminar()
    // ============================================================

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("debe eliminar el alumno cuando no tiene inscripciones")
        void eliminar_sinInscripciones_debeEliminar() {
            stubObtenerAlumno(alumno);
            when(inscripcionRepository.existsByAlumnoId(ID)).thenReturn(false);

            alumnoService.eliminar(ID);

            verify(alumnoRepository).delete(alumno);
            verify(alumnoRepository).flush();
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene inscripciones")
        void eliminar_conInscripciones_debeLanzarExcepcion() {
            stubObtenerAlumno(alumno);
            when(inscripcionRepository.existsByAlumnoId(ID)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> alumnoService.eliminar(ID)
            );
            assertTrue(ex.getMessage().contains("No se puede eliminar un alumno"));

            verify(alumnoRepository, never()).delete(any());
            verify(alumnoRepository, never()).flush();
        }

        @Test
        @DisplayName("actualizar: debe propagar la excepción si el alumno no existe")
        void actualizar_cuandoNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(alumnoRepository), eq(ID), eq(Alumno.class)))
                    .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> alumnoService.actualizar(request, ID)
            );

            verify(alumnoRepository, never()).save(any());
            verify(alumnoRepository, never()).flush();
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar la excepción si el alumno no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(alumnoRepository), eq(ID), eq(Alumno.class)))
                    .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> alumnoService.obtenerPorId(ID)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar la excepción si el alumno no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(alumnoRepository), eq(ID), eq(Alumno.class)))
                    .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> alumnoService.eliminar(ID)
            );

            verify(inscripcionRepository, never()).existsByAlumnoId(anyLong());
        }
    }
}