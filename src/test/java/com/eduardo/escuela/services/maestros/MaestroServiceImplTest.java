package com.eduardo.escuela.services.maestros;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.entities.Maestro;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import com.eduardo.escuela.mapper.MaestroMapper;
import com.eduardo.escuela.repositories.GrupoRepository;
import com.eduardo.escuela.repositories.MaestroRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de MaestroServiceImpl")
class MaestroServiceImplTest {

    @Mock private MaestroRepository maestroRepository;
    @Mock private MaestroMapper maestroMapper;
    @Mock private GrupoRepository grupoRepository;

    @InjectMocks private MaestroServiceImpl maestroService;

    private MockedStatic<ServiceUtils> serviceUtilsMock;

    private MaestroRequest request;
    private Maestro maestro;
    private MaestroResponse response;
    private final Long ID = 1L;

    private static final String NOMBRE = "Eduardo";
    private static final String APELLIDO_PATERNO = "García";
    private static final String APELLIDO_MATERNO = "López";
    private static final String EMAIL = "eduardo@escuela.com";
    private static final String TELEFONO = "5512345678";

    @BeforeEach
    void setUp() {
        request = new MaestroRequest(NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, EMAIL, TELEFONO);
        maestro = mock(Maestro.class);
        response = mock(MaestroResponse.class);

        serviceUtilsMock = mockStatic(ServiceUtils.class);
    }

    @AfterEach
    void tearDown() {
        serviceUtilsMock.close();
    }

