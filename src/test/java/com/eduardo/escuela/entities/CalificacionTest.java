package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CalificacionTest {

    @Mock
    private Inscripcion inscripcionMock;

    @Mock
    private Inscripcion otraInscripcionMock;

    private Calificacion calificacion;

    @BeforeEach
    void setUp() {
        calificacion = Calificacion.crear(inscripcionMock, new BigDecimal("8.5"));
    }

    @Nested
    @DisplayName("Método Factory: crear")
    class CrearTest {

        @Test
        @DisplayName("Happy Path: Crea una calificación válida en el rango permitido [0, 10]")
        void crearCalificacion_HappyPath() {
            BigDecimal nota = new BigDecimal("10.0");

            Calificacion nuevaCalificacion = Calificacion.crear(inscripcionMock, nota);

            assertThat(nuevaCalificacion.getInscripcion()).isEqualTo(inscripcionMock);
            assertThat(nuevaCalificacion.getCalificacion()).isEqualTo(nota);
            assertThat(nuevaCalificacion.getFechaRegistro()).isNotNull();
        }

        @Test
        @DisplayName("Unhappy Path: Falla al crear si la inscripción es nula (RecursoNoEncontradoException)")
        void crearCalificacion_InscripcionNula_ThrowsException() {
            assertThatThrownBy(() -> Calificacion.crear(null, new BigDecimal("8.0")))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessage("La inscripcion es necesaria");
        }

        @Test
        @DisplayName("Unhappy Path: Falla al crear si la calificación es menor a 0 (DatoInvalidoException)")
        void crearCalificacion_MenorAZero_ThrowsException() {
            assertThatThrownBy(() -> Calificacion.crear(inscripcionMock, new BigDecimal("-0.1")))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessage("La falificacion debe ser positiva");
        }

        @Test
        @DisplayName("Unhappy Path: Falla al crear si la calificación es mayor a 10 (DatoInvalidoException)")
        void crearCalificacion_MayorADiez_ThrowsException() {
            assertThatThrownBy(() -> Calificacion.crear(inscripcionMock, new BigDecimal("10.1")))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessage("La calificacion debe estar entre 0 y 10");
        }
    }

    @Nested
    @DisplayName("Método: actualizar")
    class ActualizarTest {

        @Test
        @DisplayName("Happy Path: Actualiza la calificación e inscripción correctamente")
        void actualizarCalificacion_HappyPath() {
            BigDecimal nuevaNota = new BigDecimal("9.5");

            calificacion.actualizar(otraInscripcionMock, nuevaNota);

            assertThat(calificacion.getInscripcion()).isEqualTo(otraInscripcionMock);
            assertThat(calificacion.getCalificacion()).isEqualTo(nuevaNota);
        }

        @Test
        @DisplayName("Unhappy Path: Falla al actualizar con un valor fuera del rango")
        void actualizarCalificacion_NotaInvalida_ThrowsException() {
            assertThatThrownBy(() -> calificacion.actualizar(inscripcionMock, new BigDecimal("15.0")))
                    .isInstanceOf(DatoInvalidoException.class);

            // Verifica que el estado original no haya cambiado
            assertThat(calificacion.getCalificacion()).isEqualTo(new BigDecimal("8.5"));
        }
    }
}