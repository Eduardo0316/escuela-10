package com.eduardo.escuela.entities;

import com.eduardo.escuela.enums.DiaSemana;
import com.eduardo.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class HorarioTest {

    private Grupo grupoMock;
    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @BeforeEach
    void setUp() {
        grupoMock = Mockito.mock(Grupo.class);
        dia = DiaSemana.LUNES; // Asumiendo que es un Enum tipo LUNES, MARTES, etc.
        horaInicio = LocalTime.of(8, 0);
        horaFin = LocalTime.of(10, 0);
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea un horario correctamente con datos válidos")
        void crearHorario_Exitoso() {
            Horario horario = Horario.crear(grupoMock, dia, horaInicio, horaFin);

            assertNotNull(horario);
            assertEquals(grupoMock, horario.getGrupo());
            assertEquals(dia, horario.getDia());
            assertEquals(horaInicio, horario.getHoraInicio());
            assertEquals(horaFin, horario.getHoraFin());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si alguno de los parámetros es nulo")
        void crearHorario_ParametroNulo_LanzaDatoInvalidoException() {
            DatoInvalidoException ex = assertThrows(DatoInvalidoException.class, () ->
                    Horario.crear(null, dia, horaInicio, horaFin)
            );

            assertEquals("El grupo, el día y las horas son requeridos", ex.getMessage());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si la hora de fin es igual o anterior a la hora de inicio")
        void crearHorario_HoraFinInvalida_LanzaDatoInvalidoException() {
            LocalTime horaFinInvalida = LocalTime.of(7, 59);

            DatoInvalidoException ex = assertThrows(DatoInvalidoException.class, () ->
                    Horario.crear(grupoMock, dia, horaInicio, horaFinInvalida)
            );

            assertEquals("La hora de fin debe ser posterior a la hora de inicio", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza todos los campos del horario de forma exitosa")
        void actualizarHorario_Exitoso() {
            Horario horario = Horario.crear(grupoMock, dia, horaInicio, horaFin);

            Grupo nuevoGrupoMock = Mockito.mock(Grupo.class);
            DiaSemana nuevoDia = DiaSemana.MIERCOLES;
            LocalTime nuevaHoraInicio = LocalTime.of(10, 0);
            LocalTime nuevaHoraFin = LocalTime.of(12, 0);

            horario.actualizar(nuevoGrupoMock, nuevoDia, nuevaHoraInicio, nuevaHoraFin);

            assertEquals(nuevoGrupoMock, horario.getGrupo());
            assertEquals(nuevoDia, horario.getDia());
            assertEquals(nuevaHoraInicio, horario.getHoraInicio());
            assertEquals(nuevaHoraFin, horario.getHoraFin());
        }

        @Test
        @DisplayName("Unhappy Path: Falla al actualizar cuando la hora de fin es igual a la de inicio")
        void actualizarHorario_MismaHoraInicioYFin_LanzaDatoInvalidoException() {
            Horario horario = Horario.crear(grupoMock, dia, horaInicio, horaFin);
            LocalTime mismaHora = LocalTime.of(10, 0);

            assertThrows(DatoInvalidoException.class, () ->
                    horario.actualizar(grupoMock, dia, mismaHora, mismaHora)
            );

            // Verifica que los valores originales no cambiaron tras el fallo
            assertEquals(horaInicio, horario.getHoraInicio());
            assertEquals(horaFin, horario.getHoraFin());
        }
    }
}