    private void stubObtenerMaestro(Maestro retorno) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(maestroRepository), eq(ID), eq(Maestro.class)))
                .thenReturn(retorno);
    }

    private void stubObtenerMaestroLanza(RuntimeException ex) {
        serviceUtilsMock.when(() -> ServiceUtils.obtenerEntidadOException(
                        eq(maestroRepository), eq(ID), eq(Maestro.class)))
                .thenThrow(ex);
    }

    // ============================================================
    // HAPPY PATHS - registrar()
    // ============================================================

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("debe registrar un maestro correctamente")
        void registrar_conDatosValidos_debeGuardarYRetornarResponse() {
            when(maestroRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(maestroRepository.existsByTelefono(TELEFONO)).thenReturn(false);
            when(maestroMapper.entidadAResponse(any(Maestro.class))).thenReturn(response);

            MaestroResponse resultado = maestroService.registrar(request);

            assertSame(response, resultado);
            verify(maestroRepository).save(any(Maestro.class));
            verify(maestroRepository).flush();
        }

        @Test
        @DisplayName("debe construir el Maestro con los datos normalizados por Maestro.crear")
        void registrar_debeConstruirMaestroConDatosNormalizados() {
            // El request tiene espacios y email en mayúsculas
            MaestroRequest conEspacios = new MaestroRequest(
                    "  Eduardo  ", "  García  ", "  López  ",
                    "  EDUARDO@ESCUELA.COM  ", "  5512345678  ");

            when(maestroRepository.existsByEmail("eduardo@escuela.com")).thenReturn(false);
            when(maestroRepository.existsByTelefono("5512345678")).thenReturn(false);
            when(maestroMapper.entidadAResponse(any(Maestro.class))).thenReturn(response);

            maestroService.registrar(conEspacios);

            ArgumentCaptor<Maestro> captor = ArgumentCaptor.forClass(Maestro.class);
            verify(maestroRepository).save(captor.capture());

            Maestro guardado = captor.getValue();
            assertEquals("Eduardo", guardado.getNombre());
            assertEquals("García", guardado.getApellidoPaterno());
            assertEquals("López", guardado.getApellidoMaterno());
            assertEquals("eduardo@escuela.com", guardado.getEmail());
            assertEquals("5512345678", guardado.getTelefono());
        }

        @Test
        @DisplayName("debe validar email y teléfono únicos contra BD")
        void registrar_debeValidarEmailYTelefonoUnicos() {
            when(maestroRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(maestroRepository.existsByTelefono(TELEFONO)).thenReturn(false);
            when(maestroMapper.entidadAResponse(any(Maestro.class))).thenReturn(response);

            maestroService.registrar(request);

            verify(maestroRepository).existsByEmail(EMAIL);
            verify(maestroRepository).existsByTelefono(TELEFONO);
        }
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar el maestro correctamente")
        void actualizar_conDatosValidos_debeActualizarYGuardar() {
            stubObtenerMaestro(maestro);
            when(maestroRepository.existsByEmailAndIdNot(EMAIL, ID)).thenReturn(false);
            when(maestroRepository.existsByTelefonoAndIdNot(TELEFONO, ID)).thenReturn(false);
            when(maestroMapper.entidadAResponse(maestro)).thenReturn(response);

            MaestroResponse resultado = maestroService.actualizar(request, ID);

            assertSame(response, resultado);
            verify(maestro).actualizar(NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO,
                    EMAIL, TELEFONO);
            verify(maestroRepository).save(maestro);
            verify(maestroRepository).flush();
        }

        @Test
        @DisplayName("debe permitir mantener el mismo email y teléfono (sin conflicto consigo mismo)")
        void actualizar_conMismosDatos_noDebeLanzarConflicto() {
            stubObtenerMaestro(maestro);
            // IdNot excluye el propio ID: no hay conflicto
            when(maestroRepository.existsByEmailAndIdNot(EMAIL, ID)).thenReturn(false);
            when(maestroRepository.existsByTelefonoAndIdNot(TELEFONO, ID)).thenReturn(false);
            when(maestroMapper.entidadAResponse(maestro)).thenReturn(response);

            assertDoesNotThrow(() -> maestroService.actualizar(request, ID));
        }

        @Test
        @DisplayName("debe normalizar los datos antes de actualizar la entidad")
        void actualizar_debeNormalizarDatosAntesDeActualizar() {
            MaestroRequest conEspacios = new MaestroRequest(
                    "  Eduardo  ", "  García  ", "  López  ",
                    "  EDUARDO@ESCUELA.COM  ", "  5512345678  ");

            stubObtenerMaestro(maestro);
            when(maestroRepository.existsByEmailAndIdNot("eduardo@escuela.com", ID))
                    .thenReturn(false);
            when(maestroRepository.existsByTelefonoAndIdNot("5512345678", ID))
                    .thenReturn(false);
            when(maestroMapper.entidadAResponse(maestro)).thenReturn(response);

            maestroService.actualizar(conEspacios, ID);

            verify(maestro).actualizar("Eduardo", "García", "López",
                    "eduardo@escuela.com", "5512345678");
        }
    }

    // ============================================================
    // HAPPY PATHS - listar() y obtenerPorId()
    // ============================================================

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("debe retornar la lista mapeada de maestros")
        void listar_debeMapearTodosLosMaestros() {
            Maestro otro = mock(Maestro.class);
            MaestroResponse response2 = mock(MaestroResponse.class);
            when(maestroRepository.findAll()).thenReturn(List.of(maestro, otro));
            when(maestroMapper.entidadAResponse(maestro)).thenReturn(response);
            when(maestroMapper.entidadAResponse(otro)).thenReturn(response2);

            List<MaestroResponse> resultado = maestroService.listar();

            assertEquals(2, resultado.size());
            assertSame(response, resultado.get(0));
            assertSame(response2, resultado.get(1));
        }

        @Test
        @DisplayName("debe retornar lista vacía si no hay maestros")
        void listar_sinMaestros_debeRetornarListaVacia() {
            when(maestroRepository.findAll()).thenReturn(List.of());

            List<MaestroResponse> resultado = maestroService.listar();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el maestro mapeado cuando existe")
        void obtenerPorId_cuandoExiste_debeRetornarResponse() {
            stubObtenerMaestro(maestro);
            when(maestroMapper.entidadAResponse(maestro)).thenReturn(response);

            MaestroResponse resultado = maestroService.obtenerPorId(ID);

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
        @DisplayName("debe eliminar el maestro cuando no tiene grupos asignados")
        void eliminar_sinGrupos_debeEliminar() {
            stubObtenerMaestro(maestro);
            when(grupoRepository.existsByMaestroId(ID)).thenReturn(false);

            maestroService.eliminar(ID);

            verify(maestroRepository).delete(maestro);
            verify(maestroRepository).flush();
        }
    }

    // ============================================================
    // UNHAPPY PATHS
    // ============================================================

    @Nested
    @DisplayName("errores")
    class Errores {

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si el email ya existe")
        void registrar_conEmailDuplicado_debeLanzarConflicto() {
            when(maestroRepository.existsByEmail(EMAIL)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> maestroService.registrar(request)
            );
            assertEquals("Email ya existente", ex.getMessage());

            // No debe consultar teléfono ni guardar
            verify(maestroRepository, never()).existsByTelefono(anyString());
            verify(maestroRepository, never()).save(any());
            verify(maestroRepository, never()).flush();
        }

        @Test
        @DisplayName("registrar: debe lanzar ConflictoException si el teléfono ya existe")
        void registrar_conTelefonoDuplicado_debeLanzarConflicto() {
            when(maestroRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(maestroRepository.existsByTelefono(TELEFONO)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> maestroService.registrar(request)
            );
            assertEquals("Telefono ya existente", ex.getMessage());

            verify(maestroRepository, never()).save(any());
            verify(maestroRepository, never()).flush();
        }

        @Test
        @DisplayName("registrar: debe propagar excepción si los datos del maestro no son válidos")
        void registrar_conDatosInvalidos_debeLanzarExcepcion() {
            // Email demasiado corto: Maestro.crear lanzará DatoInvalidoException
            MaestroRequest requestInvalido = new MaestroRequest(
                    NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, "a@b.com", TELEFONO);

            assertThrows(
                    RuntimeException.class,
                    () -> maestroService.registrar(requestInvalido)
            );

            // No debe consultar unicidad ni guardar
            verify(maestroRepository, never()).existsByEmail(anyString());
            verify(maestroRepository, never()).existsByTelefono(anyString());
            verify(maestroRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si otro maestro tiene ese email")
        void actualizar_conEmailDuplicado_debeLanzarConflicto() {
            stubObtenerMaestro(maestro);
            when(maestroRepository.existsByEmailAndIdNot(EMAIL, ID)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> maestroService.actualizar(request, ID)
            );
            assertEquals("Otro maestro ya tiene este email", ex.getMessage());

            // No debe consultar teléfono ni actualizar ni guardar
            verify(maestroRepository, never()).existsByTelefonoAndIdNot(anyString(), anyLong());
            verify(maestro, never()).actualizar(any(), any(), any(), any(), any());
            verify(maestroRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe lanzar ConflictoException si otro maestro tiene ese teléfono")
        void actualizar_conTelefonoDuplicado_debeLanzarConflicto() {
            stubObtenerMaestro(maestro);
            when(maestroRepository.existsByEmailAndIdNot(EMAIL, ID)).thenReturn(false);
            when(maestroRepository.existsByTelefonoAndIdNot(TELEFONO, ID)).thenReturn(true);

            ConflictoException ex = assertThrows(
                    ConflictoException.class,
                    () -> maestroService.actualizar(request, ID)
            );
            assertEquals("Otro maestro ya tiene este telefono", ex.getMessage());

            verify(maestro, never()).actualizar(any(), any(), any(), any(), any());
            verify(maestroRepository, never()).save(any());
        }

        @Test
        @DisplayName("actualizar: debe propagar excepción si el maestro no existe")
        void actualizar_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerMaestroLanza(new RecursoNoEncontradoException("Maestro no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> maestroService.actualizar(request, ID)
            );

            // No debe validar unicidad ni guardar
            verify(maestroRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
            verify(maestroRepository, never()).save(any());
        }

        @Test
        @DisplayName("eliminar: debe lanzar EntidadRelacionadaException si tiene grupos asignados")
        void eliminar_conGrupos_debeLanzarEntidadRelacionada() {
            stubObtenerMaestro(maestro);
            when(grupoRepository.existsByMaestroId(ID)).thenReturn(true);

            EntidadRelacionadaException ex = assertThrows(
                    EntidadRelacionadaException.class,
                    () -> maestroService.eliminar(ID)
            );
            assertEquals("No se puede eliminar si tiene grupos asignados", ex.getMessage());

            verify(maestroRepository, never()).delete(any());
            verify(maestroRepository, never()).flush();
        }

        @Test
        @DisplayName("obtenerPorId: debe propagar excepción si el maestro no existe")
        void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerMaestroLanza(new RecursoNoEncontradoException("Maestro no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> maestroService.obtenerPorId(ID)
            );
        }

        @Test
        @DisplayName("eliminar: debe propagar excepción si el maestro no existe")
        void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
            stubObtenerMaestroLanza(new RecursoNoEncontradoException("Maestro no encontrado"));

            assertThrows(
                    RecursoNoEncontradoException.class,
                    () -> maestroService.eliminar(ID)
            );

            verify(grupoRepository, never()).existsByMaestroId(anyLong());
        }
    }
}