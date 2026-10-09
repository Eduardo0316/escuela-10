package com.eduardo.escuela.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AulaTest {

    private Aula aulaBase;

    @BeforeEach
    void setUp() {
        aulaBase = Aula.crear("Laboratorio A", 30);
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea un aula correctamente con datos válidos")
        void crearAula_Exitoso() {
            Aula aula = Aula.crear("  Aula 101 ", 25);

            assertNotNull(aula);
            assertEquals("Aula 101", aula.getNombre());
            assertEquals(25, aula.getCapacidad());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el nombre excede el límite o es nulo/vacío")
        void crearAula_NombreInvalido_LanzaExcepcion() {
            String nombreLargo = "A".repeat(101);

            assertThrows(Exception.class, () -> 
                Aula.crear(nombreLargo, 25)
            );
        }

        @Test
        @DisplayName("Unhappy Path: Falla si la capacidad no es positiva o es nula")
        void crearAula_CapacidadInvalida_LanzaExcepcion() {
            assertThrows(Exception.class, () -> 
                Aula.crear("Aula 102", -5)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza los campos del aula correctamente")
        void actualizarAula_Exitoso() {
            aulaBase.actualizar("Laboratorio B", 40);

            assertEquals("Laboratorio B", aulaBase.getNombre());
            assertEquals(40, aulaBase.getCapacidad());
        }

        @Test
        @DisplayName("Unhappy Path: Falla al intentar actualizar con una capacidad inválida")
        void actualizarAula_CapacidadInvalida_LanzaExcepcion() {
            assertThrows(Exception.class, () -> 
                aulaBase.actualizar("Laboratorio B", 0)
            );
            // Verifica que no se hayan modificado los datos originales tras el fallo
            assertEquals("Laboratorio A", aulaBase.getNombre());
            assertEquals(30, aulaBase.getCapacidad());
        }
    }
}