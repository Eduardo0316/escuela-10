package com.eduardo.escuela.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CursoTest {

    private Curso cursoBase;

    @BeforeEach
    void setUp() {
        cursoBase = Curso.crear("Matemáticas Discretas", "Curso introductorio", 5);
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea un curso correctamente con datos válidos y aplica trim")
        void crearCurso_Exitoso() {
            Curso curso = Curso.crear("  Física General ", "  Leyes de Newton  ", 4);

            assertNotNull(curso);
            assertEquals("Física General", curso.getNombre());
            assertEquals("Leyes de Newton", curso.getDescripcion());
            assertEquals(4, curso.getCreditos());
        }

        @Test
        @DisplayName("Happy Path: Permite crear un curso con descripción nula")
        void crearCurso_DescripcionNula_Exitoso() {
            Curso curso = Curso.crear("Química Orgánica", null, 3);

            assertNotNull(curso);
            assertEquals("Química Orgánica", curso.getNombre());
            assertNull(curso.getDescripcion());
            assertEquals(3, curso.getCreditos());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el nombre excede el tamaño permitido o es inválido")
        void crearCurso_NombreInvalido_LanzaExcepcion() {
            String nombreLargo = "A".repeat(101);

            assertThrows(Exception.class, () ->
                    Curso.crear(nombreLargo, "Descripción corta", 3)
            );
        }

        @Test
        @DisplayName("Unhappy Path: Falla si los créditos no son positivos o son nulos")
        void crearCurso_CreditosInvalidos_LanzaExcepcion() {
            assertThrows(Exception.class, () ->
                    Curso.crear("Estructuras de Datos", "Algoritmos básicos", 0)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza correctamente los datos del curso")
        void actualizarCurso_Exitoso() {
            cursoBase.actualizar("Álgebra Lineal", "Matrices y vectores", 6);

            assertEquals("Álgebra Lineal", cursoBase.getNombre());
            assertEquals("Matrices y vectores", cursoBase.getDescripcion());
            assertEquals(6, cursoBase.getCreditos());
        }

        @Test
        @DisplayName("Unhappy Path: Falla al actualizar con créditos negativos o cero")
        void actualizarCurso_CreditosNegativos_LanzaExcepcion() {
            assertThrows(Exception.class, () ->
                    cursoBase.actualizar("Matemáticas Discretas", "Curso introductorio", -2)
            );

            // Verifica que los valores originales no cambiaron tras la falla
            assertEquals("Matemáticas Discretas", cursoBase.getNombre());
            assertEquals(5, cursoBase.getCreditos());
        }
    }
}