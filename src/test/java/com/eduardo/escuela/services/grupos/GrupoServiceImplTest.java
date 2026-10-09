package com.eduardo.escuela.services.grupos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import com.eduardo.escuela.dto.grupo.GrupoRequest;
import com.eduardo.escuela.dto.grupo.GrupoResponse;
import com.eduardo.escuela.entities.Aula;
import com.eduardo.escuela.entities.Curso;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Maestro;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.GrupoMapper;
import com.eduardo.escuela.repositories.AulaRepository;
import com.eduardo.escuela.repositories.CursoRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.repositories.HorarioRepository;
import com.eduardo.escuela.repositories.InscripcionRepository;
import com.eduardo.escuela.repositories.MaestroRepository;
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
@DisplayName("Pruebas unitarias de GrupoServiceImpl")
class GrupoServiceImplTest {

    @Mock private GrupoMapper grupoMapper;
    @Mock private AulaRepository aulaRepository;
    @Mock private MaestroRepository maestroRepository;
    @Mock private CursoRepository cursoRepository;
    @Mock private GrupoRepository grupoRepository;
    @Mock private HorarioRepository horarioRepository;
    @Mock private InscripcionRepository inscripcionRepository;

    @InjectMocks private GrupoServiceImpl grupoService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private GrupoRequest request;
    private Grupo grupo;
    private GrupoResponse response;
    private Curso curso;
    private Maestro maestro;
    private Aula aula;

    private final Long ID_GRUPO = 1L;
    private final Long ID_CURSO = 10L;
    private final Long ID_MAESTRO = 20L;
    private final Long ID_AULA = 30L;
    private final String PERIODO = "2024-1";

    @BeforeEach
    void setUp() {
        request = new GrupoRequest(ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO);
        grupo = mock(Grupo.class);
        response = mock(GrupoResponse.class);
        curso = mock(Curso.class);
        maestro = mock(Maestro.class);
        aula = mock(Aula.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    // ------------------------------------------------------------------
    // Helpers para stubbear ServiceUtils según el repositorio
    // ------------------------------------------------------------------

    private void stubObtenerGrupo(Grupo retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(grupoRepository), eq(ID_GRUPO), eq(Grupo.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerGrupoLanza(RuntimeException ex) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(grupoRepository), eq(ID_GRUPO), eq(Grupo.class)))
                .thenThrow(ex);
    }

    private void stubObtenerCurso(Curso retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(cursoRepository), eq(ID_CURSO), eq(Curso.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerMaestro(Maestro retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(maestroRepository), eq(ID_MAESTRO), eq(Maestro.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerAula(Aula retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(aulaRepository), eq(ID_AULA), eq(Aula.class)))
                .thenReturn(retorno);
    }

    /** Stub combinado: curso, maestro y aula se resuelven correctamente. */
    private void stubRelacionesOk() {
        stubObtenerCurso(curso);
        stubObtenerMaestro(maestro);
        stubObtenerAula(aula);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar un grupo correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            stubRelacionesOk();
            when(grupoMapper.requestAEntidad(request, curso, maestro, aula)).thenReturn(grupo);
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO)).thenReturn(false);
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);

            GrupoResponse resultado = grupoService.registrar(request);

            assertSame(response, resultado);
            verify(grupoRepository).save(grupo);
            verify(grupoRepository).flush();
        }

