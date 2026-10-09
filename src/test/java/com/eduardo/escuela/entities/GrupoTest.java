package com.eduardo.escuela.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.eduardo.escuela.exceptions.DatoInvalidoException;
import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias de la entidad Grupo")
class GrupoTest {

    private Curso curso;
    private Maestro maestro;
    private Aula aula;
    private String periodoValido;

    @BeforeEach
    void setUp() {
        // Mocks simples: la entidad Grupo solo verifica que no sean null,
        // no llama a ningún método de estas clases.
        curso = mock(Curso.class);
        maestro = mock(Maestro.class);
        aula = mock(Aula.class);
        periodoValido = "2024-1";
    }

    // ============================================================
    // HAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe construir un Grupo correctamente con datos válidos")
    void crear_conDatosValidos_debeConstruirGrupo() {
        Grupo grupo = Grupo.crear(curso, maestro, aula, periodoValido);

        assertNotNull(grupo);
        assertEquals(curso, grupo.getCurso());
        assertEquals(maestro, grupo.getMaestro());
        assertEquals(aula, grupo.getAula());
        assertEquals(periodoValido, grupo.getPeriodo());
        assertNotNull(grupo.getHorarios());
        assertTrue(grupo.getHorarios().isEmpty());
    }

    @Test
    @DisplayName("crear: debe aceptar un periodo con la longitud mínima permitida (1 carácter)")
    void crear_conPeriodoMinimo_debeConstruirGrupo() {
        Grupo grupo = Grupo.crear(curso, maestro, aula, "A");

        assertNotNull(grupo);
        assertEquals("A", grupo.getPeriodo());
    }

    @Test
    @DisplayName("crear: debe aceptar un periodo con la longitud máxima permitida (20 caracteres)")
    void crear_conPeriodoMaximo_debeConstruirGrupo() {
        String periodoMax = "12345678901234567890"; // 20 chars
        Grupo grupo = Grupo.crear(curso, maestro, aula, periodoMax);

        assertNotNull(grupo);
        assertEquals(periodoMax, grupo.getPeriodo());
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe modificar todos los campos correctamente")
    void actualizar_conDatosValidos_debeActualizarCampos() {
        Grupo grupo = Grupo.crear(curso, maestro, aula, "2024-1");

        Curso nuevoCurso = mock(Curso.class);
        Maestro nuevoMaestro = mock(Maestro.class);
        Aula nuevaAula = mock(Aula.class);
        String nuevoPeriodo = "2024-2";

        grupo.actualizar(nuevoCurso, nuevoMaestro, nuevaAula, nuevoPeriodo);

        assertEquals(nuevoCurso, grupo.getCurso());
        assertEquals(nuevoMaestro, grupo.getMaestro());
        assertEquals(nuevaAula, grupo.getAula());
        assertEquals(nuevoPeriodo, grupo.getPeriodo());
    }

    // ============================================================
    // UNHAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe lanzar excepción si el curso es null")
    void crear_conCursoNull_debeLanzarExcepcion() {
        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> Grupo.crear(null, maestro, aula, periodoValido)
        );
        assertEquals("El curso es requerido", ex.getMessage());
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el periodo está vacío")
    void crear_conPeriodoVacio_debeLanzarExcepcion() {
        DatoInvalidoException ex = assertThrows(
                DatoInvalidoException.class,
                () -> Grupo.crear(curso, maestro, aula, "")
        );
        assertTrue(ex.getMessage().contains("El periodo es requerido"));
    }

    // ============================================================
    // UNHAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el maestro es null")
    void actualizar_conMaestroNull_debeLanzarExcepcion() {
        Grupo grupo = Grupo.crear(curso, maestro, aula, periodoValido);

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> grupo.actualizar(curso, null, aula, "2024-2")
        );
        assertEquals("El maestro es requerido", ex.getMessage());
    }

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el periodo excede los 20 caracteres")
    void actualizar_conPeriodoDemasiadoLargo_debeLanzarExcepcion() {
        Grupo grupo = Grupo.crear(curso, maestro, aula, periodoValido);

        String periodoLargo = "123456789012345678901"; // 21 chars

        assertThrows(
                DatoInvalidoException.class,
                () -> grupo.actualizar(curso, maestro, aula, periodoLargo)
        );
    }
}