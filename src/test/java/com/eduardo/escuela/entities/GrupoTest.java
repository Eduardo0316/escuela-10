package com.eduardo.escuela.entities;

import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class GrupoTest {

    private Curso cursoMock;
    private Maestro maestroMock;
    private Aula aulaMock;

    @BeforeEach
    void setUp() {
        cursoMock = Mockito.mock(Curso.class);
        maestroMock = Mockito.mock(Maestro.class);
        aulaMock = Mockito.mock(Aula.class);
    }

    @Nested
    @DisplayName("Pruebas para el método crear()")
    class CrearTests {

        @Test
        @DisplayName("Happy Path: Crea un grupo exitosamente con relaciones y periodo válidos")
        void crearGrupo_Exitoso() {
            String periodoValido = "2026-1";

            Grupo grupo = Grupo.crear(cursoMock, maestroMock, aulaMock, periodoValido);

            assertNotNull(grupo);
            assertEquals(cursoMock, grupo.getCurso());
            assertEquals(maestroMock, grupo.getMaestro());
            assertEquals(aulaMock, grupo.getAula());
            assertEquals("2026-1", grupo.getPeriodo());
            assertNotNull(grupo.getHorarios());
            assertTrue(grupo.getHorarios().isEmpty());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si alguna relación (curso, maestro o aula) es nula")
        void crearGrupo_RelacionNula_LanzaRecursoNoEncontradoException() {
            RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () ->
                    Grupo.crear(cursoMock, null, aulaMock, "2026-1")
            );

            assertEquals("El curso, el maestro y el aula son requeridos", ex.getMessage());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si el periodo excede los 20 caracteres o es inválido")
        void crearGrupo_PeriodoInvalido_LanzaExcepcion() {
            String periodoLargo = "A".repeat(21);

            assertThrows(Exception.class, () ->
                    Grupo.crear(cursoMock, maestroMock, aulaMock, periodoLargo)
            );
        }
    }

    @Nested
    @DisplayName("Pruebas para el método actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Happy Path: Actualiza correctamente las relaciones y el periodo")
        void actualizarGrupo_Exitoso() {
            Grupo grupo = Grupo.crear(cursoMock, maestroMock, aulaMock, "2026-1");

            Curso nuevoCursoMock = Mockito.mock(Curso.class);
            Maestro nuevoMaestroMock = Mockito.mock(Maestro.class);
            Aula nuevaAulaMock = Mockito.mock(Aula.class);
            String nuevoPeriodo = "2026-2";

            grupo.actualizar(nuevoCursoMock, nuevoMaestroMock, nuevaAulaMock, nuevoPeriodo);

            assertEquals(nuevoCursoMock, grupo.getCurso());
            assertEquals(nuevoMaestroMock, grupo.getMaestro());
            assertEquals(nuevaAulaMock, grupo.getAula());
            assertEquals("2026-2", grupo.getPeriodo());
        }

        @Test
        @DisplayName("Unhappy Path: Falla si se intenta actualizar dejando el curso en null")
        void actualizarGrupo_CursoNulo_LanzaRecursoNoEncontradoException() {
            Grupo grupo = Grupo.crear(cursoMock, maestroMock, aulaMock, "2026-1");

            assertThrows(RecursoNoEncontradoException.class, () ->
                    grupo.actualizar(null, maestroMock, aulaMock, "2026-2")
            );

            // Confirma la integridad del estado anterior
            assertEquals(cursoMock, grupo.getCurso());
            assertEquals("2026-1", grupo.getPeriodo());
        }
    }
}