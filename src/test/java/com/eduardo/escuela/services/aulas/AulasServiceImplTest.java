package com.eduardo.escuela.services.aulas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import com.eduardo.escuela.dto.aulas.AulaRequest;
import com.eduardo.escuela.dto.aulas.AulaResponse;
import com.eduardo.escuela.entities.Aula;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.AulaMapper;
import com.eduardo.escuela.repositories.AulaRepository;
import com.eduardo.escuela.repositories.GrupoRepository;
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
@DisplayName("Pruebas unitarias de AulasServiceImpl")
class AulasServiceImplTest {

    @Mock private GrupoRepository grupoRepository;
    @Mock private AulaRepository aulaRepository;
    @Mock private AulaMapper aulaMapper;

    @InjectMocks private AulasServiceImpl aulasService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private AulaRequest request;
    private Aula aula;
    private AulaResponse response;
    private final Long ID = 1L;

    @BeforeEach
    void setUp() {
        request = new AulaRequest("Aula 101", 30);
        aula = mock(Aula.class);
        response = mock(AulaResponse.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    private void stubObtenerAula(Aula retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(aulaRepository), eq(ID), eq(Aula.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerAulaLanzaExcepcion(RuntimeException ex) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(aulaRepository), eq(ID), eq(Aula.class)))
                .thenThrow(ex);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar un aula correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            when(aulaMapper.requestAEntidad(request)).thenReturn(aula);
            when(aula.getNombre()).thenReturn("Aula 101");
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(false);
            when(aulaMapper.entidadAResponse(aula)).thenReturn(response);

            AulaResponse resultado = aulasService.registrar(request);

            assertSame(response, resultado);
            verify(aulaRepository).save(aula);
            verify(aulaRepository).flush();
        }

        @Test
        @DisplayName("debe validar el nombre ya normalizado que devuelve el mapper")
        void registrar_debeValidarElNombreDeLaEntidadMapeada() {
            when(aulaMapper.requestAEntidad(request)).thenReturn(aula);
            when(aula.getNombre()).thenReturn("Aula 101");
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(false);
            when(aulaMapper.entidadAResponse(aula)).thenReturn(response);

            aulasService.registrar(request);

            verify(aulaRepository).existsByNombre("Aula 101");
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar el aula correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(false);
            stubObtenerAula(aula);
            when(aulaMapper.entidadAResponse(aula)).thenReturn(response);

            AulaResponse resultado = aulasService.actualizar(request, ID);

            assertSame(response, resultado);
            verify(aula).actualizar("Aula 101", 30);
            verify(aulaRepository).save(aula);
            verify(aulaRepository).flush();
        }

        @Test
        @DisplayName("debe validar el nombre antes de obtener el aula")
        void actualizar_debeValidarUnicidadAntesDeBuscar() {
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(false);
            stubObtenerAula(aula);
            when(aulaMapper.entidadAResponse(aula)).thenReturn(response);

            aulasService.actualizar(request, ID);

            var inOrder = inOrder(aulaRepository);
            inOrder.verify(aulaRepository).existsByNombre("Aula 101");
            inOrder.verify(aulaRepository).save(aula);
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de aulas")
        void listar_debeMapearTodasLasAulas() {
            Aula otra = mock(Aula.class);
            AulaResponse response2 = mock(AulaResponse.class);
            when(aulaRepository.findAll()).thenReturn(List.of(aula, otra));
            when(aulaMapper.entidadAResponse(aula)).thenReturn(response);
            when(aulaMapper.entidadAResponse(otra)).thenReturn(response2);

            List<AulaResponse> resultado = aulasService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay aulas")
        void listar_sinAulas_debeRetornarListaVacia() {
            when(aulaRepository.findAll()).thenReturn(List.of());

            List<AulaResponse> resultado = aulasService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el aula mapeada cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerAula(aula);
            when(aulaMapper.entidadAResponse(aula)).thenReturn(response);

            AulaResponse resultado = aulasService.obtenerPorId(ID);

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
        @DisplayName("debe eliminar el aula cuando no tiene grupos asignados")
        void eliminar_sinGrupos_debeEliminar() {
            stubObtenerAula(aula);
            when(grupoRepository.existsByAulaId(ID)).thenReturn(false);

            aulasService.eliminar(ID);

            verify(aulaRepository).delete(aula);
            verify(aulaRepository).flush();
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
            when(aulaMapper.requestAEntidad(request)).thenReturn(aula);
            when(aula.getNombre()).thenReturn("Aula 101");
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> aulasService.registrar(request)
            );
            assertEquals("Nombre de aula ya existente", ex.getMessage());

            verify(aulaRepository, never()).save(any());
            verify(aulaRepository, never()).flush();
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si el nombre ya existe")
        void actualizar_conNombreDuplicado_debeLanzarConflicto() {
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(true);

            assertThrows(
                    ConflictoException.class,
                    () -> aulasService.actualizar(request, ID)
            );

            // No debe buscar el aula ni guardar
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    any(), anyLong(), any()), never());
            verify(aulaRepository, never()).save(any());
        }

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene grupos")
        void eliminar_conGrupos_debeLanzarEntidadRelacionada() {
            stubObtenerAula(aula);
            when(grupoRepository.existsByAulaId(ID)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> aulasService.eliminar(ID)
            );
            assertEquals("No se pueden eliminar aulas con grupos asignados", ex.getMessage());

            verify(aulaRepository, never()).delete(any());
            verify(aulaRepository, never()).flush();
        }

        @Test
        @DisplayName("actualizar: debe propagar la excepción si el aula no existe")
        void actualizar_cuandoNoExiste_debeLanzarExcepcion() {
            when(aulaRepository.existsByNombre("Aula 101")).thenReturn(false);
            stubObtenerAulaLanzaExcepcion(
                    new RecursoNoEncontradoException("Aula no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> aulasService.actualizar(request, ID)
            );

            verify(aulaRepository, never()).save(any());
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar la excepción si el aula no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerAulaLanzaExcepcion(
                    new RecursoNoEncontradoException("Aula no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> aulasService.obtenerPorId(ID)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar la excepción si el aula no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerAulaLanzaExcepcion(
                    new RecursoNoEncontradoException("Aula no encontrada"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> aulasService.eliminar(ID)
            );

            verify(grupoRepository, never()).existsByAulaId(anyLong());
        }
    }
}