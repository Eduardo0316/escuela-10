package com.eduardo.escuela.enums;

import static org.junit.jupiter.api.Assertions.*;

import com.eduardo.escuela.exceptions.DatoInvalidoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Pruebas unitarias del enum DiaSemana")
class DiaSemanaTest {

    // ============================================================
    // HAPPY PATHS - valores del enum y getDescripcion()
    // ============================================================

    @Test
    @DisplayName("values: debe contener exactamente 7 días")
    void values_debeContenerSieteDias() {
        assertEquals(7, DiaSemana.values().length);
    }

    @ParameterizedTest(name = "{0} -> descripción = {1}")
    @CsvSource({
            "LUNES,      Lunes",
            "MARTES,     Martes",
            "MIERCOLES,  Miércoles",
            "JUEVES,     Jueves",
            "VIERNES,    Viernes",
            "SABADO,     Sábado",
            "DOMINGO,    Domingo"
    })
    @DisplayName("getDescripcion: cada día debe tener su descripción correcta")
    void getDescripcion_debeRetornarDescripcionCorrecta(DiaSemana dia, String descripcionEsperada) {
        assertEquals(descripcionEsperada, dia.getDescripcion());
    }

    @ParameterizedTest
    @EnumSource(DiaSemana.class)
    @DisplayName("getDescripcion: ningún día debe tener descripción null ni vacía")
    void getDescripcion_ningunDiaDebeSerNullNiVacio(DiaSemana dia) {
        assertNotNull(dia.getDescripcion());
        assertFalse(dia.getDescripcion().isBlank());
    }

    // ============================================================
    // HAPPY PATHS - obtenerPorDescripcion()
    // ============================================================

    @Nested
    @DisplayName("obtenerPorDescripcion - casos válidos")
    class ObtenerPorDescripcionValidos {

        @Test
        @DisplayName("debe retornar el día correcto con la descripción exacta")
        void conDescripcionExacta_debeRetornarDia() {
            assertEquals(DiaSemana.LUNES, DiaSemana.obtenerPorDescripcion("Lunes"));
            assertEquals(DiaSemana.MIERCOLES, DiaSemana.obtenerPorDescripcion("Miércoles"));
            assertEquals(DiaSemana.DOMINGO, DiaSemana.obtenerPorDescripcion("Domingo"));
        }

        @Test
        @DisplayName("debe ser case-insensitive")
        void conMayusculasMinusculas_debeRetornarDia() {
            assertEquals(DiaSemana.LUNES, DiaSemana.obtenerPorDescripcion("lunes"));
            assertEquals(DiaSemana.LUNES, DiaSemana.obtenerPorDescripcion("LUNES"));
            assertEquals(DiaSemana.LUNES, DiaSemana.obtenerPorDescripcion("LuNeS"));
        }

        @Test
        @DisplayName("debe ignorar espacios al inicio y al final")
        void conEspaciosAlrededor_debeRetornarDia() {
            assertEquals(DiaSemana.MARTES, DiaSemana.obtenerPorDescripcion("  Martes  "));
            assertEquals(DiaSemana.MARTES, DiaSemana.obtenerPorDescripcion("\tMartes\n"));
        }

        @Test
        @DisplayName("debe ignorar acentos (normalización de texto)")
        void sinAcentos_debeRetornarDia() {
            assertEquals(DiaSemana.MIERCOLES, DiaSemana.obtenerPorDescripcion("Miercoles"));
            assertEquals(DiaSemana.SABADO, DiaSemana.obtenerPorDescripcion("Sabado"));
        }

        @Test
        @DisplayName("debe combinar espacios, acentos y case-insensitive")
        void combinacionDeNormalizaciones_debeRetornarDia() {
            assertEquals(DiaSemana.MIERCOLES, DiaSemana.obtenerPorDescripcion("  miercoles  "));
            assertEquals(DiaSemana.SABADO, DiaSemana.obtenerPorDescripcion("  SABADO  "));
        }

        @Test
        @DisplayName("debe retornar el día correcto para todas las descripciones válidas")
        void paraTodasLasDescripciones_debeRetornarDia() {
            for (DiaSemana dia : DiaSemana.values()) {
                assertEquals(dia, DiaSemana.obtenerPorDescripcion(dia.getDescripcion()));
            }
        }
    }

    // ============================================================
    // UNHAPPY PATHS - obtenerPorDescripcion()
    // ============================================================

    @Nested
    @DisplayName("obtenerPorDescripcion - casos inválidos")
    class ObtenerPorDescripcionInvalidos {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("debe lanzar excepción si la descripción es null, vacía o solo espacios")
        void conDescripcionVacia_debeLanzarExcepcion(String descripcion) {
            DatoInvalidoException ex = assertThrows(
                    DatoInvalidoException.class,
                    () -> DiaSemana.obtenerPorDescripcion(descripcion)
            );
            assertEquals("El día es requerido", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Luness",
                "Martez",
                "Feriado",
                "Lunes1",
                "12345",
                "Día"
        })
        @DisplayName("debe lanzar excepción si la descripción no corresponde a ningún día")
        void conDescripcionDesconocida_debeLanzarExcepcion(String descripcion) {
            DatoInvalidoException ex = assertThrows(
                    DatoInvalidoException.class,
                    () -> DiaSemana.obtenerPorDescripcion(descripcion)
            );
            assertTrue(ex.getMessage().contains("No existe un día con la descripción"));
            assertTrue(ex.getMessage().contains(descripcion));
        }
    }
}