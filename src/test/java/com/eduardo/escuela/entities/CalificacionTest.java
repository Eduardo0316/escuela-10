package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalificacionTest {

    private Inscripcion inscripcionMock;

    @BeforeEach
    void setUp() {
        inscripcionMock = Mockito.mock(Inscripcion.class);
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea una calificación válida correctamente")
        void crearCalificacion_Exitoso() {
            BigDecimal nota = new BigDecimal("8.50");

            Calificacion calificacion = Calificacion.crear(inscripcionMock, nota);

            assertNotNull(calificacion);
            assertEquals(inscripcionMock, calificacion.getInscripcion());
            assertEquals(nota, calificacion.getCalificacion());
            assertNotNull(calificacion.getFechaRegistro());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si la calificación es mayor a 10 o menor a 0")
        void crearCalificacion_RangoInvalido_LanzaDatoInvalidoException() {
            BigDecimal notaInvalida = new BigDecimal("10.5");

            DatoInvalidoException ex = assertThrows(DatoInvalidoException.class, () ->
                    Calificacion.crear(inscripcionMock, notaInvalida)
            );

            assertEquals("La calificacion debe ser positiva y estar entre 0 y 10", ex.getMessage());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si la inscripción es nula")
        void crearCalificacion_InscripcionNula_LanzaRecursoNoEncontradoException() {
            BigDecimal nota = new BigDecimal("9.00");

            RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () ->
                    Calificacion.crear(null, nota)
            );

            assertEquals("La inscripcion es necesaria", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza la inscripción y la calificación correctamente")
        void actualizarCalificacion_Exitoso() {
            Calificacion calificacion = Calificacion.crear(inscripcionMock, new BigDecimal("6.00"));
            Inscripcion nuevaInscripcionMock = Mockito.mock(Inscripcion.class);
            BigDecimal nuevaNota = new BigDecimal("9.50");

            calificacion.actualizar(nuevaInscripcionMock, nuevaNota);

            assertEquals(nuevaInscripcionMock, calificacion.getInscripcion());
            assertEquals(nuevaNota, calificacion.getCalificacion());
        }

        @Test
        @DisplayName("Unhappy Path: Falla al intentar actualizar con una nota negativa")
        void actualizarCalificacion_NotaNegativa_LanzaDatoInvalidoException() {
            Calificacion calificacion = Calificacion.crear(inscripcionMock, new BigDecimal("7.00"));
            BigDecimal notaNegativa = new BigDecimal("-1.00");

            assertThrows(DatoInvalidoException.class, () ->
                    calificacion.actualizar(inscripcionMock, notaNegativa)
            );

            // Verifica que el valor previo no haya sido alterado
            assertEquals(new BigDecimal("7.00"), calificacion.getCalificacion());
        }
    }
}