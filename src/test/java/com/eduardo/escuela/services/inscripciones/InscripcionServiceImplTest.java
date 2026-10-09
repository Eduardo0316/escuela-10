package com.eduardo.escuela.services.inscripciones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import com.eduardo.escuela.dto.inscripciones.InscripcionRequest;
import com.eduardo.escuela.dto.inscripciones.InscripcionResponse;
import com.eduardo.escuela.entities.Alumno;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Inscripcion;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.InscripcionMapper;
import com.eduardo.escuela.repositories.AlumnoRepository;
import com.eduardo.escuela.repositories.CalificacionRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de InscripcionServiceImpl")
class InscripcionServiceImplTest {

    @Mock private AlumnoRepository alumnoRepository;
    @Mock private GrupoRepository grupoRepository;
    @Mock private CalificacionRepository calificacionRepository;
    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private InscripcionMapper inscripcionMapper;

    @InjectMocks private InscripcionServiceImpl inscripcionService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private InscripcionRequest request;
    private Inscripcion inscripcion;
    private InscripcionResponse response;
    private Alumno alumno;
    private Grupo grupo;

    private final Long ID_INSCRIPCION = 1L;
    private final Long ID_ALUMNO = 10L;
    private final Long ID_GRUPO = 20L;

    @BeforeEach
    void setUp() {
        request = new InscripcionRequest(ID_ALUMNO, ID_GRUPO);
        inscripcion = mock(Inscripcion.class);
        response = mock(InscripcionResponse.class);
        alumno = mock(Alumno.class);
        grupo = mock(Grupo.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void stubObtenerInscripcion(Inscripcion retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(inscripcionRepository), eq(ID_INSCRIPCION), eq(Inscripcion.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerInscripcionLanza(RuntimeException ex) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(inscripcionRepository), eq(ID_INSCRIPCION), eq(Inscripcion.class)))
                .thenThrow(ex);
    }

    private void stubObtenerAlumno(Alumno retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(alumnoRepository), eq(ID_ALUMNO), eq(Alumno.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerGrupo(Grupo retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(grupoRepository), eq(ID_GRUPO), eq(Grupo.class)))
                .thenReturn(retorno);
    }

    private void stubRelacionesOk() {
        stubObtenerAlumno(alumno);
        stubObtenerGrupo(grupo);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar una inscripción correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            when(inscripcionRepository.existsByAlumnoIdAndGrupoId(ID_ALUMNO, ID_GRUPO))
                    .thenReturn(false);
            stubRelacionesOk();
            when(inscripcionMapper.requestAEntidad(request, alumno, grupo)).thenReturn(inscripcion);
            when(inscripcionMapper.entidadAResponse(inscripcion)).thenReturn(response);

            InscripcionResponse resultado = inscripcionService.registrar(request);

            assertSame(response, resultado);
            verify(inscripcionRepository).save(inscripcion);
            verify(inscripcionRepository).flush();
        }

        @Test
        @DisplayName("debe validar duplicado antes de resolver relaciones")
        void registrar_debeValidarDuplicadoAntesDeResolverRelaciones() {
            when(inscripcionRepository.existsByAlumnoIdAndGrupoId(ID_ALUMNO, ID_GRUPO))
                    .thenReturn(false);
            stubRelacionesOk();
            when(inscripcionMapper.requestAEntidad(request, alumno, grupo)).thenReturn(inscripcion);
            when(inscripcionMapper.entidadAResponse(inscripcion)).thenReturn(response);

            inscripcionService.registrar(request);

            InOrder inOrder = inOrder(inscripcionRepository);
            inOrder.verify(inscripcionRepository)
                    .existsByAlumnoIdAndGrupoId(ID_ALUMNO, ID_GRUPO);
            inOrder.verify(inscripcionRepository).save(inscripcion);
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar la inscripción correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            stubObtenerInscripcion(inscripcion);
            when(inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(
                    ID_ALUMNO, ID_GRUPO, ID_INSCRIPCION)).thenReturn(false);
            stubRelacionesOk();
            when(inscripcionMapper.entidadAResponse(inscripcion)).thenReturn(response);

            InscripcionResponse resultado = inscripcionService.actualizar(request, ID_INSCRIPCION);

            assertSame(response, resultado);
            verify(inscripcion).actualizar(alumno, grupo);
            verify(inscripcionRepository).save(inscripcion);
            verify(inscripcionRepository).flush();
        }

        @Test
        @DisplayName("debe permitir mantener el mismo alumno y grupo (sin conflicto consigo misma)")
        void actualizar_conMismaCombinacion_noDebeLanzarConflicto() {
            stubObtenerInscripcion(inscripcion);
            // IdNot excluye la propia inscripción: no hay conflicto
            when(inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(
                    ID_ALUMNO, ID_GRUPO, ID_INSCRIPCION)).thenReturn(false);
            stubRelacionesOk();
            when(inscripcionMapper.entidadAResponse(inscripcion)).thenReturn(response);

            assertDoesNotThrow(() -> inscripcionService.actualizar(request, ID_INSCRIPCION));
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de inscripciones")
        void listar_debeMapearTodasLasInscripciones() {
            Inscripcion otra = mock(Inscripcion.class);
            InscripcionResponse response2 = mock(InscripcionResponse.class);
            when(inscripcionRepository.findAll()).thenReturn(List.of(inscripcion, otra));
            when(inscripcionMapper.entidadAResponse(inscripcion)).thenReturn(response);
            when(inscripcionMapper.entidadAResponse(otra)).thenReturn(response2);

            List<InscripcionResponse> resultado = inscripcionService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay inscripciones")
        void listar_sinInscripciones_debeRetornarListaVacia() {
            when(inscripcionRepository.findAll()).thenReturn(List.of());

            List<InscripcionResponse> resultado = inscripcionService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar la inscripción mapeada cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerInscripcion(inscripcion);
            when(inscripcionMapper.entidadAResponse(inscripcion)).thenReturn(response);

            InscripcionResponse resultado = inscripcionService.obtenerPorId(ID_INSCRIPCION);

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
        @DisplayName("debe eliminar la inscripción cuando no tiene calificaciones")
        void eliminar_sinCalificaciones_debeEliminar() {
            stubObtenerInscripcion(inscripcion);
            when(calificacionRepository.existsByInscripcionId(ID_INSCRIPCION)).thenReturn(false);

            inscripcionService.eliminar(ID_INSCRIPCION);

            verify(inscripcionRepository).delete(inscripcion);
            verify(inscripcionRepository).flush();
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si el alumno ya está inscrito en el grupo")
        void registrar_conInscripcionDuplicada_debeLanzarConflicto() {
            when(inscripcionRepository.existsByAlumnoIdAndGrupoId(ID_ALUMNO, ID_GRUPO))
                    .thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> inscripcionService.registrar(request)
            );
            assertEquals("El alumno ya se encuentra inscrito en este grupo", ex.getMessage());

            // No debe resolver relaciones ni guardar
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(alumnoRepository), anyLong(), eq(Alumno.class)), never());
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(grupoRepository), anyLong(), eq(Grupo.class)), never());
            verify(inscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si el alumno no existe")
        void registrar_cuandoAlumnoNoExiste_debeLanzarExcepcion() {
            when(inscripcionRepository.existsByAlumnoIdAndGrupoId(ID_ALUMNO, ID_GRUPO))
                    .thenReturn(false);
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(alumnoRepository), eq(ID_ALUMNO), eq(Alumno.class)))
                    .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> inscripcionService.registrar(request)
            );

