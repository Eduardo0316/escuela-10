package com.eduardo.escuela.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import com.eduardo.escuela.exceptions.RecursoNoEncontradoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias de la entidad Inscripcion")
class InscripcionTest {

    private Alumno alumno;
    private Grupo grupo;

    @BeforeEach
    void setUp() {
        alumno = mock(Alumno.class);
        grupo = mock(Grupo.class);
    }

    // ============================================================
    // HAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe construir una Inscripcion correctamente con datos válidos")
    void crear_conDatosValidos_debeConstruirInscripcion() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        assertNotNull(inscripcion);
        assertEquals(alumno, inscripcion.getAlumno());
        assertEquals(grupo, inscripcion.getGrupo());
    }

    @Test
    @DisplayName("crear: debe inicializar la fecha de inscripción con la fecha actual")
    void crear_debeInicializarFechaInscripcionConFechaActual() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        assertNotNull(inscripcion.getFechaInscripcion());
        assertEquals(LocalDate.now(), inscripcion.getFechaInscripcion());
    }

    @Test
    @DisplayName("crear: la calificación debe inicializarse en null")
    void crear_debeInicializarCalificacionNull() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        assertNull(inscripcion.getCalificacion());
    }

    // ============================================================
    // HAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe modificar alumno y grupo correctamente")
    void actualizar_conDatosValidos_debeActualizarCampos() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        Alumno nuevoAlumno = mock(Alumno.class);
        Grupo nuevoGrupo = mock(Grupo.class);

        inscripcion.actualizar(nuevoAlumno, nuevoGrupo);

        assertEquals(nuevoAlumno, inscripcion.getAlumno());
        assertEquals(nuevoGrupo, inscripcion.getGrupo());
    }

    @Test
    @DisplayName("actualizar: no debe alterar la fecha de inscripción ni la calificación")
    void actualizar_noDebeAlterarFechaNiCalificacion() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);
        LocalDate fechaOriginal = inscripcion.getFechaInscripcion();

        inscripcion.actualizar(mock(Alumno.class), mock(Grupo.class));

        assertEquals(fechaOriginal, inscripcion.getFechaInscripcion());
        assertNull(inscripcion.getCalificacion());
    }

    // ============================================================
    // UNHAPPY PATHS - crear()
    // ============================================================

    @Test
    @DisplayName("crear: debe lanzar excepción si el alumno es null")
    void crear_conAlumnoNull_debeLanzarExcepcion() {
        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> Inscripcion.crear(null, grupo)
        );
        assertEquals("El curso, el maestro y el aula son requeridos", ex.getMessage());
    }

    @Test
    @DisplayName("crear: debe lanzar excepción si el grupo es null")
    void crear_conGrupoNull_debeLanzarExcepcion() {
        assertThrows(
                RecursoNoEncontradoException.class,
                () -> Inscripcion.crear(alumno, null)
        );
    }

    // ============================================================
    // UNHAPPY PATHS - actualizar()
    // ============================================================

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el alumno es null")
    void actualizar_conAlumnoNull_debeLanzarExcepcion() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> inscripcion.actualizar(null, grupo)
        );
    }

    @Test
    @DisplayName("actualizar: debe lanzar excepción si el grupo es null")
    void actualizar_conGrupoNull_debeLanzarExcepcion() {
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> inscripcion.actualizar(alumno, null)
        );
    }
}