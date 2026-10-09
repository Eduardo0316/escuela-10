package com.eduardo.escuela.services.calificaciones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import com.eduardo.escuela.dto.calificaciones.CalificacionRequest;
import com.eduardo.escuela.dto.calificaciones.CalificacionResponse;
import com.eduardo.escuela.entities.Calificacion;
import com.eduardo.escuela.entities.Inscripcion;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.CalificacionMapper;
import com.eduardo.escuela.repositories.CalificacionRepository;
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
@DisplayName("Pruebas unitarias de CalificacionServiceImpl")
class CalificacionServiceImplTest {

    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private CalificacionRepository calificacionRepository;
    @Mock private CalificacionMapper calificacionMapper;

    @InjectMocks private CalificacionServiceImpl calificacionService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private CalificacionRequest request;
    private Calificacion calificacion;
    private CalificacionResponse response;
    private Inscripcion inscripcion;
    private final Long ID = 1L;
    private final Long ID_INSCRIPCION = 10L;

    @BeforeEach
    void setUp() {
        request = new CalificacionRequest(ID_INSCRIPCION, new BigDecimal("9.5"));
        calificacion = mock(Calificacion.class);
        response = mock(CalificacionResponse.class);
        inscripcion = mock(Inscripcion.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    private void stubObtenerCalificacion(Calificacion retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(calificacionRepository), eq(ID), eq(Calificacion.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerInscripcion(Inscripcion retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(inscripcionRepository), eq(ID_INSCRIPCION), eq(Inscripcion.class)))
                .thenReturn(retorno);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar una calificación correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            when(calificacionRepository.existsByInscripcionId(ID_INSCRIPCION)).thenReturn(false);
            stubObtenerInscripcion(inscripcion);
            when(calificacionMapper.requestAEntidad(request, inscripcion)).thenReturn(calificacion);
            when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(response);

            CalificacionResponse resultado = calificacionService.registrar(request);

            assertSame(response, resultado);
            verify(calificacionRepository).save(calificacion);
            verify(calificacionRepository).flush();
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar la calificación correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            stubObtenerCalificacion(calificacion);
            when(calificacionRepository.existsByInscripcionIdAndIdNot(ID_INSCRIPCION, ID))
                    .thenReturn(false);
            stubObtenerInscripcion(inscripcion);
            when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(response);

            CalificacionResponse resultado = calificacionService.actualizar(request, ID);

            assertSame(response, resultado);
            verify(calificacion).actualizar(inscripcion, new BigDecimal("9.5"));
            verify(calificacionRepository).save(calificacion);
            verify(calificacionRepository).flush();
        }

        @Test
        @DisplayName("debe permitir mantener la misma inscripción (sin conflicto consigo misma)")
        void actualizar_conMismaInscripcion_noDebeLanzarConflicto() {
            stubObtenerCalificacion(calificacion);
            // La misma inscripción que ya tiene la calificación: no hay conflicto
            when(calificacionRepository.existsByInscripcionIdAndIdNot(ID_INSCRIPCION, ID))
                    .thenReturn(false);
            stubObtenerInscripcion(inscripcion);
            when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(response);

            assertDoesNotThrow(() -> calificacionService.actualizar(request, ID));
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de calificaciones")
        void listar_debeMapearTodasLasCalificaciones() {
            Calificacion otra = mock(Calificacion.class);
            CalificacionResponse response2 = mock(CalificacionResponse.class);
            when(calificacionRepository.findAll()).thenReturn(List.of(calificacion, otra));
            when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(response);
            when(calificacionMapper.entidadAResponse(otra)).thenReturn(response2);

            List<CalificacionResponse> resultado = calificacionService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay calificaciones")
        void listar_sinCalificaciones_debeRetornarListaVacia() {
            when(calificacionRepository.findAll()).thenReturn(List.of());

            List<CalificacionResponse> resultado = calificacionService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar la calificación mapeada cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerCalificacion(calificacion);
            when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(response);

            CalificacionResponse resultado = calificacionService.obtenerPorId(ID);

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
        @DisplayName("debe eliminar la calificación correctamente")
        void eliminar_cuandoExiste_debeEliminar() {
            stubObtenerCalificacion(calificacion);

            calificacionService.eliminar(ID);

            verify(calificacionRepository).delete(calificacion);
            verify(calificacionRepository).flush();
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si la inscripción ya tiene calificación")
        void registrar_conInscripcionYaCalificada_debeLanzarConflicto() {
            when(calificacionRepository.existsByInscripcionId(ID_INSCRIPCION)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> calificacionService.registrar(request)
            );
            assertEquals("Esta inscripcion ya tiene calificacion", ex.getMessage());

            verify(calificacionRepository, never()).save(any());
            verify(calificacionRepository, never()).flush();
            // No debe buscar la inscripción si ya falló la validación
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(inscripcionRepository), anyLong(), eq(Inscripcion.class)), never());
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si otra calificación ya usa esa inscripción")
        void actualizar_conInscripcionEnOtroRegistro_debeLanzarConflicto() {
            stubObtenerCalificacion(calificacion);
            when(calificacionRepository.existsByInscripcionIdAndIdNot(ID_INSCRIPCION, ID))
                    .thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> calificacionService.actualizar(request, ID)
            );
            assertEquals("Esa inscripción ya tiene otra calificación", ex.getMessage());

            verify(calificacionRepository, never()).save(any());
            verify(calificacionRepository, never()).flush();
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si la inscripción no existe")
        void registrar_cuandoInscripcionNoExiste_debeLanzarExcepcion() {
            when(calificacionRepository.existsByInscripcionId(ID_INSCRIPCION)).thenReturn(false);
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(inscripcionRepository), eq(ID_INSCRIPCION), eq(Inscripcion.class)))
                    .thenThrow(new RecursoNoEncontradoException("Inscripción no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> calificacionService.registrar(request)
            );

            verify(calificacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si la calificación no existe")
        void actualizar_cuandoCalificacionNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(calificacionRepository), eq(ID), eq(Calificacion.class)))
                    .thenThrow(new RecursoNoEncontradoException("Calificación no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> calificacionService.actualizar(request, ID)
            );

            verify(calificacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si la inscripción no existe")
        void actualizar_cuandoInscripcionNoExiste_debeLanzarExcepcion() {
            stubObtenerCalificacion(calificacion);
            when(calificacionRepository.existsByInscripcionIdAndIdNot(ID_INSCRIPCION, ID))
                    .thenReturn(false);
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(inscripcionRepository), eq(ID_INSCRIPCION), eq(Inscripcion.class)))
                    .thenThrow(new RecursoNoEncontradoException("Inscripción no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> calificacionService.actualizar(request, ID)
            );

            verify(calificacionRepository, never()).save(any());
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar excepción si la calificación no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(calificacionRepository), eq(ID), eq(Calificacion.class)))
                    .thenThrow(new RecursoNoEncontradoException("Calificación no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> calificacionService.obtenerPorId(ID)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar excepción si la calificación no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(calificacionRepository), eq(ID), eq(Calificacion.class)))
                    .thenThrow(new RecursoNoEncontradoException("Calificación no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> calificacionService.eliminar(ID)
            );

            verify(calificacionRepository, never()).delete(any());
        }
    }
}