            // No debe buscar el grupo
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(grupoRepository), anyLong(), eq(Grupo.class)), never());
            verify(inscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si el grupo no existe")
        void registrar_cuandoGrupoNoExiste_debeLanzarExcepcion() {
            when(inscripcionRepository.existsByAlumnoIdAndGrupoId(ID_ALUMNO, ID_GRUPO))
                    .thenReturn(false);
            stubObtenerAlumno(alumno);
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(grupoRepository), eq(ID_GRUPO), eq(Grupo.class)))
                    .thenThrow(new RecursoNoEncontradoException("Grupo no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> inscripcionService.registrar(request)
            );

            verify(inscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si otra inscripción ya tiene esa combinación")
        void actualizar_conCombinacionDuplicada_debeLanzarConflicto() {
            stubObtenerInscripcion(inscripcion);
            when(inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(
                    ID_ALUMNO, ID_GRUPO, ID_INSCRIPCION)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> inscripcionService.actualizar(request, ID_INSCRIPCION)
            );
            assertEquals("El alumno ya se encuentra inscrito en este grupo", ex.getMessage());

            // No debe resolver relaciones ni guardar
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(alumnoRepository), anyLong(), eq(Alumno.class)), never());
            verify(inscripcion, never()).actualizar(any(), any());
            verify(inscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si la inscripción no existe")
        void actualizar_cuandoInscripcionNoExiste_debeLanzarExcepcion() {
            stubObtenerInscripcionLanza(
                    new RecursoNoEncontradoException("Inscripción no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> inscripcionService.actualizar(request, ID_INSCRIPCION)
            );

            verify(inscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene calificaciones")
        void eliminar_conCalificaciones_debeLanzarEntidadRelacionada() {
            stubObtenerInscripcion(inscripcion);
            when(calificacionRepository.existsByInscripcionId(ID_INSCRIPCION)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> inscripcionService.eliminar(ID_INSCRIPCION)
            );
            assertEquals("No se pueden eliminar inscripciones con calificaciones asociadas",
                    ex.getMessage());

            verify(inscripcionRepository, never()).delete(any());
            verify(inscripcionRepository, never()).flush();
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar excepción si la inscripción no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerInscripcionLanza(
                    new RecursoNoEncontradoException("Inscripción no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> inscripcionService.obtenerPorId(ID_INSCRIPCION)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar excepción si la inscripción no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerInscripcionLanza(
                    new RecursoNoEncontradoException("Inscripción no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> inscripcionService.eliminar(ID_INSCRIPCION)
            );

            verify(calificacionRepository, never()).existsByInscripcionId(anyLong());
        }
    }
}