package com.eduardo.escuela.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias de la entidad Curso")
class CursoTest {

    private String nombreValido;
    private String descripcionValida;
    private Integer creditosValidos;

    @BeforeEach
    void setUp() {
        nombreValido = "Matemáticas";
        descripcionValida = "Curso de álgebra básica";
        creditosValidos = 5;
    }

    // ============================================================
    // HAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe construir un Curso correctamente con datos válidos")
    void crear_conDatosValidos_debeConstruirCurso() {
        Curso curso = Curso.crear(nombreValido, descripcionValida, creditosValidos);

        assertNotNull(curso);
        assertEquals("Matemáticas", curso.getNombre());
        assertEquals("Curso de álgebra básica", curso.getDescripcion());
        assertEquals(5, curso.getCreditos());
    }

    @Test
    @DisplayName("crear: debe recortar espacios en blanco de nombre y descripción")
    void crear_conEspaciosEnBlanco_debeRecortarlos() {
        Curso curso = Curso.crear("  Física  ", "  Mecánica clásica  ", 4);

        assertEquals("Física", curso.getNombre());
        assertEquals("Mecánica clásica", curso.getDescripcion());
    }

    @Test
    @DisplayName("crear: debe permitir descripción null y dejarla como null")
    void crear_conDescripcionNull_debeDejarlaNull() {
        Curso curso = Curso.crear(nombreValido, null, creditosValidos);

        assertNull(curso.getDescripcion());
    }

    @Test
    @DisplayName("crear: debe convertir descripción vacía en null")
    void crear_conDescripcionVacia_debeConvertirlaNull() {
        Curso curso = Curso.crear(nombreValido, "   ", creditosValidos);

        assertNull(curso.getDescripcion());
    }

    @Test
    @DisplayName("crear: debe aceptar nombre con longitud mínima (1 carácter)")
    void crear_conNombreMinimo_debeConstruirCurso() {
        Curso curso = Curso.crear("A", null, 1);

        assertEquals("A", curso.getNombre());
        assertEquals(1, curso.getCreditos());
    }

    @Test
    @DisplayName("crear: debe aceptar nombre con longitud máxima (100 caracteres)")
    void crear_conNombreMaximo_debeConstruirCurso() {
        String nombreMax = "a".repeat(100);
        Curso curso = Curso.crear(nombreMax, null, 10);

        assertEquals(nombreMax, curso.getNombre());
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe modificar todos los campos correctamente")
    void actualizar_conDatosValidos_debeActualizarCampos() {
        Curso curso = Curso.crear(nombreValido, descripcionValida, creditosValidos);

        curso.actualizar("Química", "  Orgánica  ", 6);

        assertEquals("Química", curso.getNombre());
        assertEquals("Orgánica", curso.getDescripcion());
        assertEquals(6, curso.getCreditos());
    }

    @Test
    @DisplayName("actualizar: debe permitir cambiar la descripción a null")
    void actualizar_conDescripcionNull_debeDejarlaNull() {
        Curso curso = Curso.crear(nombreValido, descripcionValida, creditosValidos);

        curso.actualizar(nombreValido, null, creditosValidos);

        assertNull(curso.getDescripcion());
    }

    // ============================================================
    // UNHAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe lanzar excepción si el nombre es null")
    void crear_conNombreNull_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Curso.crear(null, descripcionValida, creditosValidos)
        );
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el nombre está vacío")
    void crear_conNombreVacio_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Curso.crear("   ", descripcionValida, creditosValidos)
        );
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si los créditos son null")
    void crear_conCreditosNull_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Curso.crear(nombreValido, descripcionValida, null)
        );
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si los créditos son cero o negativos")
    void crear_conCreditosNoPositivos_debeLanzarExcepcion() {
        assertThrows(
                DatoInvalidoException.class,
                () -> Curso.crear(nombreValido, descripcionValida, 0)
        );
        assertThrows(
                DatoInvalidoException.class,
                () -> Curso.crear(nombreValido, descripcionValida, -3)
        );
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el nombre excede los 100 caracteres")
    void crear_conNombreDemasiadoLargo_debeLanzarExcepcion() {
        String nombreLargo = "a".repeat(101);

        assertThrows(
                DatoInvalidoException.class,
                () -> Curso.crear(nombreLargo, descripcionValida, creditosValidos)
        );
    }

    // ============================================================
    // UNHAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el nombre es null")
    void actualizar_conNombreNull_debeLanzarExcepcion() {
        Curso curso = Curso.crear(nombreValido, descripcionValida, creditosValidos);

        assertThrows(
                DatoInvalidoException.class,
                () -> curso.actualizar(null, descripcionValida, creditosValidos)
        );
    }

    @Test
    @DisplayName("actualizar: debe lanzar excepción si los créditos son negativos")
    void actualizar_conCreditosNegativos_debeLanzarExcepcion() {
        Curso curso = Curso.crear(nombreValido, descripcionValida, creditosValidos);

        assertThrows(
                DatoInvalidoException.class,
                () -> curso.actualizar(nombreValido, descripcionValida, -1)
        );
    }
}