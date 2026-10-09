package com.eduardo.escuela.services.horarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;

import com.eduardo.escuela.dto.datos.DatosGrupo;
import com.eduardo.escuela.dto.horarios.HorarioRequest;
import com.eduardo.escuela.dto.horarios.HorarioResponse;
import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.enums.DiaSemana;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.GrupoMapper;
import com.eduardo.escuela.mapper.HorarioMapper;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.repositories.HorarioRepository;
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
@DisplayName("Pruebas unitarias de HorarioServiceImpl")
class HorarioServiceImplTest {

    @Mock private HorarioMapper horarioMapper;
    @Mock private HorarioRepository horarioRepository;
    @Mock private GrupoRepository grupoRepository;
    @Mock private GrupoMapper grupoMapper;

    @InjectMocks private HorarioServiceImpl horarioService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private HorarioRequest request;
    private Horario horario;
    private HorarioResponse response;
    private Grupo grupo;
    private DatosGrupo datosGrupo;

    private final Long ID_HORARIO = 1L;
    private final Long ID_GRUPO = 10L;
    private final String DIA_DESC = "Lunes";
    private final LocalTime HORA_INICIO = LocalTime.of(8, 0);
    private final LocalTime HORA_FIN = LocalTime.of(10, 0);

    @BeforeEach
    void setUp() {
        request = new HorarioRequest(ID_GRUPO, DIA_DESC, HORA_INICIO, HORA_FIN);
        horario = mock(Horario.class);
        response = mock(HorarioResponse.class);
        grupo = mock(Grupo.class);
        datosGrupo = mock(DatosGrupo.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void stubObtenerHorario(Horario retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(horarioRepository), eq(ID_HORARIO), eq(Horario.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerHorarioLanza(RuntimeException ex) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(horarioRepository), eq(ID_HORARIO), eq(Horario.class)))
                .thenThrow(ex);
    }

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

    /** Stub del grupoMapper para que devuelva el DatosGrupo. */
    private void stubGrupoMapper() {
        when(grupoMapper.entidadADatosGrupo(grupo)).thenReturn(datosGrupo);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar un horario correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            stubObtenerGrupo(grupo);
            when(horarioRepository.existeTraslape(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN)).thenReturn(false);
            when(horarioMapper.requestAEntidad(request, grupo)).thenReturn(horario);
            stubGrupoMapper();
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);

            HorarioResponse resultado = horarioService.registrar(request);

            assertSame(response, resultado);
            verify(horarioRepository).save(horario);
            verify(horarioRepository).flush();
        }

        @Test
        @DisplayName("debe resolver el día desde la descripción del request")
        void registrar_debeResolverDiaDesdeDescripcion() {
            stubObtenerGrupo(grupo);
            when(horarioRepository.existeTraslape(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN)).thenReturn(false);
            when(horarioMapper.requestAEntidad(request, grupo)).thenReturn(horario);
            stubGrupoMapper();
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);

            horarioService.registrar(request);

