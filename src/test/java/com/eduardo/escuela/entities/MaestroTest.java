package com.eduardo.escuela.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaestroTest {

    private Maestro maestroBase;

    @BeforeEach
    void setUp() {
        maestroBase = Maestro.crear("Roberto", "Gómez", "Bolaños", "roberto@email.com", "5512345678");
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea un maestro correctamente con datos válidos, email en minúsculas y trim")
        void crearMaestro_Exitoso() {
            Maestro maestro = Maestro.crear("  Ana ", " Martínez ", " López ", " ANA.MARTINEZ@EMAIL.COM ", " 5587654321 ");

            assertNotNull(maestro);
            assertEquals("Ana", maestro.getNombre());
            assertEquals("Martínez", maestro.getApellidoPaterno());
            assertEquals("López", maestro.getApellidoMaterno());
            assertEquals("ana.martinez@email.com", maestro.getEmail());
            assertEquals("5587654321", maestro.getTelefono());
            assertNotNull(maestro.getGrupos());
            assertTrue(maestro.getGrupos().isEmpty());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el teléfono no tiene exactamente 10 caracteres")
        void crearMaestro_TelefonoInvalido_LanzaExcepcion() {
            assertThrows(Exception.class, () ->
                    Maestro.crear("Roberto", "Gómez", "Bolaños", "roberto@email.com", "12345")
            );
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el email no cumple el tamaño mínimo (menos de 8 caracteres)")
        void crearMaestro_EmailCorto_LanzaExcepcion() {
            assertThrows(Exception.class, () ->
                    Maestro.crear("Roberto", "Gómez", "Bolaños", "a@b.c", "5512345678")
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza todos los campos del maestro correctamente")
        void actualizarMaestro_Exitoso() {
            maestroBase.actualizar(" Carlos ", " Hernández ", " Pérez ", " CARLOS.H@EMAIL.COM ", " 5599887766 ");

            assertEquals("Carlos", maestroBase.getNombre());
            assertEquals("Hernández", maestroBase.getApellidoPaterno());
            assertEquals("Pérez", maestroBase.getApellidoMaterno());
            assertEquals("carlos.h@email.com", maestroBase.getEmail());
            assertEquals("5599887766", maestroBase.getTelefono());
        }

        @Test
        @DisplayName("Unhappy Path: Falla al actualizar si un apellido excede los 50 caracteres")
        void actualizarMaestro_ApellidoLargo_LanzaExcepcion() {
            String apellidoLargo = "A".repeat(51);

            assertThrows(Exception.class, () ->
                    maestroBase.actualizar("Roberto", apellidoLargo, "Bolaños", "roberto@email.com", "5512345678")
            );

            // Se comprueba que el estado de la entidad permanezca inalterado tras el fallo
            assertEquals("Gómez", maestroBase.getApellidoPaterno());
            assertEquals("roberto@email.com", maestroBase.getEmail());
        }
    }
}