package com.eduardo.escuela.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalTime;

import com.eduardo.escuela.enums.DiaSemana;
import com.eduardo.escuela.exceptions.DatoInvalidoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias de la entidad Horario")
class HorarioTest {

    private Grupo grupo;
    private DiaSemana diaValido;
    private LocalTime horaInicioValida;
    private LocalTime horaFinValida;

    @BeforeEach
    void setUp() {
        grupo = mock(Grupo.class);
        diaValido = DiaSemana.LUNES;
        horaInicioValida = LocalTime.of(8, 0);
        horaFinValida = LocalTime.of(10, 0);
    }

    // ============================================================
    // HAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe construir un Horario correctamente con datos válidos")
    void crear_conDatosValidos_debeConstruirHorario() {
        Horario horario = Horario.crear(grupo, diaValido, horaInicioValida, horaFinValida);

        assertNotNull(horario);
        assertEquals(grupo, horario.getGrupo());
        assertEquals(DiaSemana.LUNES, horario.getDia());
        assertEquals(LocalTime.of(8, 0), horario.getHoraInicio());
        assertEquals(LocalTime.of(10, 0), horario.getHoraFin());
    }

    @Test
    @DisplayName("crear: debe aceptar un rango de un solo minuto de duración")
    void crear_conRangoMinimo_debeConstruirHorario() {
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(8, 1);

        Horario horario = Horario.crear(grupo, diaValido, inicio, fin);

        assertEquals(inicio, horario.getHoraInicio());
        assertEquals(fin, horario.getHoraFin());
    }

    @Test
    @DisplayName("crear: debe aceptar un rango que cruza el mediodía")
    void crear_conRangoCruzandoMediodia_debeConstruirHorario() {
        Horario horario = Horario.crear(grupo, diaValido,
                LocalTime.of(11, 30), LocalTime.of(13, 30));

        assertEquals(LocalTime.of(11, 30), horario.getHoraInicio());
        assertEquals(LocalTime.of(13, 30), horario.getHoraFin());
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe modificar todos los campos correctamente")
    void actualizar_conDatosValidos_debeActualizarCampos() {
        Horario horario = Horario.crear(grupo, diaValido, horaInicioValida, horaFinValida);

        Grupo nuevoGrupo = mock(Grupo.class);
        horario.actualizar(nuevoGrupo, DiaSemana.VIERNES,
                LocalTime.of(14, 0), LocalTime.of(16, 0));

        assertEquals(nuevoGrupo, horario.getGrupo());
        assertEquals(DiaSemana.VIERNES, horario.getDia());
        assertEquals(LocalTime.of(14, 0), horario.getHoraInicio());
        assertEquals(LocalTime.of(16, 0), horario.getHoraFin());
    }

    // ============================================================
    // HAPPY PATHS - entidadAHorarioFormateado()
    // ============================================================

    @Test
    @DisplayName("entidadAHorarioFormateado: debe formatear correctamente un horario")
    void formatear_conHorarioValido_debeRetornarTextoFormateado() {
        when(grupo.getId()).thenReturn(1L);
        Horario horario = Horario.crear(grupo, DiaSemana.LUNES,
                LocalTime.of(8, 0), LocalTime.of(10, 30));

        String resultado = Horario.entidadAHorarioFormateado(horario);

        assertEquals("Lunes 08:00 - 10:30", resultado);
    }

    @Test
    @DisplayName("entidadAHorarioFormateado: debe retornar null si el horario es null")
    void formatear_conHorarioNull_debeRetornarNull() {
        assertNull(Horario.entidadAHorarioFormateado(null));
    }

    // ============================================================
    // UNHAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe lanzar excepción si el grupo es null")
    void crear_conGrupoNull_debeLanzarExcepcion() {
        DatoInvalidoException ex = assertThrows(
                DatoInvalidoException.class,
                () -> Horario.crear(null, diaValido, horaInicioValida, horaFinValida)
        );
        assertEquals("El grupo es requerido", ex.getMessage());
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el día es null")
    void crear_conDiaNull_debeLanzarExcepcion() {
        DatoInvalidoException ex = assertThrows(
                DatoInvalidoException.class,
                () -> Horario.crear(grupo, null, horaInicioValida, horaFinValida)
        );
        assertEquals("El día es requerido", ex.getMessage());
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si la hora de inicio es posterior o igual a la de fin")
    void crear_conRangoInvalido_debeLanzarExcepcion() {
        // inicio == fin
        assertThrows(
                DatoInvalidoException.class,
                () -> Horario.crear(grupo, diaValido,
                        LocalTime.of(10, 0), LocalTime.of(10, 0))
        );

        // inicio > fin
        DatoInvalidoException ex = assertThrows(
                DatoInvalidoException.class,
                () -> Horario.crear(grupo, diaValido,
                        LocalTime.of(12, 0), LocalTime.of(9, 0))
        );
        assertEquals("La hora de fin debe ser posterior a la hora de inicio", ex.getMessage());
    }

    // ============================================================
    // UNHAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el grupo es null")
    void actualizar_conGrupoNull_debeLanzarExcepcion() {
        Horario horario = Horario.crear(grupo, diaValido, horaInicioValida, horaFinValida);

        assertThrows(
                DatoInvalidoException.class,
                () -> horario.actualizar(null, DiaSemana.MARTES,
                        LocalTime.of(9, 0), LocalTime.of(11, 0))
        );
    }

    @Test
    @DisplayName("actualizar: debe lanzar excepción si la hora de fin no es posterior a la de inicio")
    void actualizar_conRangoInvalido_debeLanzarExcepcion() {
        Horario horario = Horario.crear(grupo, diaValido, horaInicioValida, horaFinValida);

        assertThrows(
                DatoInvalidoException.class,
                () -> horario.actualizar(grupo, DiaSemana.MARTES,
                        LocalTime.of(15, 0), LocalTime.of(14, 0))
        );
    }
}