            // Si se resolvió a LUNES, el mock de existeTraslape con LUNES debe haberse usado
            verify(horarioRepository).existeTraslape(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN);
        }

        @Test
        @DisplayName("debe validar rango horario antes de validar traslape")
        void registrar_debeValidarRangoAntesQueTraslape() {
            stubObtenerGrupo(grupo);
            when(horarioRepository.existeTraslape(
                    anyLong(), any(), any(), any())).thenReturn(false);
            when(horarioMapper.requestAEntidad(request, grupo)).thenReturn(horario);
            stubGrupoMapper();
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);

            horarioService.registrar(request);

            // El orden importa: primero rango, luego traslape.
            // Como el rango es válido, no hay excepción, pero verificamos
            // que no se llamó a existeTraslape antes de tiempo (no testeable directo,
            // pero podemos verificar el flujo con InOrder sobre el repo).
            InOrder inOrder = inOrder(horarioRepository);
            inOrder.verify(horarioRepository).existeTraslape(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN);
            inOrder.verify(horarioRepository).save(horario);
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar el horario correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            stubObtenerHorario(horario);
            stubObtenerGrupo(grupo);
            when(horarioRepository.existeTraslapeExcepto(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN, ID_HORARIO))
                    .thenReturn(false);
            stubGrupoMapper();
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);

            HorarioResponse resultado = horarioService.actualizar(request, ID_HORARIO);

            assertSame(response, resultado);
            verify(horario).actualizar(grupo, DiaSemana.LUNES, HORA_INICIO, HORA_FIN);
            verify(horarioRepository).save(horario);
            verify(horarioRepository).flush();
        }

        @Test
        @DisplayName("debe permitir mantener el mismo horario (sin conflicto consigo mismo)")
        void actualizar_conMismoHorario_noDebeLanzarConflicto() {
            stubObtenerHorario(horario);
            stubObtenerGrupo(grupo);
            // existeTraslapeExcepto excluye el propio ID
            when(horarioRepository.existeTraslapeExcepto(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN, ID_HORARIO))
                    .thenReturn(false);
            stubGrupoMapper();
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);

            assertDoesNotThrow(() -> horarioService.actualizar(request, ID_HORARIO));
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de horarios")
        void listar_debeMapearTodosLosHorarios() {
            Horario otro = mock(Horario.class);
            Grupo otroGrupo = mock(Grupo.class);
            DatosGrupo otrosDatos = mock(DatosGrupo.class);
            HorarioResponse response2 = mock(HorarioResponse.class);

            when(horarioRepository.findAll()).thenReturn(List.of(horario, otro));
            when(horario.getGrupo()).thenReturn(grupo);
            when(otro.getGrupo()).thenReturn(otroGrupo);
            when(grupoMapper.entidadADatosGrupo(grupo)).thenReturn(datosGrupo);
            when(grupoMapper.entidadADatosGrupo(otroGrupo)).thenReturn(otrosDatos);
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);
            when(horarioMapper.entidadAResponse(otro, otrosDatos)).thenReturn(response2);

            List<HorarioResponse> resultado = horarioService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay horarios")
        void listar_sinHorarios_debeRetornarListaVacia() {
            when(horarioRepository.findAll()).thenReturn(List.of());

            List<HorarioResponse> resultado = horarioService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el horario mapeado cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerHorario(horario);
            when(horario.getGrupo()).thenReturn(grupo);
            stubGrupoMapper();
            when(horarioMapper.entidadAResponse(horario, datosGrupo)).thenReturn(response);

            HorarioResponse resultado = horarioService.obtenerPorId(ID_HORARIO);

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
        @DisplayName("debe eliminar el horario correctamente")
        void eliminar_cuandoExiste_debeEliminar() {
            stubObtenerHorario(horario);

            horarioService.eliminar(ID_HORARIO);

            verify(horarioRepository).delete(horario);
            verify(horarioRepository).flush();
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si la hora de inicio no es anterior a la de fin")
        void registrar_conRangoInvalido_debeLanzarConflicto() {
            stubObtenerGrupo(grupo);
            HorarioRequest requestInvalido = new HorarioRequest(
                    ID_GRUPO, DIA_DESC, LocalTime.of(10, 0), LocalTime.of(9, 0));

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> horarioService.registrar(requestInvalido)
            );
            assertEquals("La hora de inicio debe ser anterior a la hora de fin", ex.getMessage());

            verify(horarioRepository, never()).existeTraslape(anyLong(), any(), any(), any());
            verify(horarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si hay traslape")
        void registrar_conTraslape_debeLanzarConflicto() {
            stubObtenerGrupo(grupo);
            when(horarioRepository.existeTraslape(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> horarioService.registrar(request)
            );
            assertEquals("El horario se traslapa con otro del mismo grupo", ex.getMessage());

            verify(horarioRepository, never()).save(any());
            verify(horarioRepository, never()).flush();
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si el día no es válido")
        void registrar_conDiaInvalido_debeLanzarExcepcion() {
            stubObtenerGrupo(grupo);
            HorarioRequest requestInvalido = new HorarioRequest(
                    ID_GRUPO, "Feriado", HORA_INICIO, HORA_FIN);

            assertThrows(
                    DatoInvalidoException.class,
                    () -> horarioService.registrar(requestInvalido)
            );

            verify(horarioRepository, never()).existeTraslape(anyLong(), any(), any(), any());
            verify(horarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si el grupo no existe")
        void registrar_cuandoGrupoNoExiste_debeLanzarExcepcion() {
            stubObtenerGrupoLanza(new RecursoNoEncontradoException("Grupo no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> horarioService.registrar(request)
            );

            verify(horarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si hay traslape con otro horario")
        void actualizar_conTraslape_debeLanzarConflicto() {
            stubObtenerHorario(horario);
            stubObtenerGrupo(grupo);
            when(horarioRepository.existeTraslapeExcepto(
                    ID_GRUPO, DiaSemana.LUNES, HORA_INICIO, HORA_FIN, ID_HORARIO))
                    .thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> horarioService.actualizar(request, ID_HORARIO)
            );
            assertEquals("El horario se traslapa con otro del mismo grupo", ex.getMessage());

            verify(horario, never()).actualizar(any(), any(), any(), any());
            verify(horarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si el horario no existe")
        void actualizar_cuandoHorarioNoExiste_debeLanzarExcepcion() {
            stubObtenerHorarioLanza(new RecursoNoEncontradoException("Horario no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> horarioService.actualizar(request, ID_HORARIO)
            );

            // No debe buscar el grupo ni validar traslape
            serviceUtilsMock.verify(() -> ServiceUtils.obtenerEntidadOException(
                    eq(grupoRepository), anyLong(), eq(Grupo.class)), never());
            verify(horarioRepository, never()).existeTraslapeExcepto(
                    anyLong(), any(), any(), any(), anyLong());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si el grupo no existe")
        void actualizar_cuandoGrupoNoExiste_debeLanzarExcepcion() {
            stubObtenerHorario(horario);
            stubObtenerGrupoLanza(new RecursoNoEncontradoException("Grupo no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> horarioService.actualizar(request, ID_HORARIO)
            );

            verify(horarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar excepción si el horario no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerHorarioLanza(new RecursoNoEncontradoException("Horario no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> horarioService.obtenerPorId(ID_HORARIO)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar excepción si el horario no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerHorarioLanza(new RecursoNoEncontradoException("Horario no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> horarioService.eliminar(ID_HORARIO)
            );

            verify(horarioRepository, never()).delete(any());
        }
    }
}