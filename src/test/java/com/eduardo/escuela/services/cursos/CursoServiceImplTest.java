package com.eduardo.escuela.services.cursos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import com.eduardo.escuela.dto.cursos.CursoRequest;
import com.eduardo.escuela.dto.cursos.CursoResponse;
import com.eduardo.escuela.entities.Curso;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.CursoMapper;
import com.eduardo.escuela.repositories.CursoRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
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
@DisplayName("Pruebas unitarias de CursoServiceImpl")
class CursoServiceImplTest {

    @Mock private CursoMapper cursoMapper;
    @Mock private CursoRepository cursoRepository;
    @Mock private GrupoRepository grupoRepository;

    @InjectMocks private CursoServiceImpl cursoService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private CursoRequest request;
    private Curso curso;
    private CursoResponse response;
    private final Long ID = 1L;

    @BeforeEach
    void setUp() {
        request = new CursoRequest("Matemáticas", "Álgebra básica", 5);
        curso = mock(Curso.class);
        response = mock(CursoResponse.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    private void stubObtenerCurso(Curso retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(cursoRepository), eq(ID), eq(Curso.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerCursoLanzaExcepcion(RuntimeException ex) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(cursoRepository), eq(ID), eq(Curso.class)))
                .thenThrow(ex);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar un curso correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            when(cursoMapper.requestAEntidad(request)).thenReturn(curso);
            when(cursoRepository.existsByNombre("Matemáticas")).thenReturn(false);
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);

            CursoResponse resultado = cursoService.registrar(request);

            assertSame(response, resultado);
            verify(cursoRepository).save(curso);
            verify(cursoRepository).flush();
        }

        @Test
        @DisplayName("debe validar el nombre del request antes de guardar")
        void registrar_debeValidarNombreDelRequest() {
            when(cursoMapper.requestAEntidad(request)).thenReturn(curso);
            when(cursoRepository.existsByNombre("Matemáticas")).thenReturn(false);
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);

            cursoService.registrar(request);

            verify(cursoRepository).existsByNombre("Matemáticas");
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar el curso correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            when(cursoRepository.existsByNombreAndIdNot("Matemáticas", ID)).thenReturn(false);
            stubObtenerCurso(curso);
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);

            CursoResponse resultado = cursoService.actualizar(request, ID);

            assertSame(response, resultado);
            verify(curso).actualizar("Matemáticas", "Álgebra básica", 5);
            verify(cursoRepository).save(curso);
            verify(cursoRepository).flush();
        }

        @Test
        @DisplayName("debe validar unicidad antes de buscar el curso")
        void actualizar_debeValidarUnicidadAntesDeBuscar() {
            when(cursoRepository.existsByNombreAndIdNot("Matemáticas", ID)).thenReturn(false);
            stubObtenerCurso(curso);
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);

            cursoService.actualizar(request, ID);

            InOrder inOrder = inOrder(cursoRepository);
            inOrder.verify(cursoRepository).existsByNombreAndIdNot("Matemáticas", ID);
            inOrder.verify(cursoRepository).save(curso);
        }

        @Test
        @DisplayName("debe permitir mantener el mismo nombre del propio curso (sin conflicto consigo mismo)")
        void actualizar_conMismoNombre_noDebeLanzarConflicto() {
            // existsByNombreAndIdNot excluye el propio ID: no hay conflicto
            when(cursoRepository.existsByNombreAndIdNot("Matemáticas", ID)).thenReturn(false);
            stubObtenerCurso(curso);
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);

            assertDoesNotThrow(() -> cursoService.actualizar(request, ID));
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de cursos")
        void listar_debeMapearTodosLosCursos() {
            Curso otro = mock(Curso.class);
            CursoResponse response2 = mock(CursoResponse.class);
            when(cursoRepository.findAll()).thenReturn(List.of(curso, otro));
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);
            when(cursoMapper.entidadAResponse(otro)).thenReturn(response2);

            List<CursoResponse> resultado = cursoService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay cursos")
        void listar_sinCursos_debeRetornarListaVacia() {
            when(cursoRepository.findAll()).thenReturn(List.of());

            List<CursoResponse> resultado = cursoService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el curso mapeado cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerCurso(curso);
            when(cursoMapper.entidadAResponse(curso)).thenReturn(response);

            CursoResponse resultado = cursoService.obtenerPorId(ID);

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
        @DisplayName("debe eliminar el curso cuando no tiene grupos relacionados")
        void eliminar_sinGrupos_debeEliminar() {
            stubObtenerCurso(curso);
            when(grupoRepository.existsByCursoId(ID)).thenReturn(false);

            cursoService.eliminar(ID);

            verify(cursoRepository).delete(curso);
            verify(cursoRepository).flush();
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si el nombre ya existe")
        void registrar_conNombreDuplicado_debeLanzarConflicto() {
            when(cursoMapper.requestAEntidad(request)).thenReturn(curso);
            when(cursoRepository.existsByNombre("Matemáticas")).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> cursoService.registrar(request)
            );
            assertEquals("Ya existe un curso con ese nombre", ex.getMessage());

            verify(cursoRepository, never()).save(any());
            verify(cursoRepository, never()).flush();
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si el nombre ya existe en otro curso")
        void actualizar_conNombreDuplicado_debeLanzarConflicto() {
            when(cursoRepository.existsByNombreAndIdNot("Matemáticas", ID)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> cursoService.actualizar(request, ID)
            );
            assertEquals("Ya existe un curso con ese nombre", ex.getMessage());

            // No debe buscar el curso ni guardar
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    any(), anyLong(), any()), never());
            verify(cursoRepository, never()).save(any());
        }

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene grupos")
        void eliminar_conGrupos_debeLanzarEntidadRelacionada() {
            stubObtenerCurso(curso);
            when(grupoRepository.existsByCursoId(ID)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> cursoService.eliminar(ID)
            );
            assertEquals("No se pueden eliminar cursos con grupos relacionados", ex.getMessage());

            verify(cursoRepository, never()).delete(any());
            verify(cursoRepository, never()).flush();
        }

        @Test
        @DisplayName("actualizar: debe propagar la excepción si el curso no existe")
        void actualizar_cuandoNoExiste_debeLanzarExcepcion() {
            when(cursoRepository.existsByNombreAndIdNot("Matemáticas", ID)).thenReturn(false);
            stubObtenerCursoLanzaExcepcion(
                    new RecursoNoEncontradoException("Curso no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> cursoService.actualizar(request, ID)
            );

            verify(cursoRepository, never()).save(any());
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar la excepción si el curso no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerCursoLanzaExcepcion(
                    new RecursoNoEncontradoException("Curso no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> cursoService.obtenerPorId(ID)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar la excepción si el curso no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerCursoLanzaExcepcion(
                    new RecursoNoEncontradoException("Curso no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> cursoService.eliminar(ID)
            );

            verify(grupoRepository, never()).existsByCursoId(anyLong());
        }
    }
}