        @Test
        @DisplayName("debe resolver curso, maestro y aula antes de mapear")
        void registrar_debeResolverRelacionesAntesDeMapear() {
            stubRelacionesOk();
            when(grupoMapper.requestAEntidad(request, curso, maestro, aula)).thenReturn(grupo);
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO)).thenReturn(false);
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);

            grupoService.registrar(request);

            // Verificamos que se resolvieron las 3 entidades
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(cursoRepository), eq(ID_CURSO), eq(Curso.class)));
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(maestroRepository), eq(ID_MAESTRO), eq(Maestro.class)));
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(aulaRepository), eq(ID_AULA), eq(Aula.class)));
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar el grupo correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            stubObtenerGrupo(grupo);
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO, ID_GRUPO)).thenReturn(false);
            stubRelacionesOk();
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);

            GrupoResponse resultado = grupoService.actualizar(request, ID_GRUPO);

            assertSame(response, resultado);
            verify(grupo).actualizar(curso, maestro, aula, PERIODO);
            verify(grupoRepository).save(grupo);
            verify(grupoRepository).flush();
        }

        @Test
        @DisplayName("debe obtener el grupo antes de validar unicidad (según el orden actual)")
        void actualizar_obtieneGrupoPrimero() {
            stubObtenerGrupo(grupo);
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO, ID_GRUPO)).thenReturn(false);
            stubRelacionesOk();
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);

            grupoService.actualizar(request, ID_GRUPO);

            InOrder inOrder = inOrder(grupoRepository);
            // El grupo debe existir primero, luego se valida unicidad, luego se guarda
            inOrder.verify(grupoRepository).existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO, ID_GRUPO);
            inOrder.verify(grupoRepository).save(grupo);
        }

        @Test
        @DisplayName("debe permitir mantener la misma combinación del propio grupo (sin conflicto consigo mismo)")
        void actualizar_conMismaCombinacion_noDebeLanzarConflicto() {
            stubObtenerGrupo(grupo);
            // IdNot excluye el propio ID, así que no hay conflicto
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO, ID_GRUPO)).thenReturn(false);
            stubRelacionesOk();
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);

            assertDoesNotThrow(() -> grupoService.actualizar(request, ID_GRUPO));
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de grupos")
        void listar_debeMapearTodosLosGrupos() {
            Grupo otro = mock(Grupo.class);
            GrupoResponse response2 = mock(GrupoResponse.class);
            when(grupoRepository.findAll()).thenReturn(List.of(grupo, otro));
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);
            when(grupoMapper.entidadAResponse(otro)).thenReturn(response2);

            List<GrupoResponse> resultado = grupoService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay grupos")
        void listar_sinGrupos_debeRetornarListaVacia() {
            when(grupoRepository.findAll()).thenReturn(List.of());

            List<GrupoResponse> resultado = grupoService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el grupo mapeado cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerGrupo(grupo);
            when(grupoMapper.entidadAResponse(grupo)).thenReturn(response);

            GrupoResponse resultado = grupoService.obtenerPorId(ID_GRUPO);

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
        @DisplayName("debe eliminar el grupo cuando no tiene horarios ni inscripciones")
        void eliminar_sinDependencias_debeEliminar() {
            when(horarioRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            when(inscripcionRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            stubObtenerGrupo(grupo);

            grupoService.eliminar(ID_GRUPO);

            verify(grupoRepository).delete(grupo);
            verify(grupoRepository).flush();
        }

        @Test
        @DisplayName("debe validar dependencias antes de buscar el grupo")
        void eliminar_debeValidarDependenciasAntesDeBuscar() {
            when(horarioRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            when(inscripcionRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            stubObtenerGrupo(grupo);

            grupoService.eliminar(ID_GRUPO);

            InOrder inOrder = inOrder(horarioRepository, inscripcionRepository, grupoRepository);
            inOrder.verify(horarioRepository).existsByGrupoId(ID_GRUPO);
            inOrder.verify(inscripcionRepository).existsByGrupoId(ID_GRUPO);
            inOrder.verify(grupoRepository).delete(grupo);
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si el grupo ya existe")
        void registrar_conGrupoDuplicado_debeLanzarConflicto() {
            stubRelacionesOk();
            when(grupoMapper.requestAEntidad(request, curso, maestro, aula)).thenReturn(grupo);
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> grupoService.registrar(request)
            );
            assertEquals("Ese grupo ya existe", ex.getMessage());

            verify(grupoRepository, never()).save(any());
            verify(grupoRepository, never()).flush();
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si el curso no existe")
        void registrar_cuandoCursoNoExiste_debeLanzarExcepcion() {
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(cursoRepository), eq(ID_CURSO), eq(Curso.class)))
                    .thenThrow(new RecursoNoEncontradoException("Curso no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> grupoService.registrar(request)
            );

            verify(grupoRepository, never()).save(any());
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si el maestro no existe")
        void registrar_cuandoMaestroNoExiste_debeLanzarExcepcion() {
            stubObtenerCurso(curso);
            serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                            eq(maestroRepository), eq(ID_MAESTRO), eq(Maestro.class)))
                    .thenThrow(new RecursoNoEncontradoException("Maestro no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> grupoService.registrar(request)
            );

            // No llegó a buscar el aula
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(aulaRepository), anyLong(), eq(Aula.class)), never());
            verify(grupoRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si la combinación ya existe en otro grupo")
        void actualizar_conCombinacionDuplicada_debeLanzarConflicto() {
            stubObtenerGrupo(grupo);
            when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                    ID_CURSO, ID_MAESTRO, ID_AULA, PERIODO, ID_GRUPO)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> grupoService.actualizar(request, ID_GRUPO)
            );
            assertTrue(ex.getMessage().contains("Ya existe otro grupo"));

            // No debió resolver las relaciones ni guardar
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(cursoRepository), anyLong(), eq(Curso.class)), never());
            verify(grupoRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si el grupo no existe")
        void actualizar_cuandoGrupoNoExiste_debeLanzarExcepcion() {
            stubObtenerGrupoLanza(new RecursoNoEncontradoException("Grupo no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> grupoService.actualizar(request, ID_GRUPO)
            );

            verify(grupoRepository, never()).save(any());
        }

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene horarios")
        void eliminar_conHorarios_debeLanzarEntidadRelacionada() {
            when(horarioRepository.existsByGrupoId(ID_GRUPO)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> grupoService.eliminar(ID_GRUPO)
            );
            assertEquals("No se puede eliminar un grupo con horarios asociados", ex.getMessage());

            // No debe consultar inscripciones ni buscar el grupo
            verify(inscripcionRepository, never()).existsByGrupoId(anyLong());
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(grupoRepository), anyLong(), eq(Grupo.class)), never());
            verify(grupoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene inscripciones")
        void eliminar_conInscripciones_debeLanzarEntidadRelacionada() {
            when(horarioRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            when(inscripcionRepository.existsByGrupoId(ID_GRUPO)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> grupoService.eliminar(ID_GRUPO)
            );
            assertEquals("No se puede eliminar un grupo con inscripciones asociadas", ex.getMessage());

            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(grupoRepository), anyLong(), eq(Grupo.class)), never());
            verify(grupoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar excepción si el grupo no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerGrupoLanza(new RecursoNoEncontradoException("Grupo no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> grupoService.obtenerPorId(ID_GRUPO)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar excepción si el grupo no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            when(horarioRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            when(inscripcionRepository.existsByGrupoId(ID_GRUPO)).thenReturn(false);
            stubObtenerGrupoLanza(new RecursoNoEncontradoException("Grupo no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> grupoService.eliminar(ID_GRUPO)
            );

            verify(grupoRepository, never()).delete(any());
        }
    }
}