package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AulaTest {

    private Aula aula;

    @BeforeEach
    void setUp() {
        aula = Aula.crear("Aula 101", 30);
    }

    @Nested
    @DisplayName("Método Factory: crear")
    class CrearTest {

        @Test
        @DisplayName("Happy Path: Crea un aula correctamente aplicando trim al nombre")
        void crearAula_HappyPath() {
            Aula nuevaAula = Aula.crear("  Laboratorio A  ", 25);

            assertThat(nuevaAula.getNombre()).isEqualTo("Laboratorio A");
            assertThat(nuevaAula.getCapacidad()).isEqualTo(25);
        }

        @Test
        @DisplayName("Unhappy Path: Falla al crear si el nombre es nulo o excede longitud")
        void crearAula_NombreInvalido_ThrowsException() {
            assertThatThrownBy(() -> Aula.crear("", 30))
                    .isInstanceOf(DatoInvalidoException.class);
        }

        @Test
        @DisplayName("Unhappy Path: Falla al crear si la capacidad es negativa o nula")
        void crearAula_CapacidadInvalida_ThrowsException() {
            assertThatThrownBy(() -> Aula.crear("Aula 102", -5))
                    .isInstanceOf(DatoInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("Método: actualizar")
    class ActualizarTest {

        @Test
        @DisplayName("Happy Path: Actualiza los datos del aula correctamente")
        void actualizarAula_HappyPath() {
            aula.actualizar("Aula Magna", 100);

            assertThat(aula.getNombre()).isEqualTo("Aula Magna");
            assertThat(aula.getCapacidad()).isEqualTo(100);
        }

        @Test
        @DisplayName("Unhappy Path: Falla si se intenta actualizar con capacidad inválida")
        void actualizarAula_CapacidadInvalida_ThrowsException() {
            assertThatThrownBy(() -> aula.actualizar("Aula 101", 0))
                    .isInstanceOf(DatoInvalidoException.class);

            // Verifica que los datos no hayan cambiado tras la excepción
            assertThat(aula.getNombre()).isEqualTo("Aula 101");
            assertThat(aula.getCapacidad()).isEqualTo(30);
        }
    }
}